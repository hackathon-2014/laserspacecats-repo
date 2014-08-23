
var fs = require('fs');
var path = require('path');
var util = require('util');
var mongoose = require('mongoose');
var restify = require('restify');

var models = require('./models');


///--- Errors
function FailedToSaveError(thing) {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'FailedToSave',
        message: 'failed to save '+ thing,
        constructorOpt: FailedToSaveError
    });

    this.name = 'FailedToSaveError';
}
util.inherits(FailedToSaveError, restify.RestError);

function MissingDestinationError() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'MissingDestination',
        message: 'you need a destination',
        constructorOpt: MissingDestinationError
    });

    this.name = 'MissingDestinationError';
}
util.inherits(MissingDestinationError, restify.RestError);

function MissingUserNameError() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'MissingUserName',
        message: 'you need a username',
        constructorOpt: MissingUserNameError
    });

    this.name = 'MissingUserNameError';
}
util.inherits(MissingUserNameError, restify.RestError);


///--- Formatters

/**
 * This is a nonsensical custom content-type 'application/todo', just to
 * demonstrate how to support additional content-types.  Really this is
 * the same as text/plain, where we pick out 'task' if available
 */
function formatToot(req, res, body) {
    if (body instanceof Error) {
        res.statusCode = body.statusCode || 500;
        body = body.message;
    } else if (typeof (body) === 'object') {
        body = body.task || JSON.stringify(body);
    } else {
        body = body.toString();
    }

    res.setHeader('Content-Length', Buffer.byteLength(body));
    return (body);
}


///--- Handlers

/**
 * Only checks for HTTP Basic Authenticaion
 *
 * Some handler before is expected to set the accepted user/pass combo
 * on req as:
 *
 * req.allow = { user: '', pass: '' };
 *
 * Or this will be skipped.
 */
function authenticate(req, res, next) {
    if (!req.allow) {
        req.log.debug('skipping authentication');
        next();
        return;
    }

    var authz = req.authorization.basic;
    if (!authz) {
        res.setHeader('WWW-Authenticate', 'Basic realm="tootapp"');
        next(new restify.UnauthorizedError('authentication required'));
        return;
    }

    if (authz.username !== req.allow.user || authz.password !== req.allow.pass) {
        next(new restify.ForbiddenError('invalid credentials'));
        return;
    }

    next();
}


///--- API
/**
 * Note this handler looks in `req.params`, which means we can load request
 * parameters in a "mixed" way, like:
 *
 * POST /toot?name=foo HTTP/1.1
 * Host: localhost
 * Content-Type: application/json
 * Content-Length: ...
 *
 * {"destination": "Whitney"}
 *
 * Which would have `destination` and `origin` available in req.params
 */
function sendToot(req, res, next) {
    if (!req.params.destination) {
        req.log.warn('Missing Destination');
        next(new MissingDestinationError());
        return;
    }

    var toot = new models.Toot(
        { 
            origin: req.params.origin,
            destination: req.params.destination,
            classification: 'arrival',
            eta: req.params.eta
        }
    );

    toot.save(function (err, fluffy) {
        if (err) {
            return console.error(err);
        } else {
            req.log.debug({toot: toot}, 'createToot: done');
            res.send(201, toot);
            next();
        }
    }); 
}

function sendOTW(req, res, next) {
    if (!req.params.destination) {
        req.log.warn({params: p}, 'createToot: missing destination');
        next(new MissingDestination());
        return;
    }

    var toot = new models.Toot(
        { 
            origin: req.params.origin,
            destination: req.params.destination,
            classification: 'otw',
            eta: req.params.eta
        }
    );

    toot.save(function (err, fluffy) {
        if (err) return console.error(err);
    }); 
}

function getUser(req, res, next) {
    var user = models.User.findOne({username: req.params.username}, function(err,obj) { 
        req.log.debug({user: user}, 'getUser: done');
        res.send(201, user);
        next();
    });
}

function createUser(req, res, next) {
    if (!req.params.name) {
        req.log.warn({params: p}, 'createUser: missing name');
        next(new MissingUserNameError());
        return;
    }

    var user = new models.User(
        { 
            username: req.params.username,
            password: req.params.password,
            registrationId: 1,
            friends: []
        }
    );

    user.save(function (err, fluffy) {
        if (err) {
            req.log.warn('createUser: failed to save');
            next(new FailedToSaveError());
            return;
        } else {
            req.log.debug({user: user}, 'createUser: done');
            res.send(201, user);
            next();
        }
    }); 
}

function updateUser(req, res, next) {
    
}

function deleteUser(req, res, next) {
    
}

function deleteAllUsers(req, res, next) {
    models.User.remove({}, function (err) {
        if (err) return handleError(err);
        // removed!
    });
}

function login(req, res, next) {
    
}

/**
 * Returns a server with all routes defined on it
 */
function createServer(options) {

    // Create a server with our logger and custom formatter
    // Note that 'version' means all routes will default to
    // 1.0.0
    var server = restify.createServer({
        log: options.log,
        name: 'tootapp',
        version: '1.0.0'
    });

    // Ensure we don't drop data on uploads
    server.pre(restify.pre.pause());

    // Clean up sloppy paths like //todo//////1//
    server.pre(restify.pre.sanitizePath());

    // Handles annoying user agents (curl)
    server.pre(restify.pre.userAgentConnection());

    // Set a per request bunyan logger (with requestid filled in)
    server.use(restify.requestLogger());

    // Allow 5 requests/second by IP, and burst to 10
    server.use(restify.throttle({
        burst: 10,
        rate: 5,
        ip: true,
    }));

    // Use the common stuff you probably want
    server.use(restify.acceptParser(server.acceptable));
    server.use(restify.dateParser());
    server.use(restify.authorizationParser());
    server.use(restify.queryParser());
    server.use(restify.gzipResponse());
    server.use(restify.bodyParser());

    // Now our own handlers for authentication/authorization
    // Here we only use basic auth, but really you should look
    // at https://github.com/joyent/node-http-signature
    server.use(function setup(req, res, next) {
        req.dir = options.directory;
        if (options.user && options.password) {
            req.allow = {
                user: options.user,
                password: options.password
            };
        }
        next();
    });
    server.use(authenticate);

    /// Now the real handlers. Here we just CRUD on TODO blobs

    server.post('/message/otw', sendOTW);
    server.post('/message/toot', sendToot);
  
    server.post('/login', login)

    // Return a TODO by name

    server.get('/user/:name', getUser);
    server.post('/user', createUser);
    server.put('/user', updateUser);
    server.del('/user/:name', deleteUser);
    server.del('/user/removeAll', deleteAllUsers);


    // Register a default '/' handler

    server.get('/', function root(req, res, next) {
        var routes = [
            'POST    /message/otw',
            'POST    /message/toot',
            'GET     /user',
            'PUT     /user',
            'POST    /user',
            'GET     /user/removeAll',
            'POST    /login'
        ];
        res.send(200, routes);
        next();
    });

    return (server);
}


///--- Exports

module.exports = {
    createServer: createServer
};
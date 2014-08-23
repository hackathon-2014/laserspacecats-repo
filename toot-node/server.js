
var fs = require('fs');
var path = require('path');
var util = require('util');
var mongoose = require('mongoose');

var restify = require('restify');


///--- Errors

function badMessage() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'BadMessage',
        message: 'your message is bad',
        constructorOpt: BadMessageError
    });

    this.name = 'BadMessageError';
}
util.inherits(BadMessageError, restify.RestError);


///--- Formatters

/**
 * This is a nonsensical custom content-type 'application/todo', just to
 * demonstrate how to support additional content-types.  Really this is
 * the same as text/plain, where we pick out 'task' if available
 */
function formatTodo(req, res, body) {
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


/**
 * Note this handler looks in `req.params`, which means we can load request
 * parameters in a "mixed" way, like:
 *
 * POST /todo?name=foo HTTP/1.1
 * Host: localhost
 * Content-Type: application/json
 * Content-Length: ...
 *
 * {"task": "get milk"}
 *
 * Which would have `name` and `task` available in req.params
 */
function createToot(req, res, next) {
    if (!req.params.task) {
        req.log.warn({params: p}, 'createTodo: missing task');
        next(new MissingTaskError());
        return;
    }

    var toot = {
        name: req.params.name || req.params.task.replace(/\W+/g, '_'),
        task: req.params.task
    };

    if (req.todos.indexOf(todo.name) !== -1) {
        req.log.warn('%s already exists', todo.name);
        next(new TodoExistsError(todo.name));
        return;
    }

    var p = path.normalize(req.dir + '/' + todo.name);
    fs.writeFile(p, JSON.stringify(todo), function (err) {
        if (err) {
            req.log.warn(err, 'createTodo: unable to save');
            next(err);
        } else {
            req.log.debug({todo: todo}, 'createTodo: done');
            res.send(201, todo);
            next();
        }
    });
}


/**
 * Deletes a TODO by name
 */
function deleteTodo(req, res, next) {
    fs.unlink(req.todo, function (err) {
        if (err) {
            req.log.warn(err,
                'deleteTodo: unable to unlink %s',
                req.todo);
            next(err);
        } else {
            res.send(204);
            next();
        }
    });
}


/**
 * Deletes all TODOs (in parallel)
 */
function deleteAll(req, res, next) {
    var done = 0;

    // Note this is safe, as restify ensures "next" is called
    // only once
    function cb(err) {
        if (err) {
            req.log.warn(err, 'unable to delete a TODO');
            next(err);
        } else if (++done === req.todos.length) {
            next();
        }
    }

    if (req.todos.length === 0) {
        next();
        return;
    }

    req.todos.forEach(function (t) {
        var p = req.dir + '/' + t;
        fs.unlink(p, cb);
    });
}


/**
 * Loads a TODO by name
 *
 * Requires `loadTodos` to have run.
 *
 * Note this function uses streaming, as that seems to come up a lot
 * on the mailing list and issue board.  restify lets you use the HTTP
 * objects as they are in "raw" node.
 *
 * Note by using the "raw" node APIs, you'll need to handle content
 * negotiation yourself.
 *
 */
function getTodo(req, res, next) {
    if (req.accepts('json')) {
        var fstream = fs.createReadStream(req.todo);

        fstream.once('open', function onOpen() {
            res.setHeader('Content-Type', 'application/json');
            res.writeHead(200);
            fstream.pipe(res);
            fstream.once('end', next);
        });

        // Really, you'd want to disambiguate the error code
        // from 'err' here to return 403, 404, etc., but this
        // is just a demo, so you get a 500
        fstream.once('error', next);
        return;
    }

    fs.readFile(req.todo, 'utf8', function (err, data) {
        if (err) {
            req.log.warn(err, 'get: unable to read %s', req.todo);
            next(err);
            return;
        }

        res.send(200, JSON.parse(data));
        next();
    });
}


/**
 * Replaces a TODO completely
 */
function putTodo(req, res, next) {
    if (!req.params.task) {
        req.log.warn({params: req.params}, 'putTodo: missing task');
        next(new MissingTaskError());
        return;
    }

    // Force the name to be what we sent this to
    var todo = {
        name: req.params.name,
        task: req.params.task
    };

    fs.writeFile(req.todo, JSON.stringify(req.body), function (err) {
        if (err) {
            req.log.warn(err, 'putTodo: unable to save');
            next(err);
        } else {
            req.log.debug({todo: req.body}, 'putTodo: done');
            res.send(204);
            next();
        }
    });
}


///--- API

/**
 * Returns a server with all routes defined on it
 */
function createServer(options) {
    assert.object(options, 'options');
    assert.string(options.directory, 'options.directory');
    assert.object(options.log, 'options.log');

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


    // Register a default '/' handler

    server.get('/', function root(req, res, next) {
        var routes = [
            'POST    /message/otw',
            'POST    /message/toot',
            'GET     /user',
            'PUT     /user',
            'POST    /user'
            'POST    /login'
        ];
        res.send(200, routes);
        next();
    });

    // Setup an audit logger
    if (!options.noAudit) {
        server.on('after', restify.auditLogger({
            body: true,
            log: bunyan.createLogger({
                level: 'info',
                name: 'todoapp-audit',
                stream: process.stdout
            })
        }));
    }

    return (server);
}


///--- Exports

module.exports = {
    createServer: createServer
};
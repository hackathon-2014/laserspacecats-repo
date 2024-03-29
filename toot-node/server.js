
var path = require('path');
var util = require('util');
var gcmService = require('./services/gcmService');
var mongoose = require('mongoose');
var restify = require('restify');
var models = require('./models');
var async = require('async');
var _ = require('underscore');


///--- Errors
function FailedToSendTootError() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'FailedToSendToot',
        message: 'failed to send toot',
        constructorOpt: FailedToSendTootError
    });

    this.name = 'FailedToSendTootError';
}
util.inherits(FailedToSendTootError, restify.RestError);

function FailedToSaveError() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'FailedToSave',
        message: 'failed to save',
        constructorOpt: FailedToSaveError
    });

    this.name = 'FailedToSaveError';
}
util.inherits(FailedToSaveError, restify.RestError);

function FailedToLoadError() {
    restify.RestError.call(this, {
        statusCode: 409,
        restCode: 'FailedToLoad',
        message: 'failed to load user',
        constructorOpt: FailedToLoadError
    });
    this.name = 'FailedToLoadError';
}
util.inherits(FailedToLoadError, restify.RestError);

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

///--- API
function sendToot(req, res, next) {
    if (!req.headers.id) {
        req.log.warn('Missing Destination');
        next(new MissingDestinationError());
        return;
    }

    models.User.findOne({_id: req.headers.id}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            return;
        } else {
            var toot = new models.Toot(
                { 
                    origin: req.headers.origin,
                    destination: req.headers.id,
                    classification: req.headers.classification,
                    eta: req.headers.eta
                }
            );
            gcmService.sendMessage(obj.registrationId, obj.username, toot, function callback(err, data) {
                if(err) {
                    req.log.warn(err, 'failed to send toot');
                    res.send(400, toot);
                    next(new FailedToSendTootError());
                    return;
                } else {
                    toot.save(function (err, fluffy) {
                        if (err) {
                            req.log.debug({error: err}, 'sendToot: failure');
                            res.send(400, toot);
                            next();
                        } else {
                            req.log.debug({toot: toot}, 'sendToot: success');
                            res.send(200, toot);
                            next();
                        }
                    }); 
                }
            });
        }
    });
}

function getUser(req, res, next) {
    models.User.findOne({_id: req.params.id}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            req.log.debug({user: obj}, 'getUser: done');
            res.send(200, obj);
            next();
        }
    });
}

function userExists(req, res, next) {
    models.User.findOne({username: req.params.name}, function(err,obj) {
        if (obj) {
            req.log.debug({user: obj}, 'getUser: done');
            res.send(200, true);
            next();
        } else {
            res.send(400, false);
            return;
        }
    });
}

function getFriends(req, res, next) {
    models.User.findOne({username: req.params.name}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            var friends = [];
            var loadFriend = function(friend, doneCallback) {
                models.User.findOne({_id: friend}, function(err,obj) { 
                    if (err) {
                        req.log.warn(err, 'getFriend: failed to load friend');
                        next(new FailedToLoadError());
                        res.send(400, obj);
                        return doneCallback("FAIL");
                    } else {
                        req.log.debug({friend: obj}, 'getFriend: done');
                        friends.push(obj);
                        return doneCallback(null);
                    }
                }); 
            }
            async.each(obj.friends, loadFriend, function(err) {
                res.send(200, friends);
                next();
            });     
        }
    });
}

function createUser(req, res, next) {
    if (!req.params.username) {
        req.log.warn('createUser: missing name');
        next(new MissingUserNameError());
        return;
    }

    var user = new models.User(
        { 
            username: req.params.username,
            password: req.params.password,
            registrationId: req.params.registrationId,
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
            res.send(200, user);
            next();
        }
    }); 
}

function updateUser(req, res, next) {
    models.User.findOne({_id: req.params.id}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            if(req.params.password) {
                obj.password = req.params.password;
            }
            if(req.params.registrationId) {
                obj.registrationId = req.params.registrationId;
            }
            if(req.params.friends) {
                obj.friends = req.params.friends;
            }
            obj.save(function (err, fluffy) {
                if (err) {
                    req.log.warn('updateUser: failed to save');
                    res.send(400, obj);
                    next(new FailedToSaveError());
                    return;
                } else {
                    req.log.debug({user: obj}, 'updateUser: done');
                    res.send(200, obj);
                    next();
                }
            }); 
        }
    });
}

function getAllUsers(req, res, next) {
    models.User.find({}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load users');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            req.log.debug({user: obj}, 'getUsers: done');
            var users = {
                users: obj
            }
            res.send(200, users);
            next();
        }
    });
}

function addFriends(req, res, next) {
    models.User.findOne({_id: req.params.id}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            obj.friends.push(req.params.friends);
            obj.friends = _.uniq(obj.friends);
            obj.save(function (err, fluffy) {
                if (err) {
                    req.log.warn('addFriends: failed to add friends');
                    res.send(400, obj);
                    next(new FailedToSaveError());
                    return;
                } else {
                    req.log.debug({user: obj}, 'addFriends: done');
                    res.send(200, obj);
                    next();
                }
            }); 
        }
    });
}

function removeFriend(req, res, next) {
    models.User.findOne({_id: req.params.id}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            var index = obj.friends.indexOf(req.params.friend);
            if (index > -1) {
                obj.friends.splice(index, 1);
            }
            obj.save(function (err, fluffy) {
                if (err) {
                    req.log.warn('removeFriend: failed to remove friend');
                    res.send(400, obj);
                    next(new FailedToSaveError());
                    return;
                } else {
                    req.log.debug({user: obj}, 'removeFriend: done');
                    res.send(200, obj);
                    next();
                }
            }); 
        }
    });
}

function deleteUser(req, res, next) {
    
}

function deleteAllUsers(req, res, next) {
    models.User.remove({}, function (err) {
        if (err) return handleError(err);
        // removed!
    });
}

function authenticateUser(req, res, next) {

    models.User.findOne({username: req.params.username}, function(err,obj) { 
        if (err) {
            req.log.warn(err, 'getUser: failed to load user');
            next(new FailedToLoadError());
            res.send(400, obj);
            return;
        } else {
            if(obj && obj.password == req.params.password) {
                req.log.debug({user: obj}, 'authenticated: true');
                res.send(200, obj);
                next();
            } else {
                next(new restify.ForbiddenError('invalid credentials'));
                return;
            }
        }
    });
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
        ip: true
    }));

    // Use the common stuff you probably want
    server.use(restify.acceptParser(server.acceptable));
    server.use(restify.dateParser());
    server.use(restify.authorizationParser());
    server.use(restify.queryParser());
    server.use(restify.gzipResponse());
    server.use(restify.bodyParser());

    /// Now the real handlers. Here we just CRUD on TODO blobs
    server.post('/message/toot', sendToot);
    server.get('/user/:name', getUser);
    server.post('/user', createUser);
    server.put('/user', updateUser);
    server.del('/user/:name', deleteUser);
    server.get('/user/:name/exists', userExists);
    server.del('/user/removeAll', deleteAllUsers);
    server.get('/users', getAllUsers);
    server.get('/user/:name/friends', getFriends);
    server.post('/user/addFriends', addFriends);
    server.post('/user/authenticate', authenticateUser);
    server.post('/user/removeFriend', removeFriend);

    // Register a default '/' handler

    server.get('/', function root(req, res, next) {
        var routes = [
            'POST    /message/toot',
            'GET     /user/:name',
            'GET     /testGCM',
            'PUT     /user',
            'POST    /user',
            'GET     /users',
            'POST    /user/authenticate',
            'GET     /user/:name/friends',
            'POST    /user/addFriends',
            'GET     /user/:name/exists'
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
var path = require('path');
var util = require('util');
var restify = require('restify');
var getopt = require('posix-getopt');
var bunyan = require('bunyan');
var mongoose = require('mongoose');

var tootApp = require('./server');
var models = require('./models');




///--- Globals

var NAME = 'tootApp';

// In true UNIX fashion, debug messages go to stderr, and audit records go
// to stdout, so you can split them as you like in the shell
var LOG = bunyan.createLogger({
    name: NAME,
    streams: [
        {
            level: (process.env.LOG_LEVEL || 'info'),
            stream: process.stderr
        },
        {
            // This ensures that if we get a WARN or above all debug records
            // related to that request are spewed to stderr - makes it nice
            // filter out debug messages in prod, but still dump on user
            // errors so you can debug problems
            level: 'debug',
            type: 'raw',
            stream: new restify.bunyan.RequestCaptureStream({
                level: bunyan.WARN,
                maxRecords: 100,
                maxRequestIds: 1000,
                stream: process.stderr
            })
        }
    ],
    serializers: restify.bunyan.serializers
});

///--- Helpers

/**
 * Standard POSIX getopt-style options parser.
 *
 * Some options, like directory/user/port are pretty cut and dry, but note
 * the 'verbose' or '-v' option afflicts the log level, repeatedly. So you
 * can run something like:
 *
 * And the log level will be set to TRACE.
 */
function parseOptions() {
    var option;
    var opts = {}
    var parser = new getopt.BasicParser('hvd:p:u:z:', process.argv);

    while ((option = parser.getopt()) !== undefined) {
        switch (option.option) {
            case 'd':
                opts.directory = path.normalize(option.optarg);
                break;

            case 'h':
                usage();
                break;

            case 'p':
                opts.port = parseInt(option.optarg, 10);
                break;

            case 'u':
                opts.user = option.optarg;
                break;

            case 'v':
                // Allows us to set -vvv -> this little hackery
                // just ensures that we're never < TRACE
                LOG.level(Math.max(bunyan.TRACE, (LOG.level() - 10)));
                if (LOG.level() <= bunyan.DEBUG)
                    LOG = LOG.child({src: true});
                break;

            case 'z':
                opts.password = option.optarg;
                break;

            default:
                usage('invalid option: ' + option.option);
                break;
        }
    }

    return (opts);
}


function usage(msg) {
    if (msg)
        console.error(msg);

    var str = 'usage: ' +
        NAME +
        ' [-v] [-d dir] [-p port] [-u user] [-z password]';
    console.error(str);
    process.exit(msg ? 1 : 0);
}


///--- Mainline

(function main() {
    var options = parseOptions();

    LOG.debug(options, 'command line arguments parsed');

    // First setup our database
    // Database Init
//    mongoose.connect('mongodb://localhost/test');
    mongoose.connect('mongodb://54.86.59.200:27017');
    var db = mongoose.connection;
    db.on('error', console.error.bind(console, 'connection error:'));
    db.once('open', function callback () {
        LOG.info("DB Connection Open");
    });

    var server = tootApp.createServer({
        log: LOG
    });

    // At last, let's rock and roll
    server.listen((options.port || 8080), function onListening() {
        LOG.info('listening at %s', server.url);
    });
})();
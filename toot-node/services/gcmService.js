var gcm = require('node-gcm');
var mongoose = require('mongoose');
var gcmService = {};

gcmService.sendMessage = function sendMessage(regId, callback) {

    var message = new gcm.Message();
    var sender = new gcm.Sender('AIzaSyDAA_igAhJYYSym4rv0127lkVpWfL9tCHg');
    var registrationIds = [];

    message.addData('title','Toot');
    message.addData('message','Im outside');
    message.addData('msgcnt','1');
    message.collapseKey = 'demo';
    message.delayWhileIdle = true;
    message.timeToLive = 3;

    // At least one token is required - each app will register a different token
    registrationIds.push(regId);

    /**
     * Parameters: message-literal, registrationIds-array, No. of retries, callback-function
     */
    sender.send(message, registrationIds, 4, function (result) {
        if(result === 401) {
            callback("ERROR", null);
        }
        
        console.log(result);
    });
    /** Use the following line if you want to send the message without retries
     sender.sendNoRetry(message, registrationIds, function (result) { console.log(result); });
     **/
    callback(null, "Success");
}

module.exports = gcmService;

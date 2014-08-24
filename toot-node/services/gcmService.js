var gcm = require('node-gcm');
var mongoose = require('mongoose');
var gcmService = {};

gcmService.sendMessage = function sendMessage(regId, userName, toot, callback) {

    var message = new gcm.Message();
    //sparc
//    var sender = new gcm.Sender('AIzaSyDAA_igAhJYYSym4rv0127lkVpWfL9tCHg');

    //prod
    var sender = new gcm.Sender('AIzaSyBAyevhUYFImGA8NjvMoztM9JRFfFAP46c');

    var registrationIds = [];

    if(toot.classification === 'arrival') {
        message.addData('message','I\'m outside');
    } else if (toot.classification === 'otw') {
        message.addData('message','I\'m on the way');
    } else if (toot.classification === 'beer') {
        message.addData('message','Bring Beer!!');
    }
    message.addData('title',userName);
//    message.addData('from', userName);
    message.addData('msgcnt','10');
    message.delayWhileIdle = true;
    message.timeToLive = 3;

    // At least one token is required - each app will register a different token
    registrationIds.push(regId);

    /**
     * Parameters: message-literal, registrationIds-array, No. of retries, callback-function
     */
    sender.send(message, registrationIds, 4, function (result) {
        if(result === 401 || result === 400) {
            callback("ERROR", null);
            console.log("toot error");
        } else {
            console.log("toot sent");
        }
    });
    /** Use the following line if you want to send the message without retries
     sender.sendNoRetry(message, registrationIds, function (result) { console.log(result); });
     **/
    callback(null, "Success");
}

module.exports = gcmService;

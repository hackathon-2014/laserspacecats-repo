var gcm = require('node-gcm');
var mongoose = require('mongoose');
var gcmService = {};

gcmService.sendMessage = function sendMessage() {

    var message = new gcm.Message();
    var sender = new gcm.Sender('AIzaSyC7lhymxsMxVtRXJZME0Sjz4cRML-eHKC0');
    var registrationIds = [];

    message.addData('title','Toot');
    message.addData('message','Im outside');
    message.addData('msgcnt','1');
    message.collapseKey = 'demo';
    message.delayWhileIdle = true;
    message.timeToLive = 3;

    // At least one token is required - each app will register a different token
    registrationIds.push('APA91bFobAwN7P3Okxy2al8RI12VcJFUS-giXWTOoWXIObtSPOE1h7FuH1VPLBPgshDI_Fp7aIYVET-ssvGUErlWYA0cKPGhoXT1daqyDsEfem9ZtgZNRhQFv7kLCIVSigYlpMluToPiSHSsFSEdtCDfKoOZqNPgfs');

    /**
     * Parameters: message-literal, registrationIds-array, No. of retries, callback-function
     */
    sender.send(message, registrationIds, 4, function (result) {
        console.log(result);
    });
    /** Use the following line if you want to send the message without retries
     sender.sendNoRetry(message, registrationIds, function (result) { console.log(result); });
     **/
}

module.exports = gcmService;

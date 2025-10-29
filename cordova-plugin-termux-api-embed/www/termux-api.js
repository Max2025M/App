var exec = require('cordova/exec');

var TermuxAPI = {
  getSimInfo: function() {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, 'TermuxApi', 'getSimInfo', []);
    });
  },
  sendSMS: function(number, message, simIndex) {
    return new Promise((resolve, reject) => {
      exec(resolve, reject, 'TermuxApi', 'sendSMS', [number, message, simIndex]);
    });
  }
};

module.exports = TermuxAPI;

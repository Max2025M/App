var exec = require('cordova/exec');

exports.smsSend = function (options, success, error) {
  exec(success, error, 'TermuxApi', 'smsSend', [options]);
};

exports.telephonyInfo = function (success, error) {
  exec(success, error, 'TermuxApi', 'telephonyInfo', []);
};

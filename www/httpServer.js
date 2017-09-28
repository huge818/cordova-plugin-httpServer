	var exec = require('cordova/exec');

	exports.startServer = function(arg0, arg1, success, error) {
	    exec(success, error, "httpServer", "startServer", [arg0,arg1]);
	};

	exports.response = function(id, data, success, error) {
	    exec(success, error, "httpServer", "response", [id,data]);
	};

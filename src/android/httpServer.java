package httpServer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Map;
import java.util.logging.Logger;

import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.request.Method;
import org.nanohttpd.protocols.http.response.Response;
import org.nanohttpd.util.ServerRunner;


/**
 * This class echoes a string called from JavaScript.
 */
public class httpServer extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("startServer")) {
			String message = args.getString(0);
			this.coolMethod(message, callbackContext);
			return true;
		}
		if (action.equals("stopServer")) {
			String message = args.getString(0);
			this.coolMethod(message, callbackContext);
			return true;
		}

		return false;
	}

	private void startServer(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}

	private void stopServer(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error("Expected one non-empty string argument.");
		}
	}


	/**
	 * An example of subclassing NanoHTTPD to make a custom HTTP server.
	 */
	public class HelloServer extends NanoHTTPD {

	    /**
	     * logger to log to.
	     */
	    private static final Logger LOG = Logger.getLogger(HelloServer.class.getName());

	    public static void main(String[] args) {
	        ServerRunner.run(HelloServer.class);
	    }

	    public HelloServer() {
	        super(8080);
	    }

	    @Override
	    public Response serve(IHTTPSession session) {
	        Method method = session.getMethod();
	        String uri = session.getUri();
	        HelloServer.LOG.info(method + " '" + uri + "' ");

	        String msg = "<html><body><h1>Hello server</h1>\n";
	        Map<String, String> parms = session.getParms();
	        if (parms.get("username") == null) {
	            msg += "<form action='?' method='get'>\n" + "  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
	        } else {
	            msg += "<p>Hello, " + parms.get("username") + "!</p>";
	        }

	        msg += "</body></html>\n";

	        return Response.newFixedLengthResponse(msg);
	    }
	}

}

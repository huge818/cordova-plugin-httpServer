package httpServer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

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

import android.util.Log;
import java.util.UUID;
import android.util.Base64;
import java.util.HashMap;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream; 
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader; 
import java.io.OutputStream;
import java.io.PrintWriter;
import android.content.res.AssetManager;
import android.app.Activity;  
import android.os.Bundle;
import android.content.Intent;  
import android.content.Context;
/**
 * This class echoes a string called from JavaScript.
 */
public class httpServer extends CordovaPlugin {
	HelloServer webServer;
	HashMap<String, String> httpHashMap = new HashMap<String, String>();

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("startServer")) {
			int port= args.getInt(0);
			String webroot= args.getString(1);
			this.startServer(port, webroot, callbackContext);
			//httpHashMap = new HashMap<String, String>();
			return true;
		}
		else if (action.equals("stopServer")) {
			this.stopServer(callbackContext);
			return true;
		}
		else if (action.equals("response")) {
			String id= args.getString(0);
			String data= args.getString(1);
			this.response(id,data,callbackContext);
			return true;
		}
		return false;
	}

	private void startServer(int port, String webroot, CallbackContext callbackContext) {
		webServer = new HelloServer(port, webroot, callbackContext);
		try {
			webServer.start();
		} catch (Exception e) {
			//e.printStackTrace();
			callbackContext.error(e.getMessage());
			return;
		}
	}

	private void stopServer(CallbackContext callbackContext) {
		try {
			webServer.stop();
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return;
		}
	}

	private void response(String id, String data, CallbackContext callbackContext) {
		try {
			httpHashMap.put(id,data);
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return;
		}
	}

	public void sendPluginResult(CallbackContext callbackContext,JSONObject obj){
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
		pluginResult.setKeepCallback(true);
		callbackContext.sendPluginResult(pluginResult);
	}

	/**
	 * An example of subclassing NanoHTTPD to make a custom HTTP server.
	 */
	public class HelloServer extends NanoHTTPD {
		CallbackContext myCallbackContext;
		String asset_mgr="";
		public String wwwroot="";

		public HelloServer(int port, String webroot, CallbackContext callbackContext) {
			super(port);
			myCallbackContext=callbackContext;
			wwwroot=webroot;
		}

		@Override
		public Response serve(IHTTPSession session) {
			Method method = session.getMethod();
			String uri = session.getUri();
			Map<String, String> params = session.getParms();
			Map<String, String> headers = session.getHeaders();
			Map<String, String> files = new HashMap<String, String>();
			String uuid = UUID.randomUUID().toString();

			try{
				session.parseBody(files);
			} catch(Exception e){
				e.printStackTrace();
			}

			String file_name = uri.substring(1);
			// 默认的页面名称设定为index.html
			if(file_name.equalsIgnoreCase("")){
				file_name = "index.html";
			}

			String body = session.getQueryParameterString();
			JSONObject requestData = new JSONObject();
			try {
				
				requestData.put("uuid", uuid);
				requestData.put("uri", uri);
				requestData.put("body", body);
				requestData.put("params", params.toString());
				requestData.put("headers", headers.toString());
				if(Method.GET.equals(method)){
					requestData.put("method", "get");
				}
				else if(Method.PUT.equals(method)){
					requestData.put("method", "put");
				}
				else if(Method.POST.equals(method)){
					requestData.put("method", "post");
				}
				else if(Method.DELETE.equals(method)){
					requestData.put("method", "delete");
				}
			}
			catch(JSONException e){
				e.printStackTrace();
			}
			try{
				httpServer.this.sendPluginResult(this.myCallbackContext,requestData);
			} catch(Exception e){
				e.printStackTrace();
			}
			String result="";
			while (true) { //等待页面发来数据
				if(httpServer.this.httpHashMap.containsKey(uuid)){
					result=httpServer.this.httpHashMap.get(uuid);
					break;
				}
			}
			httpHashMap.remove(uuid);
			return Response.newFixedLengthResponse(result);
		}
	}

}

package controllers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import play.Logger;
import play.mvc.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TriviaIndex extends Controller {
	
	static String callback () {
	    return params.get("callback");
	}
    
	/**
	 * @param msg the error message to include in the response
	 * @return a hashmap with a JSON status => error response, and 
	 * a message => msg. */
	protected static HashMap<String, String> getErrorResponse (String msg) {
		HashMap<String, String> resp = new HashMap<String, String>();
		resp.put("status", "error");
		resp.put("message", msg);
		return resp;
	}
	
	/**
	 * @return a hashmap with a JSON status => okay response */
	protected static HashMap<String, String> getOkayResponse () {
		return getOkayResponse("okay");
	}

	/**
	 * @return a hashmap with a JSON status => okay response */
	protected static HashMap<String, String> getOkayResponse (String msg) {
		HashMap<String, String> resp = new HashMap<String, String>();
		resp.put("status", "okay");
		resp.put("message", msg);
		return resp;
	}

 	/**
 	 * renders a JSON status error response from a list of errors
 	 * @param callback optional, used for cross domain requests */
 	protected static void returnFailed (List<play.data.validation.Error> errors) {
 	    String msg = "";
        for (play.data.validation.Error err : errors) {
            msg += err.message();
        }
 	    Logger.warn(msg);
 		renderJSONP(
 			getErrorResponse(msg), 
 			new TypeToken<HashMap<String, String>>() {}.getType()
 		);
 	}

	/**
	 * renders a JSON status error response
	 * @param callback optional, used for cross domain requests */
	protected static void returnFailed (String msg) {
	    Logger.warn(msg);
		renderJSONP(
			getErrorResponse(msg), 
			new TypeToken<HashMap<String, String>>() {}.getType()
		);
	}

	/**
	 * renders a JSON status okay response
	 * @param callback optional, used for cross domain requests */
	protected static void returnOkay (String msg) {
		returnOkay(getOkayResponse(msg));
	}

    protected static void returnOkay () {
        returnOkay(getOkayResponse());
    }

	/**
	 * renders a JSON status okay response */
	protected static void returnOkay (HashMap<String, String> result) {
		renderJSONP(
			result, 
			new TypeToken<HashMap<String, String>>() {}.getType()
		);
	}
	
	protected static void returnJson(String json) {
		String callback = callback();
		if (callback == null || callback.equals("")) {
		    renderJSON(json);
		} else {
			json = callback + "(" + json + ")";
			response.contentType = "application/javascript";
			renderText(json);			
		}		
	}
	
	/**
	 * renders a JSON response, utilizing <code>callback</code> as necessary for
	 * cross domain requests
	 * @param myObj 	object to be JSONified
	 * @param t 		describes the type of the object to be JSONified */
	protected static void renderJSONP (Object myObj, Type t) {
		String json;
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		if (myObj instanceof String) {
		    json = (String)myObj;
		} else if (t != null) {
			json = gson.toJson(myObj, t); 
		} else {
			json = gson.toJson(myObj); 
		}
		returnJson(json);
	}
}

package controllers;

import java.util.*;
import java.lang.reflect .*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

import models.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

/**
 * A convenience class to hold common methods used
 * by the our controllers */
public class Index extends CRUD {
	public static Http.Request currentRequest;
	
	@Before
	protected static void setCurrent () {
		currentRequest = Http.Request.current();
	}
	
	public static Http.Request currentRequest () {
		return currentRequest;
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
		HashMap<String, String> resp = new HashMap<String, String>();
		resp.put("status", "okay");
		return resp;
	}

	/**
	 * renders a JSON status error response
	 * @param callback optional, used for cross domain requests */
	protected static void returnFailed (String msg, String callback) {
		Users.renderJSONP(
			getErrorResponse(msg), 
			new TypeToken<HashMap<String, String>>() {}.getType(),
			callback
		);
	}

	/**
	 * renders a JSON status okay response
	 * @param callback optional, used for cross domain requests */
	protected static void returnOkay (String callback) {
		Users.renderJSONP(
			getOkayResponse(), 
			new TypeToken<HashMap<String, String>>() {}.getType(),
			callback
		);
	}

	/**
	 * renders a JSON response, utilizing <code>callback</code> as necessary for
	 * cross domain requests
	 * @param myObj 	object to be JSONified
	 * @param t 		describes the type of the object to be JSONified
 	 * @param callback 	optional, used for cross domain requests */
	protected static void renderJSONP (Object myObj, Type t, String callback) {
		String json;
		GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new User.ChatExclusionStrategy());
		Gson gson = gsonBuilder.create();
		if (t != null) {
			json = gson.toJson(myObj, t); 
		} else {
			json = gson.toJson(myObj); 
		}
		if (callback != null && !callback.equals("")) {
			json = callback + "(" + json + ")";
			renderText(json);
		} else {
			renderJSON(json);
		}
	}
	
}


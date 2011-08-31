package controllers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import models.User;
import models.UserSession;
import play.Logger;
import play.mvc.Before;
import play.mvc.Catch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A convenience class to hold common methods used
 * by the our controllers */
public abstract class Index extends CRUD {
		
	@Before
    static void checkAuthenticated () {
        createSessionVariable("user_id", "session", "callerSession");
        createSessionVariable("for_user", "for_session", "forSession");
    }

    private static void createSessionVariable (String user_key, String session_key, String save_key) {
        
        Long user_id = params.get(user_key, Long.class);
        String session = params.get(session_key, String.class);
        boolean containsUserKey = user_id != null;
        boolean containsSessKey = session != null;
        
        if (containsUserKey && containsSessKey && user_id != -1) {
            
            UserSession sess = UserSession.getFor(user_id, session);
            if (sess == null) {
                returnFailed(user_id + " and " + session + " do not map to a valid session");
            } else {
                renderArgs.put(save_key, sess);
            }
            UserSession.Faux sessFaux = new UserSession.Faux(user_id, session);
            renderArgs.put(save_key + "_faux", sessFaux);    
        } else {
            renderArgs.put(save_key, null);
        }
    }

    static UserSession.Faux currentForFauxSession () {
        if(renderArgs.data.containsKey("forSession_faux")) {
            return renderArgs.get("forSession_faux", UserSession.Faux.class);
        }
        return null;
    }
    
    static UserSession currentForSession () {
        if(renderArgs.data.containsKey("forSession")) {
            return renderArgs.get("forSession", UserSession.class);
        }
        return null;
    }

    static UserSession.Faux currentFauxSession () {
        if(renderArgs.data.containsKey("callerSession_faux")) {
            return renderArgs.get("callerSession_faux", UserSession.Faux.class);
        }
        return null;
    }
    
    static UserSession currentSession () {
        if(renderArgs.data.containsKey("callerSession")) {
            return renderArgs.get("callerSession", UserSession.class);
        }
        return null;
    }
		
	/**
	 * This method is utilized by children to protected
	 * methods we need to authenticate for*/
    protected static void checkAuthentication() {
       	if (!Security.isConnected()) {
			try {
				Secure.login();
			} catch (Throwable e) {
				Logger.error("authentication excpetion %s", e);
			}
		}
    }
	
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
 		Users.renderJSONP(
 			getErrorResponse(msg), 
 			new TypeToken<HashMap<String, String>>() {}.getType()
 		);
 	}

	/**
	 * renders a JSON status error response
	 * @param callback optional, used for cross domain requests */
	protected static void returnFailed (String msg) {
	    Logger.warn(msg);
		Users.renderJSONP(
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
		Users.renderJSONP(
			result, 
			new TypeToken<HashMap<String, String>>() {}.getType()
		);
	}
	
	/**
	 * renders a JSON response, utilizing <code>callback</code> as necessary for
	 * cross domain requests
	 * @param myObj 	object to be JSONified
	 * @param t 		describes the type of the object to be JSONified */
	protected static void renderJSONP (Object myObj, Type t) {
		String json;
		GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new User.ChatExclusionStrategy());
		Gson gson = gsonBuilder.create();
		if (myObj instanceof String) {
		    json = (String)myObj;
		} else if (t != null) {
			json = gson.toJson(myObj, t); 
		} else {
			json = gson.toJson(myObj); 
		}
		String callback = callback();
		if (callback == null || callback.equals("")) {
		    renderJSON(json);
		} else {
			json = callback + "(" + json + ")";
			response.contentType = "application/javascript";
			renderText(json);			
		}
	}
	
}


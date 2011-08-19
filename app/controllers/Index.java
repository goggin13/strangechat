package controllers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;

import models.Server;
import models.User;
import models.UserSession;
import play.Logger;
import play.mvc.Before;
import play.mvc.Catch;
import play.libs.F.T2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * A convenience class to hold common methods used
 * by the our controllers */
public abstract class Index extends CRUD {


    protected static Timer myTimer = new Timer();
	protected static class Timer {
    	private static AbstractMap<String, T2<Long, Long>> timers = 
    	        new ConcurrentHashMap<String, T2<Long, Long>>();	    
    	
    	public void addToTimer (ATimer t) {
    	    T2<Long, Long> tuple;
    	    long count = 1;
    	    long totalTime = t.duration();
    	    String event = t.label;
    	    if (timers.containsKey(event)) {
    	        tuple = timers.get(event);
    	        count += tuple._1;
    	        totalTime += tuple._2;
    	    } 
            timers.put(event, new T2<Long, Long>(count, totalTime));
    	}        
    	
    	public AbstractMap<String, T2<Long, Long>> getTimers () {
    	    return timers;
    	} 
    	
    	public void flushTimers () {
    	    timers.clear();
    	}
	}
	
	public static class ATimer {
	    private long start;
	    private long end;
	    public String label;
	    
	    public ATimer (String l) {
	        this.label = l;
	        this.start = System.currentTimeMillis();
	    }
	    
	    public ATimer stop () {
	        this.end = System.currentTimeMillis();
	        myTimer.addToTimer(this);
	        return this;
	    }
	    
	    public long duration () {
            // System.out.println("END = " + this.end);
            // System.out.println("START = " + this.start);
            // System.out.println("DIFF = " + (this.end - this.start));
            // System.out.println("DIFF2 = " + ((this.end - this.start) / 1000));
	        return this.end - this.start;
	    }
	}
	
	/**
	 * Catch any argument exceptions thrown by children methods, 
	 * logs the error and returns a failed JSON response */
	@Catch(ArgumentException.class)
    public static void log(Index.ArgumentException e) {
        Logger.error("Caught Illegal Argument %s", e);
		returnFailed(e.toString());
    }
	
	@Before
    static void checkAuthenticated () throws ArgumentException {
        createSessionVariable("user_id", "session", "callerSession");
        createSessionVariable("for_user", "for_session", "forSession");
    }

    private static void createSessionVariable (String user_key, String session_key, String save_key) {

        Long user_id = params.get(user_key, Long.class);
        String session = params.get(session_key, String.class);
        boolean containsUserKey = user_id != null;
        boolean containsSessKey = session != null;
        
        if (containsUserKey && containsSessKey && user_id != -1) {
            
            if (Server.onMaster()) {
                UserSession sess = UserSession.getFor(user_id, session);
                if (sess == null) {
                    returnFailed(user_id + " and " + session + " do not map to a valid session");
                } else {
                    renderArgs.put(save_key, sess);
                }
            } 
            UserSession.Faux sess = new UserSession.Faux(user_id, session);
            renderArgs.put(save_key + "_faux", sess);    
        } else {
            renderArgs.put(save_key, null);
        }
    }

     static List<UserSession.Faux> currentForFauxSessionList () {
    	 List<UserSession.Faux> sessions = new LinkedList<UserSession.Faux>();
    	 UserSession.Faux current = currentForFauxSession();
    	 if (current != null) {
    		 sessions.add(current);
    		 return sessions;
    	 }
    	 String saveKey;
    	 boolean exists;
    	 int i = 0;
         do {
        	 String userKey = "for_user[" + i + "]";
        	 String sessKey = "for_session[" + i + "]";
        	 saveKey = "for_user_" + i;
        	 String fauxSaveKey = saveKey + "_faux";
        	 createSessionVariable(userKey, sessKey, saveKey);
        	 exists = renderArgs.data.containsKey(fauxSaveKey) &&
        	            renderArgs.get(fauxSaveKey) != null;
        	 if (exists) {
        		 sessions.add(renderArgs.get(fauxSaveKey, UserSession.Faux.class));
        	 }
        	 i++;
         } while (exists);
         return sessions;
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
	
	static String callback () {
	    return params.get("callback");
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
		Users.renderJSONP(
			getOkayResponse(msg), 
			new TypeToken<HashMap<String, String>>() {}.getType()
		);
	}

	/**
	 * renders a JSON status okay response */
	protected static void returnOkay () {
		Users.renderJSONP(
			getOkayResponse(), 
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
		if (t != null) {
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
	
	/**
	 * Simple exception class used by our controllers to handle invalid
	 * arguments.  These are thrown by individual methods, and caught and handled
	 * in this class */
	protected static class ArgumentException extends Exception {
		
		public ArgumentException (String err) {
			super(err);   
		}

	}
	
}


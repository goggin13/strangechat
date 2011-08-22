package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.HeartBeat;
import models.SuperPower;
import models.UserEvent;
import models.UserSession;
import models.pusher.*;

import play.libs.F.IndexedEvent;
import play.data.validation.Required;
import models.eliza.Eliza;
import play.libs.F.T2;
import java.util.AbstractMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This controller is in charge of pushing events into the event streams
 * for the individual chat servers, as well as providing an interface for
 * clients to listen to. */  
public class Notify extends Index {
	
	public static void pusherAuth (String socket_id, String channel_name) {
	    Pusher pusher = new Pusher();
        UserSession userSession = UserSession.find("bySocket", socket_id).first();
        BasicUserInfo userInfo = new BasicUserInfo(
            userSession.user.id,
            userSession.user.alias, 
            userSession.user.avatar
        );        
        PresenceChannelData channelData = new PresenceChannelData(userSession.session, userInfo);
        String auth = pusher.createAuthString(socket_id, channel_name, channelData);
        renderJSONP(
            auth,
            null
        );
	}
	
	public static void push (
	                @Required String channel, 
	                @Required String event, 
	                @Required String message, 
	                String socket_id) 
	{
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    Pusher pusher = new Pusher();
	    pusher.trigger(channel, event, message, socket_id);
	    returnOkay();
	}
	
	public static void setMySocket (String socket_id) {
	    UserSession sess = currentSession();
	    if (sess == null) {
	        returnFailed("Need valid session to set socket id");
	    }
	    sess.socket = socket_id;
	    sess.save();
	    returnOkay();
	}
	
	/**
	 * This is the admin function for listening to events on this server.  It does not long poll.
	 * All available events are returned immediately 
	 * @param lastReceived optional; the id of the last event seen. If zero, all events returned
	 */	
	public static void adminListen (Long lastReceived) {
        List<IndexedEvent> events = UserEvent.get().availableEvents(lastReceived);
        List<UserEvent.Event> data = new LinkedList<UserEvent.Event>();
        
        for (IndexedEvent e : events) {
            if (e.data instanceof UserEvent.Event) {
                data.add((UserEvent.Event)e.data);
            }
        }
        
        // still need to pass last received to admin listener, so do it here
        int count = events.size();
        lastReceived = count > 0 ? events.get(count - 1).id : 0L;
        data.add(new UserEvent.DirectMessage(-1L, -1L, lastReceived.toString(), "no_session"));
        
        renderJSON(new Gson().toJson(data));
	}

	/**
	 * Long poll for events relevant to <code>facebook_id</code>
	 * @param facebook_id
	 * @param lastReceived optional; the id of the last event seen. if omitted or zero,
						   will assign to the most current event
	 */	
	public static void listen (Long lastReceived) {
	    
	    UserSession.Faux sess = currentFauxSession();
		if (sess == null) {
			returnFailed("no user_id, session provided");
		}
		
		List<IndexedEvent<UserEvent.Event>> returnData = 
		    await(UserEvent.get().nextEvents(sess.user_id, lastReceived));
		    
        for (IndexedEvent<UserEvent.Event> es : returnData) {
            System.out.println("\t" + es.data.type);
        }
        
		renderJSONP(
			returnData, 
			new TypeToken<List<IndexedEvent<UserEvent.Event>>>() {}.getType()
		);
	}
	
	/**
	 * Creates a new event signifying a user has joined a room.  
	 * @param new_user the user id of the user who just joined
	 * @param name the name to be passed with the new user
	 * @param server the uri the new user is located on
	 * @param avatar url to display for the new user
	 * @param room_id the id of the room to join */
	public static void joined (String name, String server, String avatar, Long room_id) {
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();
		if (for_sess == null || from_sess == null) {
			returnFailed("cant create join without 2 valid faux sessions");
		}
		UserEvent.get().addJoin(for_sess.user_id, 
						   from_sess.user_id, 
						   avatar,
						   name,
						   server,
						   room_id, 
						   for_sess.session,
						   from_sess.session);				
        returnOkay();    
	}

	/**
	 * Creates a new event signifying a user has left a room.  
	 * @param room_id the id of the room being left */
	public static void left (Long room_id) {
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();		
		UserEvent.get().addLeave(for_sess.user_id, from_sess.user_id, room_id, for_sess.session);
		returnOkay();	
	}

	/**
	 * Creates a new event signifying a user has just logged on and is available
	 * to chat
	 * @param name the name to be passed with the new user
	 * @param server the uri the new user is located on */
	public static void login (String name, String server) {
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();			
		UserEvent.get().addUserLogon(for_sess.user_id, from_sess.user_id, name, server, for_sess.session, from_sess.session);
		returnOkay();	
	}

	/**
	 * Creates a new event signifying a user has logged out of the system
	 * @param session_id current session id this event is pertinent	 */	
	public static void logout () {
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();		
		UserEvent.get().addUserLogout(for_sess.user_id, from_sess.user_id, for_sess.session);
		returnOkay();	
	}

	/**
	 * Creates a new event signifying a message from one user to another 
	 * @param msg the text of the message being sent */
	public static void message (@Required String msg) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();
		UserEvent.get().addDirectMessage(for_sess.user_id, from_sess.user_id, msg, for_sess.session);
		returnOkay();
	}

	/**
	 * Creates a new event signifying a message from one user to another IN a chatroom.    
	 * @param msg the text of the message being sent
	 * @param room_id the pertinent room for this message  */
    public static void roomMessage (@Required String msg, @Required Long room_id) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }
    	UserSession.Faux from_sess = currentFauxSession();
		List<UserSession.Faux> for_sessions = currentForFauxSessionList();	
        for (UserSession.Faux for_sess : for_sessions) {
            UserEvent.get().addRoomMessage(for_sess.user_id, from_sess.user_id, room_id, msg, for_sess.session);
        }
        returnOkay();
    }
	
	/**
	 * Create a new event indicating the given user is currently typing 
	 * @param room_id optional, the room the typing is occuring in
	 * @param text the text that the user has typed so far */
	public static void userIsTyping (Long room_id, String text) {
		UserSession.Faux for_sess = currentForFauxSession();
		UserSession.Faux from_sess = currentFauxSession();			
		UserEvent.get().addUserIsTyping(for_sess.user_id, from_sess.user_id, text, room_id, for_sess.session);
		returnOkay();
	}	
	
	/**
	 * Create an event telling a user they have a new superpower 
	 * @param for_user the user who should read this event 	 
 	 * @param superPowerJSON serialized power  	 
 	 * @param power_id the id of the stored power record
 	 * @param level the level of the new power
	 * @param session_id current session id this event is pertinent	to */		 
	 public static void newPower (String superPowerJSON, Long power_id, int level) {
		 UserSession.Faux for_sess = currentForFauxSession();
         SuperPower superPower = SuperPower.fromJSON(superPowerJSON);
         UserEvent.get().addNewPower(for_sess.user_id, superPower, power_id, level, for_sess.session);
         returnOkay();
	 }
	 
 	/**
 	 * Create an event telling a user that a superpower was used
 	 * @param room_id optional, the room_id that it was used in
 	 * @param superPowerJSON serialized power  
 	 * @param level the level of the used power 	 
 	 * @param result the result of superpower.use() */
 	 public static void usedPower (Long room_id, String superPowerJSON, int level, String result) {
         SuperPower superPower = SuperPower.fromJSON(superPowerJSON);	     
         UserSession.Faux used_by = currentFauxSession();
         UserSession.Faux used_on = currentForFauxSession();
         if (used_by == null || used_on == null) {
             returnFailed("must specify sessions for used power");
         }
         UserEvent.get().addUsedPower(used_on.user_id, used_by.user_id, room_id, superPower, level, result, used_on.session);
         returnOkay();
 	 }
	
	/**
	 * http://localhost:9000/heartbeat?for_user=1&room_ids[0]=1&room_ids[1]=2&room_ids[2]=3
	 * Record a heartbeat for the given user.  Once we have recorded a heartbeat,
	 * this server will be in charge of keeping track of whether this user is still
	 * alive in the system. Once their heartbeat fades for more than 
	 * <code>User.HEALTHY_HEARTBEAT</code> seconds, the master is notified that this 
	 * user logged out 
	 * @param room_ids optional, list of rooms this user is currently in */
	public static void heartbeat (List<Long> room_ids) {
	    UserSession.Faux sess = currentFauxSession();
	    if (sess == null) {
	        returnFailed("user_id and session are required");
	    }
		HeartBeat.beatFor(sess);
		if (room_ids != null && room_ids.size() > 0 && room_ids.get(0) != null) {
		    UserEvent.get().addHeartBeat(sess.user_id);
			for (long rid : room_ids) {
				HeartBeat.beatInRoom(rid, sess);
			}
		}
		returnOkay();
	}
	
	/**
	 * return a message indicating whether the given user is alive or dead, eg if
	 * there is a remaining heartbeat for them.  The function will return the normal JSON
	 * response, e.g. a two element map of "status" and "message".  If the user is alive, 
	 * you will get "status" => "okay" and "message" => "true".  If dead, message will read false. */
	public static void isalive (long check_user, String check_session) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    returnOkay(HeartBeat.isAlive(check_user, check_session) 
	               ? "true" 
	               : "false");
	}

	/**
	 * return a message indicating whether the given user is alive or dead, eg if
	 * there is a remaining heartbeat for them.  The function will return the normal JSON
	 * response, e.g. a two element map of "status" and "message".  If the user is alive, 
	 * you will get "status" => "okay" and "message" => "true".  If dead, message will read false. */
	public static void flushheartbeats (@Required long flush_user, @Required String flush_session) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    HeartBeat.removeAll(new UserSession.Faux(flush_user, flush_session));
	    returnOkay();
	}
	
	/* 
	 * All of the following are helpers used by clients wishing to make requests to this
	 * controller.  They are used mostly by the Users controller and USers object, and 
	 * also by the unit tests */
	 
	public static HashMap<String, String> getFlushHeartBeatParams (long user_id, String session) {
		HashMap<String, String> params = new HashMap<String, String>();
     params.put("flush_user", user_id + "");
     params.put("flush_session", session);
     return params;
	}	

	public static HashMap<String, String> getCheckAliveParams (long user_id, String session) {
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("check_user", user_id + "");
        params.put("check_session", session);
        return params;
	}
	
	public static HashMap<String, String> getBasic (long for_user, String for_session, long user_id, String session) {
		HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user + "");
        params.put("for_session", for_session);
        params.put("user_id", user_id + "");
        params.put("session", session);
        return params;
	}
	
    public static HashMap<String, String> getNotifyLeftParams (long for_user, String for_session, long user_id, String session, Long room_id) {
        HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("room_id", room_id.toString());
        return params;
    }

    public static HashMap<String, String> getNotifyUsedPowerParams (
    				long for_user, 
    				String for_session, 
    				long user_id, 
    				String session,    		 
                    Long room_id, 
                    SuperPower power, 
                    int level, 
                    String result) 
    {
        HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("superPowerJSON", power.toJSON());
        params.put("result", result); 
        params.put("level", level + "");       
        params.put("room_id", (room_id != null ? room_id.toString() : ""));
        return params;        
    }

    public static HashMap<String, String> getNotifyNewPowerParams (long for_user, 
    															   String for_session, 
    															   SuperPower p, 
    															   Long power_id, 
    															   int level) 
    {
        HashMap<String, String> params = getBasic(for_user, for_session, -1, "");
        params.put("superPowerJSON", p.toJSON());
        params.put("power_id", power_id.toString());
        params.put("level", level + "");
        return params;
    }

    public static HashMap<String, String> getNotifyTypingParams (long for_user, 
    															 String for_session, 
    															 long user_id, 
    															 String session, 
    															 Long room_id, 
    															 String txt) {
    	HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("room_id", room_id.toString());
        params.put("text", "helloworld");
        return params;
    }

    public static HashMap<String, String> getNotifyLogoutParams (long for_user, String for_session, long user_id, String session) {
    	return getBasic(for_user, for_session, user_id, session);
    }

    public static HashMap<String, String> getNotifyLoginParams (long for_user, String for_session, long user_id, String session, String name, String server) {
    	HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("name", name);
        params.put("server", server);                
        return params;
    }

    public static HashMap<String, String> getNotifyMessageParams (long for_user, String for_session, long user_id, String session, String msg) {
    	HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("msg", msg);
        return params;
    }
    
    public static HashMap<String, String> getNotifyChatMessageParams (long for_user, 
    																  String for_session, 
    																  long user_id, 
    																  String session, 
    																  String msg, 
    																  Long room_id) 
    {
    	HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("msg", msg);
        params.put("room_id", room_id.toString());
        return params;
    }

    public static HashMap<String, String> getNotifyChatMessageParams (List<Long> for_users,  
    													    		  List<String> for_sessions,
    													    		  long user_id,
    													    		  String session,
    													    		  String msg, 
    													    		  Long room_id) {
    	HashMap<String, String> params = new HashMap<String, String>();
    	int i = 0;
        for (long for_user : for_users) {
            params.put("for_user[" + i + "]", for_user + "");
            params.put("for_session[" + i + "]", for_sessions.get(i) + "");
            i++;
        }
        params.put("user_id", user_id + "");
        params.put("session", session);        
        params.put("msg", msg);
        params.put("room_id", room_id.toString());
        return params;
    }

    public static HashMap<String, String> getNotifyJoinedParams (long for_user, 
																 String for_session, 
																 long user_id, 
																 String session, 
                                                                 String avatar, 
                                                                 String name, 
                                                                 String server, 
                                                                 Long room_id) 
    {
    	HashMap<String, String> params = getBasic(for_user, for_session, user_id, session);
        params.put("avatar", avatar);
        params.put("name", name);
        params.put("server", server);
        params.put("room_id", room_id.toString());	    
        return params;
    }	
	
}
package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.lang.reflect .*;
import models.*;
import enums.*;

/**
 * This controller is in charge of pushing events into the event streams
 * for the individual chat servers, as well as providing an interface for
 * clients to listen to. */  
public class Notify extends Index {
	
	/**
	 * This is the admin function for listening to events on this server.  It does not long poll.
	 * All available events are returned immediately 
	 * @param lastReceived optional; the id of the last event seen. If zero, all events returned
	 */	
	public static void adminListen (Long lastReceived) {
        List<IndexedEvent> events = UserEvent.userEvents.availableEvents(lastReceived);
        List<UserEvent.Event> data = new LinkedList<UserEvent.Event>();
        
        for (IndexedEvent e : events) {
            if (e.data instanceof UserEvent.Event) {
                data.add((UserEvent.Event)e.data);
            }
        }
        
        // still need to pass last recieved to admin listener, so do it here
        int count = events.size();
        lastReceived = count > 0 ? events.get(count - 1).id : 0L;
        data.add(new UserEvent.DirectMessage(-1L, -1L, lastReceived.toString()));
        
        renderJSON(new Gson().toJson(data));
	}

	/**
	 * Long poll for events relevant to <code>facebook_id</code>
	 * @param facebook_id
	 * @param lastReceived optional; the id of the last event seen. if omitted or zero,
						   will assign to the most current event
	 * @param callback optional JSONP callback
	 */	
	public static void listen (Long user_id, Long lastReceived, String callback) {
		if (user_id == null) {
			returnFailed("no user_id provided", callback);
		}
		if (lastReceived == null || lastReceived.equals(-1L)) {
			lastReceived = UserEvent.lastID();
		}
		List<IndexedEvent<UserEvent.Event>> returnData = new LinkedList<IndexedEvent<UserEvent.Event>>();
		
		do {
            // Logger.info(user_id + " is waiting for events : " + lastReceived);
			List<IndexedEvent<UserEvent.Event>> events = await(UserEvent.userEvents.nextEvents(lastReceived));
			// Logger.info(user_id + " is considering returned events : " + lastReceived);
			
			for (IndexedEvent<UserEvent.Event> e : events) {
				if (e.data.user_id == user_id) {
					returnData.add(e);
				}
				lastReceived = e.id;
				
			}			
		} while (returnData.size() == 0);
		
        // Logger.info("user " + user_id + " gets " + returnData.size() + " events back (" + callback + ")");
		renderJSONP(
			returnData, 
			new TypeToken<List<IndexedEvent<UserEvent.Event>>>() {}.getType(),
			callback
		);
	}
	
	/**
	 * Creates a new event signifying a user has joined a room.  
	 * @param for_user the user id who will recieve this notification
	 * @param new_user the user id of the user who just joined
	 * @param name the name to be passed with the new user
	 * @param server the uri the new user is located on
	 * @param avatar url to display for the new user
	 * @param room_id the id of the room to join 
	 * @param session_id current session id this event is pertinent	 */
	public static void joined (Long for_user, Long new_user, String name, String server, String avatar, Long room_id, String session_id) {
		new UserEvent.Join(for_user, 
						   new_user, 
						   avatar,
						   name,
						   server,
						   room_id, 
						   session_id);				
        returnOkay(null);    
	}

	/**
	 * Creates a new event signifying a user has left a room.  
	 * @param for_user the user id who will recieve this notification
	 * @param left_user the user id of the user who has left
	 * @param room_id the id of the room being left
	 * @param session_id current session id this event is pertinent	  */	
	public static void left (Long for_user, Long left_user, Long room_id, String session_id) {
		new UserEvent.Leave(for_user, left_user, room_id, session_id);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a user has just logged on and is available
	 * to chat
	 * @param for_user the user id who will recieve this notification
	 * @param new_user the user id of the user who just logged on
	 * @param name the name to be passed with the new user
	 * @param server the uri the new user is located on 
	 * @param session_id current session id this event is pertinent	 */
	public static void login (Long for_user, Long new_user, String name, String server, String session_id) {
		new UserEvent.UserLogon(for_user, new_user, name, server, session_id);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a user has logged out of the system
	 * @param for_user the user id who will recieve this notification
	 * @param left_user the user id of the user who has left 
	 * @param session_id current session id this event is pertinent	 */	
	public static void logout (Long for_user, Long left_user, String session_id) {
		new UserEvent.UserLogout(for_user, left_user, session_id);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a message from one user to another 
	 * @param for_user the user id who will recieve this notification; note that this
	 * 				   doesn't preclude more than one user from receiving this message, 
	 *				   if there are more than 2 users in a room multiple events of this
	 *				   type will be generated.  	
	 * @param from_user the user id of the user who sent the message
	 * @param msg the text of the message being sent
	 * @param callback optional JSONP callback */
	public static void message (Long for_user, Long from_user, String msg, String callback) {
		new UserEvent.DirectMessage(for_user, from_user, msg);
		returnOkay(callback);
	}

	/**
	 * Creates a new event signifying a message from one user to another IN a chatroom.    
	 * @param for_user the user id who will recieve this notification; note that this
	 * 				   doesn't preclude more than one user from receiving this message, 
	 *				   if there are more than 2 users in a room multiple events of this
	 *				   type will be generated.  	
	 * @param from_user the user id of the user who sent the message
	 * @param msg the text of the message being sent
	 * @param room_id the pertinent room for this message
	 * @param callback optional JSONP callback */
	public static void roomMessage (Long for_user, Long from_user, String msg, Long room_id, String callback) {
		new UserEvent.RoomMessage(for_user, from_user, room_id, msg);
		returnOkay(callback);
	}
	
	/**
	 * Create a new event indicating the given user is currently typing 
	 * @param for_user the user who should read this event 
	 * @param user_id the user who is typing
	 * @param room_id optional, the room the typing is occuring in
	 * @param text the text that the user has typed so far 
	 * @param callback optional JSONP callback */
	public static void userIsTyping (Long for_user, Long user_id, Long room_id, String text, String callback) {
		new UserEvent.UserIsTyping(for_user, user_id, text, room_id);
		returnOkay(callback);
	}	
	
	/**
	 * Create an event telling a user they have a new superpower 
	 * @param for_user the user who should read this event 	 
 	 * @param superPowerJSON serialized power  	 
 	 * @param power_id the id of the stored power record
 	 * @param level the level of the new power
	 * @param session_id current session id this event is pertinent	to */		 
	 public static void newPower (Long for_user, String superPowerJSON, Long power_id, int level, String session_id) {
         SuperPower superPower = SuperPower.fromJSON(superPowerJSON);
         new UserEvent.NewPower(for_user, superPower, power_id, level, session_id);
         returnOkay(null);
	 }
	 
 	/**
 	 * Create an event telling a user that a superpower was used
 	 * @param for_user the user who should read this event 	 
 	 * @param by_user the user who used the power
 	 * @param room_id optional, the room_id that it was used in
 	 * @param superPowerJSON serialized power  
 	 * @param level the level of the used power 	 
 	 * @param result the result of superpower.use()
 	 * @param session_id current session id this event is pertinent	to */		 
 	 public static void usedPower (Long for_user, Long by_user, Long room_id, String superPowerJSON, int level, String result, String session_id) {
         SuperPower superPower = SuperPower.fromJSON(superPowerJSON);	     
         new UserEvent.UsedPower(for_user, by_user, room_id, superPower, level, result, session_id);
         returnOkay(null);
 	 }
	
	/**
	 * http://localhost:9000/heartbeat?for_user=1&room_ids[0]=1&room_ids[1]=2&room_ids[2]=3
	 * Record a heartbeat for the given user.  Once we have recorded a heartbeat,
	 * this server will be in charge of keeping track of whether this user is still
	 * alive in the system. Once their heartbeat fades for more than 
	 * <code>User.HEALTHY_HEARTBEAT</code> seconds, the master is notified that this 
	 * user logged out 
	 * @param for_user the user_id of the user logging out 
	 * @param room_ids optional, list of rooms this user is currently in
	 * @param callback optional JSONP callback */
	public static void heartbeat (Long for_user, List<Long> room_ids, String callback) {
		HeartBeat.beatFor(for_user);
		if (room_ids != null && room_ids.size() > 0 && room_ids.get(0) != null) {
		    new UserEvent.HeartBeat(for_user);
			for (long rid : room_ids) {
				HeartBeat.beatInRoom(rid, for_user);
			}
		}
		returnOkay(callback);
	}
	
		
    public static HashMap<String, String> getNotifyLeftParams (Long for_user, Long left_user, Long room_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("left_user", left_user.toString());
        params.put("room_id", room_id.toString());
        return params;
    }

    public static HashMap<String, String> getNotifyUsedPowerParams (
                    Long for_user, 
                    Long by_user, 
                    Long room_id, 
                    SuperPower power, 
                    int level, 
                    String result, 
                    String session_id) 
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("superPowerJSON", power.toJSON());
        params.put("by_user", by_user.toString());
        params.put("result", result); 
        params.put("level", level + "");       
        params.put("room_id", (room_id != null ? room_id.toString() : ""));
        params.put("for_user", for_user.toString());
        params.put("session_id", session_id);
        return params;        
    }

    public static HashMap<String, String> getNotifyNewPowerParams (Long for_user, SuperPower p, Long power_id, int level, String session_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("superPowerJSON", p.toJSON());
        params.put("power_id", power_id.toString());
        params.put("level", level + "");
        params.put("for_user", for_user.toString());
        params.put("session_id", session_id);
        return params;
    }

    public static HashMap<String, String> getNotifyTypingParams (Long for_user, Long user_id, Long room_id, String txt) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("user_id", user_id.toString());
        params.put("room_id", room_id.toString());
        params.put("text", "helloworld");
        return params;
    }

    public static HashMap<String, String> getNotifyLogoutParams (Long for_user, Long left_user) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("left_user", left_user.toString());
        return params;
    }

    public static HashMap<String, String> getNotifyLoginParams (Long for_user, Long new_user, String name, String server) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("new_user", new_user.toString());
        params.put("name", name);
        params.put("server", server);                
        return params;
    }

    public static HashMap<String, String> getNotifyMessageParams (Long from_user, Long for_user, String msg) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("from_user", from_user.toString());
        params.put("msg", msg);
        return params;
    }

    public static HashMap<String, String> getNotifyChatMessageParams (Long from_user, Long for_user, String msg, Long room_id) {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("from_user", from_user.toString());
        params.put("msg", msg);
        params.put("room_id", room_id.toString());
        return params;
    }


    public static HashMap<String, String> getNotifyJoinedParams (Long for_user, 
                                                                  Long new_user, 
                                                                  String avatar, 
                                                                  String name, 
                                                                  String server, 
                                                                  Long room_id, 
                                                                  String session_id) 
    {
    	HashMap<String, String> params = new HashMap<String, String>();
        params.put("for_user", for_user.toString());
        params.put("new_user", new_user.toString());
        params.put("avatar", avatar);
        params.put("name", name);
        params.put("server", server);
        params.put("room_id", room_id.toString());	    
        params.put("session_id", session_id);
        return params;
    }	
	
}
package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import models.*;

/**
 * This controller is in charge of pushing events into the event streams
 * for the individual chat servers, as well as providing an interface for
 * clients to listen to. */  
public class Notify extends Index {
	
	/**
	 * Long poll for events relevant to <code>facebook_id</code>
	 * @param facebook_id
	 * @param lastReceived the id of the last event seen
	 * @param callback optional JSONP callback
	 */	
	public static void listen (Long user_id, Long lastReceived, String callback) {
		if (user_id == null) {
			returnFailed("no user_id provided", callback);
		}
		List<IndexedEvent<UserEvent.Event>> returnData = new LinkedList<IndexedEvent<UserEvent.Event>>();
		
		do {
			System.out.println(user_id + " is waiting for events : " + lastReceived);
			List<IndexedEvent<UserEvent.Event>> events = await(UserEvent.userEvents.nextEvents(lastReceived));
			System.out.println(user_id + " is considering returned events : " + lastReceived);
			
			for (IndexedEvent<UserEvent.Event> e : events) {
				if (e.data.user_id.equals(user_id)) {
					returnData.add(e);
				}
				lastReceived = e.id;
			}			
		} while (returnData.size() == 0);
		
		Users.renderJSONP(
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
	 * @param room_id the id of the room to join */
	public static void joined (Long for_user, Long new_user, String name, String server, String avatar, Long room_id) {
		new UserEvent.Join(for_user, 
						   new_user, 
						   avatar,
						   name,
						   server,
						   room_id);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a user has left a room.  
	 * @param for_user the user id who will recieve this notification
	 * @param left_user the user id of the user who has left
	 * @param room_id the id of the room being left */	
	public static void left (Long for_user, Long left_user, Long room_id) {
		new UserEvent.Leave(for_user, left_user, room_id);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a user has just logged on and is available
	 * to chat
	 * @param for_user the user id who will recieve this notification
	 * @param new_user the user id of the user who just logged on
	 * @param name the name to be passed with the new user
	 * @param server the uri the new user is located on */
	public static void login (Long for_user, Long new_user, String name, String server) {
		new UserEvent.UserLogon(for_user, new_user, name, server);
		returnOkay(null);	
	}

	/**
	 * Creates a new event signifying a user has logged out of the system
	 * @param for_user the user id who will recieve this notification
	 * @param left_user the user id of the user who has left */	
	public static void logout (Long for_user, Long left_user) {
		new UserEvent.UserLogout(for_user, left_user);
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
	 * @param typing_user the user who is typing
	 * @param room_id optional, the room the typing is occuring in
	 * @param text the text that the user has typed so far 
	 * @param callback optional JSONP callback */
	public static void userIsTyping (Long for_user, Long typing_user, Long room_id, String text, String callback) {
		new UserEvent.UserIsTyping(for_user, typing_user, text, room_id);
		returnOkay(callback);
	}	
	
	
	/**
	 * Record a heartbeat for the given user.  Once we have recorded a heartbeat,
	 * this server will be in charge of keeping track of whether this user is still
	 * alive in the system. Once their heartbeat fades for more than 
	 * <code>User.HEALTHY_HEARTBEAT</code> seconds, the master is notified that this 
	 * user logged out 
	 * @param for_user the user_id of the user logging out 
	 * @param callback optional JSONP callback */
	public static void heartbeat (Long for_user, String callback) {
		User.heartbeats.put(for_user, new Date());
		returnOkay(callback);
	}
	
}
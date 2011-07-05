package controllers;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;

import models.*;

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

		System.out.println(user_id + " returning events : " + lastReceived);
		Users.renderJSONP(
			returnData, 
			new TypeToken<List<IndexedEvent<UserEvent.Event>>>() {}.getType(),
			callback
		);
	}
	
	public static void joined (Long for_user, Long new_user, String avatar) {
		User newUser = User.find("byUser_id", new_user).first();
		newUser.avatar = avatar;
		newUser.save();
		new UserEvent.Join(for_user, newUser);
		returnOkay(null);	
	}
	
	public static void login (Long for_user, Long new_user) {
		new UserEvent.UserLogon(for_user, new_user);
		returnOkay(null);	
	}
	
	public static void logout (Long for_user, Long left_user) {
		new UserEvent.UserLogout(for_user, left_user);
		returnOkay(null);	
	}
	
	public static void message (Long for_user, Long from_user, String msg, String callback) {
		new UserEvent.DirectMessage(for_user, from_user, msg);
		returnOkay(callback);
	}
	
	public static void heartbeat (Long for_user, String callback) {
		User.heartbeats.put(for_user, new Date());
		returnOkay(callback);
	}
	
}
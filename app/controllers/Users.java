package controllers;

import java.util.*;
import java.lang.reflect .*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import play.cache.Cache;

import models.*;

import com.google.gson.*;
import com.google.gson.reflect.*;


/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Users extends Index {
	
	/**
	 * Log user with <code>facebook_id</code> into the system.  Using <code>access_token</code>,
	 * contact the Facebook API and download their latest set of friends.  Check current online
	 * users for matches, and return the set of friends who are online.  In the list of friends is
	 * included a mapping of -1 => chatroom server; this is the server the requesting client should 
	 * login to
	 * @param facebook_id the facebook id of the user logging in
	 * @param access_token an up to date access_token for logging into the facebook API
	 * @param callback optional, required for cross-doman requests
	 */
	public static void login (Long facebook_id, String access_token, String callback) {
		
		JsonObject jsonObj = Utility.getMyFacebookFriends(facebook_id, access_token);
		if (jsonObj.has("error")) {
			renderJSON(jsonObj.toString());
		}
		
		Set<User> friends = new HashSet<User>();
		HashMap<Long, String> friendData = new HashMap<Long, String>();
		
		for (JsonElement ele : jsonObj.get("data").getAsJsonArray()) {
			JsonObject friendObj = ele.getAsJsonObject();
			try {
				Long friendID = friendObj.get("id").getAsLong();
				String friendName = friendObj.get("name").getAsString();
				User friend = User.find("byUser_id", friendID).first();
				if (friend != null && friend.online) {
					friends.add(friend);
					friendData.put(friendID, friendName);
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION " + e);
			}
		}

		User user = User.getOrCreate(facebook_id);
		user.online = true;
		user.friends = friends;
		user.save();		
		
		// add heartbeat server into list
		friendData.put(-1L, Server.getMyHeartbeatServer(user).name);
		Users.renderJSONP(
			friendData, 
			new TypeToken<HashMap<Long, String>>() {}.getType(),
			callback
		);
	}
	
	/**
	 * Mark this user as offline
	 * @param facebook_id
	 */
	public static void logout (Long facebook_id, String callback) {
		User user = User.find("byUser_id", facebook_id).first();
		if (user != null) {
			user.online = false;
			user.save();
			returnOkay(callback);
		} else {
			returnFailed("user " + facebook_id + " not found", callback);
		}
	}

	/**
	 * Long poll for events relevant to <code>facebook_id</code>
	 * @param facebook_id
	 * @param lastReceived the id of the last event seen
	 * @param callback optional JSONP callback
	 */	
	public static void listen (Long user_id, Long lastReceived, String callback) {
		
		List<IndexedEvent<UserEvent.AnEvent>> returnData = new LinkedList<IndexedEvent<UserEvent.AnEvent>>();
		
		do {
			System.out.println(user_id + " is waiting for events");
			List<IndexedEvent<UserEvent.AnEvent>> events = await(User.userEvents.nextEvents(lastReceived));
			System.out.println(user_id + " is considering returned events");
			for (IndexedEvent<UserEvent.AnEvent> e : events) {
				if (e.data.user_id.equals(user_id)) {
					returnData.add(e);
				}
			}			
		} while (returnData.size() == 0);

		System.out.println(user_id + " returning events");
		Users.renderJSONP(
			returnData, 
			new TypeToken<List<IndexedEvent<UserEvent.AnEvent>>>() {}.getType(),
			callback
		);
	}

}
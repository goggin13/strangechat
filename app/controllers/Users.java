package controllers;

import java.util.*;
import java.lang.reflect .*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import play.cache.Cache;

import models.*;

import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Users extends Index {
	/** list of ids of people waiting to be matched up with someone to chat */
	public static final Queue<Long> waitingRoom = new ConcurrentLinkedQueue<Long>();
	public static AtomicLong nextRoomID = new AtomicLong(1);
	
	/**
	 * Log user with <code>facebook_id</code> into the system.  Using <code>access_token</code>,
	 * contact the Facebook API and download their latest set of friends.  Check current online
	 * users for matches, and return the set of friends who are online.  In the list of friends is
	 * included a mapping of -1 => chatroom server; this is the server the requesting client should 
	 * login to
	 * @param facebook_id the facebook id of the user logging in
	 * @param name the name to assign to the user logging in
	 * @param access_token an up to date access_token for logging into the facebook API
	 * @param avatar optional, url of an avatar to display for this user
	 * @param alias optional, an alias for this user
	 * @param callback optional, required for cross-doman requests
	 */
	public static void login (Long facebook_id, String name, String access_token, String avatar, String alias, String callback) {
		
		JsonObject jsonObj = Utility.getMyFacebookFriends(facebook_id, access_token);
		if (jsonObj.has("error")) {
			renderJSON(jsonObj.toString());
		}
		
		Set<User> friends = new HashSet<User>();
		HashMap<String, User> friendData = new HashMap<String, User>();
		
		for (JsonElement ele : jsonObj.get("data").getAsJsonArray()) {
			JsonObject friendObj = ele.getAsJsonObject();
			try {
				Long friendID = friendObj.get("id").getAsLong();
				String friendName = friendObj.get("name").getAsString();
				User friend = User.find("byUser_id", friendID).first();
				if (friend != null && friend.online) {
					friends.add(friend);
					friendData.put(friendID.toString(), friend);
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION " + e);
			}
		}

		User user = User.getOrCreate(facebook_id);
		Server server = Server.getMyHeartbeatServer(user);
		user.heartbeatServer = server;
		user.friends = friends;
		user.name = name;
		if (avatar != null) {
			user.avatar = avatar;
		}
		user.alias = alias == null ? "" : alias;
		user.login();
		user.save();		
		
		// add heartbeat server into list
		friendData.put(user.user_id.toString(), user);
		Users.renderJSONP(
			friendData, 
			new TypeToken<HashMap<String, User>>() {}.getType(),
			callback
		);
	}
	
	/**
	 * Mark this user as offline
	 * @param facebook_id
	 * @param callback optional JSONP callback
	 */
	public static void logout (Long facebook_id, String callback) {
		if (User.logOutUser(facebook_id)) {
			returnOkay(callback);
		} else {
			returnFailed("user " + facebook_id + " not found", callback);
		}
	}

	/**
	 * Return a random user to chat with 
	 * @param user_id your user_id, so the random returned user isn't you 
	 * @param callback optional JSONP callback*/
	public static void random (Long user_id, String callback) {
		User you = User.find("byUser_id", user_id).first();
		if (you == null) {
			returnFailed("user " + user_id + " not found", callback);
		}
		User rando = you.getRandom();
		if (!rando.friends.contains(you)) {
			rando.friends.add(you);
			rando.save();
		}
		if (!you.friends.contains(rando)) {
			you.friends.add(you);
			you.save();
		}
		Users.renderJSONP(
			rando, 
			new TypeToken<User>() {}.getType(),
			callback
		);
	}
	
	/**
	 * For super hero chat, indicate you are ready to start chatting.  Someone else will be
	 * paired with you immediately if they are available, or whenever they do become available
	 * @param user_id your user_id, so the random returned user isn't you 
	 * @param callback optional JSONP callback*/
	public static synchronized void requestRandomRoom (Long user_id, String callback) {
		
		System.out.println(waitingRoom);
		if (waitingRoom.size() > 0) {
			Long otherUserID = null;
			if (waitingRoom.peek().equals(user_id)) {
				for (Long id : waitingRoom) {
					if (!id.equals(user_id)) {
						otherUserID = id;
						waitingRoom.remove(id);
						break;
					}
				}
			} else {
				otherUserID = waitingRoom.poll();
			}
			
			if (otherUserID != null) {
				User otherUser = User.find("byUser_id", otherUserID).first();
				User you = User.find("byUser_id", user_id).first();

				Long room_id = nextRoomID.incrementAndGet();
				Room r = new Room(room_id);
				r.participants.add(otherUser);
				r.participants.add(you);
				r.save();
				
				you.notifyJoined(otherUser, room_id);
				otherUser.notifyJoined(you, room_id);
				returnOkay(callback);
			}
		}
		
		// no one there yet!
		if (!waitingRoom.contains(user_id)) {
			waitingRoom.add(user_id);
		}
		returnOkay(callback);
	}
	
	/**
	 * Remove a user from a room, and notify other participants they left
	 * @param user_id the user to remove from the room
	 * @param room_id the room to remove them from
	 * @param callback optional JSONP callback */
	public static void leaveRoom (Long user_id, Long room_id, String callback) {
		Room room = Room.find("byRoom_id", room_id).first();
		User user = User.find("byUser_id", user_id).first();
		if (user == null) {
			returnFailed("user " + user_id + " not found", callback);
		}
		if (room == null) {
			returnFailed("room " + room_id + " not found", callback);
		}
		room.removeParticipant(user);
	}
	

}
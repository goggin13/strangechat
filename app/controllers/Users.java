package controllers;

import java.util.*;
import java.lang.reflect .*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import play.cache.Cache;
import play.*;
import models.*;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.*;
import com.google.gson.reflect.*;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Users extends Index {
	/** list of ids of people waiting to be matched up with someone to chat */
	public static List<Long> waitingRoom = new CopyOnWriteArrayList<Long>();
	
	/** How many meetings, 0 indexed, ago users must have interacted before they
	 *  can be matched again.  0 means users can talk, then they have to talk to 
	 *  at least one other perosn each.  -1 means they can be pairied in back
	 *  to back rooms. */
	public static int remeetEligible = -1;
	
	/**
	 * Maximum number of pending spots in the waiting room a single user can occupy */
	public static int spotsPerUser = 2;
	
	@Before (unless={"signin", "signout", "random", "requestRandomRoom", "leaveRoom"})
	public static void checkAuth () {
		Index.checkAuthentication();
	}
	
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
	public static void signin (
								Long facebook_id, 
								String name, 
								String access_token, 
								String avatar, 
								String alias, 
								boolean updatefriends, 
								String callback) throws Index.ArgumentException
	{
		if (facebook_id == null || (updatefriends && access_token == null)) {
			throw new Index.ArgumentException("are both required", "facebook_id and access_token", callback);
		}	
		User user = User.getOrCreate(facebook_id);
		Server server = Server.getMyHeartbeatServer(user);
		user.heartbeatServer = server;
		
		HashMap<String, User> friendData = new HashMap<String, User>();
		if (updatefriends) {
			friendData = user.updateMyFacebookFriends(access_token);	
			if (friendData == null){
				returnFailed("Failed to log into facebook; likely expired token", callback);
			}
		}
		
		user.name = name;
		if (avatar != null) {
			user.avatar = avatar;
		}
		user.alias = alias == null ? "" : alias;
		user.login();
		user.save();		
		broadcastHeartbeat(user);
		
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
	public static void signout (Long facebook_id, String callback) {
		if (User.logOutUser(facebook_id)) {
			returnOkay(callback);
		} else {
			returnFailed("user " + facebook_id + " not found", callback);
		}
		waitingRoom.remove(facebook_id);
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
	 * Helper for <code>requestRandomRoom</code>
	 * @return true if these users are eligible to be paired in a room right now */
	private static boolean canBePaired (Long user_id1, Long user_id2) {
	    return !user_id1.equals(user_id2) 
		        && (remeetEligible == -1 
		            || !Room.hasMetRecently(user_id1, user_id2, remeetEligible));
	}
	
	/**
	 * For super hero chat, indicate you are ready to start chatting.  Someone else will be
	 * paired with you immediately if they are available, or whenever they do become available
	 * @param user_id your user_id, so the random returned user isn't you 
	 * @param callback optional JSONP callback*/
	public static synchronized void requestRandomRoom (Long user_id, String callback) {
		
		if (waitingRoom.size() > 0) {
		    Long otherUserID = null;
			for (Long id : waitingRoom) {
				if (canBePaired(id, user_id)) {
					otherUserID = id;
					waitingRoom.remove(id);
					break;
				}
			}
			
			if (otherUserID != null) {
			    Room.createRoomFor(otherUserID, user_id);	
				returnOkay(callback);
			}
		}
		
		// no one there yet!
		if (Collections.frequency(waitingRoom, user_id) < spotsPerUser) {
		    waitingRoom.add(user_id);
		}
        System.out.println(waitingRoom);
        // Logger.info("putting user " + user_id + " in the waiting room (" + waitingRoom.toString() + ")");    
		returnOkay(callback);
	}

    /**
     * Broadcast a heartbeat for the given User, either to this server if we are
     * their heartbeat server, else send the request to the appropriate server.
     * This function is called on login for a user, we heartbeat at their assigned server
     * to catch the case where they login, receive a response, but fail before they begin to 
     * heartbeat on their own
     * @user the user to heartbeat for 
     * @return true if the heartbeat is successfuly sent */
	private static boolean broadcastHeartbeat (User user) {
	    String heartbeatURI = user.getHeartbeatURI();
	    String masterURI = Server.getMasterServer().uri;
		if (masterURI.equals(heartbeatURI)) {
		    User.heartbeats.put(user.user_id, new Date());
		    return true;
		} else {	
			String url = heartbeatURI + "heartbeat";
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", user.user_id);
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
			return json.get("status").getAsString().equals("okay");
		}
	}
	
	/**
	 * Remove a user from a room, and notify other participants they left
	 * @param user_id the user to remove from the room
	 * @param room_id the room to remove them from
	 * @param callback optional JSONP callback */
	public static void leaveRoom (Long user_id, Long room_id, String callback) {
		Room.removeUserFrom(room_id, user_id);
		returnOkay(callback);
	}
	

}
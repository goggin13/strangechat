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
	 *  at least one other person each.  -1 means they can be paired in back
	 *  to back rooms. */
	public static int remeetEligible = 0;
	
	/**
	 * Maximum number of pending spots in the waiting room a single user can occupy */
	public static int spotsPerUser = 2;
	
	@Before (unless={"signin", "signout", "random", "requestRandomRoom", "leaveRoom", "usePower", "waiting_room_is_empty"})
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
		if (BlackList.isBlacklisted(user)) {
		    returnFailed("You have been blacklisted", callback);
		}
    	
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
		user.session_id = Utility.md5(facebook_id.toString() + System.currentTimeMillis());
		user.alias = alias == null ? "" : alias;
		user.login();
		user.save();		
		broadcastHeartbeat(user);
		
		// since this user is just logging in, if they have previous requests for the waiting room, we should
		// null them out
        removeFromWaitingRoom(user.id, true);
		
		// add heartbeat server into list
		friendData.put(user.id.toString(), user);
		
		Users.renderJSONP(
			friendData, 
			new TypeToken<HashMap<String, User>>() {}.getType(),
			callback
		);
	}
	
	/**
	 * Mark this user as offline; remove them from the waiting room
	 * @param user_id
	 * @param callback optional JSONP callback
	 */
	public static void signout (Long user_id, String callback) {
	    User u = User.findById(user_id);
		if (u != null) {
		    removeFromWaitingRoom(user_id, true);
		    u.logout();
			returnOkay(callback);
		} else {
			returnFailed("user " + user_id + " not found", callback);
		}
	}
	
	/**
	 * Helper for <code>requestRandomRoom</code>
	 * @return true if these users are eligible to be paired in a room right now */
	private static boolean canBePaired (Long user_id1, Long user_id2) {
	    System.out.println("pair " + user_id1 + ", " + user_id2);
	    
        // System.out.println(UserExclusion.canSpeak(user_id1, user_id2));
	    
        // System.out.println(remeetEligible == -1 
            // || !Room.hasMetRecently(user_id1, user_id2, remeetEligible));
	    
        // System.out.println(!Room.areSpeaking(user_id1, user_id2));
	    
	    return !user_id1.equals(user_id2) 
		        && UserExclusion.canSpeak(user_id1, user_id2)
		        && (remeetEligible == -1 
		            || !Room.hasMetRecently(user_id1, user_id2, remeetEligible))
                && !Room.areSpeaking(user_id1, user_id2);
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
					removeFromWaitingRoom(id, false);
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
		
		returnOkay(callback);
	}
	
    /**
     * Removes all occurences of the given user from the waiting room
     * @param user_id the id to remove from the room 
     * @param removeAll if true, remove all of the occurences of this user,
     *                  else just one */
    private static void removeFromWaitingRoom (Long user_id, boolean removeAll) {
        while (waitingRoom.contains(user_id)) {
            waitingRoom.remove(user_id);
            if (!removeAll) {
                return;
            }
        }
    }

    /**
     * This is only called from a unit test, and all it does it test if the waitingroom is
     * empty.  Two test bots wish to talk to eachother but don't want to accidentally get a 
     * real user */
    public static void waiting_room_is_empty () {
        if (waitingRoom.size() == 0) {
            returnOkay(null);
        } else {
            returnFailed("The waiting room is not empty", null);
        }
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
		    HeartBeat.beatFor(user.id);
		    new UserEvent.HeartBeat(user.id);		    
		    return true;
		} else {	
			String url = heartbeatURI + "heartbeat";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("for_user", user.id.toString());
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
	
	/**
	 * Use the given power, and notify the relevant users.
	 * @param power_id the id of a {@link StoredPower} to use
	 * @param user_id the user using the power
	 * @param other_id the other user in the room
	 * @param room_id optional, the room the event is in
	 * @param callback optional jsonp callback */
	public static void usePower (Long power_id, Long user_id, Long other_id, Long room_id, String callback) { 
	    if (power_id == null || user_id == null) {
	        returnFailed("power_id, user_id, are both required", callback);
	    }
	    User user = User.findById(user_id);
	    User other = null;
	    if (other_id != null) {
	        other = User.findById(other_id);
	    }
	    if (user == null || (other_id != null && other_id != -1 && other == null)) {
	        returnFailed("both user_id and other_id must map to existing users", callback);
	    }
	    
        StoredPower storedPower = StoredPower.findById(power_id);
        if (storedPower == null) {
            returnFailed("no power by that ID exists", callback);
        } else if (!storedPower.canUse()) {
            returnFailed("Use Power : You don't have any of that power remaining!", callback);
        }

        SuperPower sp = storedPower.getSuperPower();
        String result = storedPower.use(other);
        
        user.notifyUsedPower(user_id, room_id, sp, storedPower.level, result);
        if (room_id != null && other != null) {
            other.notifyUsedPower(user_id, room_id, sp, storedPower.level, result);            
        } else {            
            HashMap<User, Long> conversants = user.getConversants();
            for (User u : conversants.keySet()) {
                u.notifyUsedPower(user_id, conversants.get(u), sp, storedPower.level, result);
            }
        }

        returnOkay(callback);

	}
}
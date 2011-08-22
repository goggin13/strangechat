package controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import models.BlackList;
import models.HeartBeat;
import models.Room;
import models.Server;
import models.StoredPower;
import models.SuperPower;
import models.User;
import models.WaitingRoom;
import models.UserEvent;
import models.UserSession;
import models.UserExclusion;
import models.Utility;
import models.pusher.*;
import play.data.validation.Required;
import play.libs.WS;
import play.libs.F.T2;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Users extends Index {
	    				
	/**
	 * This function signs in a user who has not linked their facebook account.  
	 * @param user_id the user_id field of the user signing in
	 * @param avatar optional, url of an avatar to display for this user
	 * @param alias optional, an alias for this user */
	public static void signin (long sign_in_id, String avatar, String alias) {
	    User user = User.getOrCreate(sign_in_id);
        if (BlackList.isBlacklisted(user)) {
		    returnFailed("You have been blacklisted");
		}
        Users.renderJSONP(
            getYourData(user, avatar, alias), 
            new TypeToken<HashMap<String, User>>() {}.getType()
        );   
	}
	
	private static HashMap<String, User> getYourData (
	            User user,
	            String avatar,
	            String alias) 
	{	    
		user.avatar = avatar == null ? "" : avatar;
		user.alias = alias == null ? "" : alias;
		UserSession sess = user.login();
		user.save();		
		
		HashMap<String, User> data = new HashMap<String, User>();
		data.put(user.user_id + "", user);
		
		return data;	    
	}
	
	/**
	 * Mark this user as offline; remove them from the waiting room;
	 * @param fromChatServer if this is <code>true</code>, then it means
	 * we are getting this request from their chat server.  They will have
	 * already silenced this users heartbeat.  If we are not getting this from
	 * the chat server, we should contact their chat server, and tell them
	 * to flush this heartbeat. */
	public static void signout (boolean fromChatServer) {
	    UserSession sess = currentSession();
	    UserSession.Faux sessFaux = currentFauxSession();
		if (sess != null) {
		    sess.flushMyHeartBeats();
		    WaitingRoom.get().remove(sess.user.id, sess.session, true);
		    sess.logout();
		    sess.delete();
			returnOkay();
		} else {
		    if (sessFaux != null) {
		        WaitingRoom.get().remove(sessFaux.user_id, sessFaux.session, true);
		    }
		    returnFailed("No valid user, session data passed (user_id, session)");
		}
	}

    /**
     * For super hero chat, indicate you are ready to start chatting.  Someone else will be
     * paired with you immediately if they are available, or whenever they do become available
     * @param user_id your user_id, so the random returned user isn't you 
     * @param callback optional JSONP callback*/
    public static synchronized void requestRandomRoom () {
        UserSession user = currentSession();
        if (user == null) {
            returnFailed("No current session provided");
        }
        WaitingRoom.get().requestRandomRoom(user);
        System.out.println(WaitingRoom.get());
        returnOkay();
    }	
    
	/**
	 * Join or create a group room with the given key
	 * @param key the key to link to the room */
    public static void joinGroupChat (@Required String key) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }
        Room r = Room.joinGroupChat(currentSession(), key);
        returnOkay(r.room_id + "");
    }
	
    /**
     * This is only called from a unit test, and all it does it test if the waitingroom is
     * empty.  Two test bots wish to talk to eachother but don't want to accidentally get a 
     * real user */
    public static void waiting_room_is_empty () {
        if (WaitingRoom.get().empty()) {
            returnOkay(null);
        } else {
            returnFailed("The waiting room is not empty");
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
	private static boolean broadcastHeartbeat (UserSession user) {
	    String heartbeatURI = user.user.getHeartbeatURI();
	    String masterURI = Server.getMasterServer().uri;
		if (masterURI.equals(heartbeatURI)) {
		    HeartBeat.beatFor(user.toFaux());
		    return true;
		} else {	
			String url = heartbeatURI + "heartbeat";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("for_user", user.user.id + "");
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
			return json.get("status").getAsString().equals("okay");
		}
	}
	
	/**
	 * Remove a user from a room, and notify other participants they left
	 * @param room_id the room to remove them from */
	public static void leaveRoom (long room_id) {
		Room room = Room.find("byRoom_id", room_id).first();
		UserSession user = currentSession();
		if (room == null) {
			returnFailed("no room with id " + room_id);
		}
		room.removeParticipant(user);
		returnOkay();
	}
	
	/**
	 * Use the given power, and notify the relevant users.
	 * @param power_id the id of a {@link StoredPower} to use
	 * @param channel optional, the channel this power is being used in */
	public static void usePower (@Required long power_id, @Required String channel) { 
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    UserSession user = currentSession();
	    UserSession other = currentForSession();
	    
	    if (user == null) {
	        returnFailed("valid session is required");
	    }
	    
        StoredPower storedPower = StoredPower.findById(power_id);
        if (storedPower == null) {
            returnFailed("no power by that ID exists");
        } else if (!storedPower.canUse()) {
            returnFailed("Use Power : You don't have any of that power remaining!");
        }

        SuperPower sp = storedPower.getSuperPower();
        String result = storedPower.use(other != null ? other.user : null);
        
        Pusher pusher = new Pusher();
        UserEvent.UsedPower message = new UserEvent.UsedPower(user.user.id, sp, storedPower.level, result);
	    pusher.trigger(channel, "usedpower", message.toJson());
        returnOkay();
	}
}
package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;
import play.libs.WS;
import play.*;
import play.mvc.*;
import java.lang.reflect.Modifier;
import java.lang.reflect .*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import controllers.*;
import enums.Power;
import models.powers.*;

/**
 * A chat user in the system, who can be online or off, and the maintained meta
 * data relevant to them
 */

@Entity
public class User extends Model {
	/** Amount of time a user can go without heartbeating before they are removed */
	public static final int HEALTHY_HEARTBEAT = 6;
	
	/** A map of all the latest user_ids to heartbeats on this server */
	public static final AbstractMap<Long, Date> heartbeats = new ConcurrentHashMap<Long, Date>();
	
	/** A map of all the latest user_id_room_id to last heartbeat in that room on this server */
	public static final AbstractMap<String, Date> roombeats = new ConcurrentHashMap<String, Date>();
	
	/** list of ids of people waiting to be matched up with someone to chat */
	public static List<Long> waitingRoom = new CopyOnWriteArrayList<Long>();
    
	/**
	 * The user_id, in this case will be the facebook_id
	 */
	@Required	 
    public Long user_id;

	/**
	 * Name of this user */
	@Required
	public String name;
	
    // /**
    //  * The most current faceook token we used for this user
    //  */
    // public String facebook_token;
	
	/**
	 * the avatar to display for this user */
	public String avatar;

    /** String id of the most recent session we have for this user */ 
    public String session_id;
    
	/**
	 * this users alias */
	public String alias;
	
	/**
	 * Collection of other users this user is friends with
	 */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	public Set<User> friends;

    /** 
     * Collection of the superpowers this user has, including
     * ones that have already been used */ 
    @OneToMany(cascade=CascadeType.REMOVE)
    public List<StoredPower> superPowers;
	
	/**
	 * comma delimted list of icebreaker indices seen */
	public String icebreakers_seen;
	
	/**
	 * True if this user is currently online
	 */	
	public boolean online;

    /** total seconds chatting */
    public Long chatTime;  		

    /** total messages sent */
    public int messageCount;  		

    /** total messages received */
    public int gotMessageCount;

    /** total joins by this user */
    public int joinCount;
     
    /** offers to reveal made */
    public int offersMadeCount; 		 

    /** offers to reveal received */
    public int offersReceivedCount;  	 

    /** mutual offers to receive */
    public int revealCount; 			

	/**
	 * The server this user was assigned to heartbeat on
	 */	
	@ManyToOne
	public Server heartbeatServer;
	
	public User (Long u) {
		this.user_id = u;
        // this.facebook_token = "";
		this.online = false;
		this.session_id = "";
		this.chatTime = 0L;
		this.messageCount = 0;
		this.joinCount = 0;
		this.offersMadeCount = 0;
		this.offersReceivedCount = 0;
		this.revealCount = 0;
		this.friends = new HashSet<User>();
	    this.superPowers = new LinkedList<StoredPower>();	
	    this.icebreakers_seen = "";
	    this.save();
	    StoredPower sp = new StoredPower(Power.ICE_BREAKER, this);
        // sp.available = 10;
	    sp.save();
	    this.superPowers.add(sp);
	    
        // sp = new StoredPower(Power.X_RAY_LEVEL_1, this);
        // sp.save();       
        // this.superPowers.add(sp);
        // sp = new StoredPower(Power.MIND_READER, this);
        // sp.save();    
        // this.superPowers.add(sp);
	    
	    this.save();
	}
	
	/**
	 * log this user in, and notify any of their friends that
	 * are online that they are available */	
	public void login () {
		this.online = true;
		for (User friend : this.friends) {
			if (friend.online) {
				friend.notifyMeLogin(this);
			}
		}
	}
	
	/**
	 * log this user out, and notify any of their friends that
	 * are online that they are no longer available */	
	public void logout () {
		this.online = false;
		this.removeMeFromRooms();
		if (this.friends == null) {
			return;
		}
		for (User friend : this.friends) {
			friend.notifyMeLogout(this.id);
		}
		User.removeFromWaitingRoom(this.user_id);
	}	
	
	/**
	 * @return string representation of this user
	 */	
	public String toString () {
		return this.user_id.toString() + " "
			   + (this.heartbeatServer == null ? "no hb server" : this.heartbeatServer.uri) + " "
			   + (this.online ? " - online" : "");
	}
	
	/**
	 * @return the URI of this users heartbeat server */ 
	public String getHeartbeatURI () {
	    return this.heartbeatServer.uri;
	}
	
	/**
	 * A random user who's user_id does not match this object
	 * @return a random user */
	public User getRandom () {
		return User.find(
		    "user_id != ? and online = ? order by rand()", this.user_id, true
		).first();
	}
	
	/**
	 * All the rooms this user is in
	 * @return a list of all the rooms this user is participating in */
	public List<Room> getRooms () {
	    return Room.find(
	        "select distinct r from Room r join r.participants as p where p = ?", this
	    ).fetch();
	}
	
	public HashMap<String, User> updateMyFacebookFriends (String access_token) {
		JsonObject jsonObj = Utility.getMyFacebookFriends(this.user_id, access_token);

		if (jsonObj.has("error")) {
			Logger.error("Failed to log in " + this.user_id + " ( " + jsonObj.toString() + " )");
			return null;
		}
		
		HashMap<String, User> friendData = new HashMap<String, User>();
		
		for (JsonElement ele : jsonObj.get("data").getAsJsonArray()) {
			JsonObject friendObj = ele.getAsJsonObject();
			try {
				Long friendID = friendObj.get("id").getAsLong();
				String friendName = friendObj.get("name").getAsString();
				User friend = User.find("byUser_id", friendID).first();
				if (friend != null && friend.online) {
					this.friends.add(friend);
					friendData.put(friend.id.toString(), friend);
				}
			} catch (Exception e) {
				System.out.println("EXCEPTION " + e);
			}
		}
		
		return friendData;
	}
	
	/**
	 * Check if this user is listening on the master server
	 * @return <code>true</code> if this user's heartbeat server
	 * 		   matches the master servers uri */
	public boolean imOnMaster () {
		return this.heartbeatServer.uri.equals(Server.getMasterServer().uri);
	}
	
	/**
	 * Remove this user from any chat rooms they are in */
	public void removeMeFromRooms () {
		for (Room r : this.getRooms()) {
			if (r.participants.contains(this)) {
				r.removeParticipant(this);
			}
		}
	}

    /**
     * Return a list of all the ice breakers this user has seen 
     * @return list of indices of icebreakers */
    public Set<Integer> getSeenIceBreakers () {
        String[] seen = this.icebreakers_seen.split(",");
        Set<Integer> seenInts = new TreeSet<Integer>();
        for (String s : seen) {
            if (!s.equals("")) {
                seenInts.add(Integer.parseInt(s.trim()));
            }
        }
        return seenInts;
    }
    
    /**
     * Mark the given index as seen by this user 
     * @param i the index of the icebreaker to mark as seen */
    public void addSeenIceBreaker (int i) {
        if (!this.seenIceBreaker(i)) {
            String toAdd = " " + i + ",";            
            this.icebreakers_seen += toAdd;
            this.save();
        }
    }

    /**
     * @param i
     * @return <code>true</code> if this user has seen the icebreaker at index i */
    public boolean seenIceBreaker (int i) {
        String indexStr = " " + i + ",";  
        return this.icebreakers_seen.indexOf(indexStr) > -1;
    }

	/**
	* Count how many {@link StoredPower} instances 
	* @param p the {@link Power} to count
	* @param countUsed flag to indicate whether to include used powers;
	*                  0 => include used and unused
	*                  1 => unused only
	*                  2 => used only
	* @return number of powers matching <code>p</code>
	*/
	public int countPowers (Power p, int countUsed) {
	    StoredPower sp = StoredPower.find("byOwnerAndPower", this, p).first();
	    if (sp == null) {
	        return 0;
	    } else if (countUsed == 0) {
			return sp.used + sp.available;
		} else if (countUsed == 1) {
			return sp.available;
		} else {
		    return sp.used;
		}
	}

    /**
     * This users current level for <code>p</code>
     * @param p the power to check for
     * @return the level of that power for this user, or 0 if they don't have it */
    public int currentLevel (Power p) {
        StoredPower sp = StoredPower.find("byOwnerAndPower", this, p).first();
	    if (sp == null) {
	        return 0;
	    } 
	    return sp.level;
    }

    /**
     * Check all of the super powers and see if I am eligible for any
     * new ones; if so, assign them to me, and put notification events in
     * the stream */
    public void checkForNewPowers () {
        Power[] powers = Power.values(); 
		for (Power power : powers) {
		    SuperPower superPower = power.getSuperPower();
		    superPower.awardIfQualified(this);
		}
    }

    /**
     * Send this user a notification that a super power was used */
    public void notifyUsedPower (Long used_by, Long room_id, SuperPower power, int level, String result) {
		if (!this.imOnMaster()) {
		    HashMap<String, String> params 
		        = Notify.getNotifyUsedPowerParams(this.id, used_by, room_id, power, level, result, this.session_id);
    		notifyMe("usedpower", params);            
        } else {
			new UserEvent.UsedPower(this.id, used_by, room_id, power, level, result, this.session_id);
		}        
    } 
    
	/**
	 * Send this user a notification that they have recieved a new power
	 * @param power the new power they have received */
	public void notifyNewPower (StoredPower power) {
		if (!this.imOnMaster()) {
		    HashMap<String, String> params 
		        = Notify.getNotifyNewPowerParams(this.id, power.getSuperPower(), power.id, power.level, this.session_id);
    		notifyMe("newpower", params);            
        } else {
			new UserEvent.NewPower(this.id, power.getSuperPower(), power.id, power.level, this.session_id);
		}
	}

	/**
	 * Send this user a notification that someone in the room
	 * they were chatting in has left 
	 * @param user the user who left the room
	 * @param room_id the id of the room */
	public void notifyLeftRoom (User user, Long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyLeftParams(this.id, user.id, room_id);
			notifyMe("left", params);   
        } else {
			new UserEvent.Leave(this.id, user.id, room_id, this.session_id);
		}
	}

	/**
	 * Notify this user that someone has joined them in a room
	 * @param otherUser the user joining the room
	 * @param room_id the id of the room being joined */
	public void notifyJoined (User otherUser, Long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyJoinedParams(this.id, 
          	                                   otherUser.id, 
          	                                   otherUser.avatar, 
          	                                   otherUser.alias, 
          	                                   otherUser.heartbeatServer.uri, 
          	                                   room_id, 
          	                                   this.session_id);
			notifyMe("joined", params);   
        } else {
			new UserEvent.Join(this.id, 
							   otherUser.id, 
							   otherUser.avatar,
							   otherUser.alias,
							   otherUser.heartbeatServer.uri,
							   room_id,
							   this.session_id);
		}
	}

	/**
	 * Inform this user that one of their friends has logged out 
	 * @param left_user the user_id of the user who has left */
	public void notifyMeLogout (Long left_user) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyLogoutParams(this.id, left_user);
			notifyMe("logout", params);   
        } else {
			new UserEvent.UserLogout(this.id, left_user, this.session_id);
		}
	}

	/**
	 * Inform this user that one of their friends has logged on
	 * @param newUser the user who has logged on */
	public void notifyMeLogin (User newUser) {
		String name = newUser.name;
		String server = newUser.heartbeatServer.uri;
		if (!this.imOnMaster()) {
			HashMap<String, String> params
			    = Notify.getNotifyLoginParams(this.id, newUser.id, name, server);
			notifyMe("login", params);
	    } else {
			new UserEvent.UserLogon(this.id, newUser.id, name, server, this.session_id);
		}		
	}
	
	/**
	 * Helper for previous notify functions; sends the given parameters
	 * to the /notify/<code>action</code> url of this users heartbeat server
	 * @param action the notify action to take, eg <code>login</code>, <code>logout</code>, etc...
	 * @param params the parameters to pass along to that notified */	
	private void notifyMe (String action, HashMap<String, String> params) {
		String url = this.heartbeatServer.uri + "notify/" + action;
        params.put("session_id", this.session_id);
		WS.HttpResponse resp = Utility.fetchUrl(url, params);
		JsonObject json = resp.getJson().getAsJsonObject();	
		if (!json.get("status").getAsString().equals("okay")) {
		    Logger.error("bad response from notification (%s)", url);
		}	
	}
		
	/**
	 * retrieve and log out the given user
	 * @param user_id the user_id of the user to log out
	 * @return true on success, false if the user is not found */
	public static boolean logOutUser (Long user_id) {
		User user = User.findById(user_id);
		if (user != null) {
			user.removeMeFromRooms();
			user.logout();
			user.save();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Return a user with id <code>user_id</code>, either an 
	 * existing one, or a newly created user with this id
	 * @param user_id
	 * @return user object of matched or created user
	 */
	public static User getOrCreate (Long user_id) {
		User user = User.find("byUser_id", user_id).first();
		if (user == null) {
			user = new User(user_id);
		}
		return user;
	}
		
	/**
	 * Update the heartbeat for the given user in the given room
	 * @param room_id the id of the room to beat in
	 * @param user_id the id of the user to beat for */
	public static void beatInRoom (Long room_id, Long user_id) {
		String key = room_id.toString() + "_" + user_id.toString();
		User.roombeats.put(key, new Date());	
	}
	
    /**
     * Removes all occurences of the given user from the waiting room
     * @param user_id the id to remove from the room */
    public static void removeFromWaitingRoom (Long user_id) {
        while (waitingRoom.contains(user_id)) {
            waitingRoom.remove(user_id);
        }
    }

	/** 
	 * This class is used when serializing and deserializing JSON.  Its only 
	 * purpose is to inform the GsonBuilder objects that they should exclude 
	 * the friends field (since it likely will contain a circular reference) */
	public static class ChatExclusionStrategy implements ExclusionStrategy {

		/** 
		 * Required to implement this, but we dont want to skip
		 * any classes */
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		/**
		 * Indicate which fieds to skip; for now, just friends */
		public boolean shouldSkipField(FieldAttributes f) {
		  return f.getName().equals("friends")
                 || f.getName().equals("user_id")
                 || f.getName().equals("owner");
		}
 	}
		
}
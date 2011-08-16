package models;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.WS;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonObject;

import controllers.Notify;
import enums.Power;

/**
 * A user session is an abstraction for a browser window that the user has open; 
 * Every time a user logs in (refreshes or lands on a chat page), they begin a new
 * UserSession.
 */
@Entity
public class UserSession extends Model {
    
	/** The user_id, in this case will be the facebook_id */
	@ManyToOne
    public User user;
    
    /** Session key for this session */
    public String session;
    
    public UserSession (User u, String s) {
        this.user = u;
        this.session = s;
        this.save();
    }
    
    public boolean equals (Object obj) {
        if (obj == null || 
            !(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession)obj;
        return other.user.equals(user) && other.session.equals(session);
    }
    
    /**
     * Send this user a notification that a super power was used */
    public void sendMessage (UserSession.Faux from, long room_id, String text) {
		if (!this.imOnMaster()) {
		    HashMap<String, String> params 
		        = Notify.getNotifyChatMessageParams(this.user.id, this.session, from.user_id, from.session, text, room_id);
    		notifyMe("roommessage", params);            
        } else {
            logWrongServer();
			UserEvent.get().addRoomMessage(this.user.id, from.user_id, room_id, text, this.session);
		}        
    }

    /**
     * Send this user a notification that a super power was used */
    public void notifyUsedPower (UserSession.Faux from, long room_id, SuperPower power, int level, String result) {
		if (!this.imOnMaster()) {
		    HashMap<String, String> params 
		        = Notify.getNotifyUsedPowerParams(this.user.id, this.session, from.user_id, from.session, room_id, power, level, result);
    		notifyMe("usedpower", params);            
        } else {
            logWrongServer();
			UserEvent.get().addUsedPower(this.user.id, from.user_id, room_id, power, level, result, this.session);
		}        
    } 
    
	/**
	 * Send this user a notification that they have recieved a new power
	 * @param power the new power they have received */
	public void notifyNewPower (StoredPower power) {
		if (!this.imOnMaster()) {
		    HashMap<String, String> params 
		        = Notify.getNotifyNewPowerParams(this.user.id, this.session, power.getSuperPower(), power.id, power.level);
    		notifyMe("newpower", params);            
        } else {
            logWrongServer();            
			UserEvent.get().addNewPower(this.user.id, power.getSuperPower(), power.id, power.level, this.session);
		}
	}

	/**
	 * Send this user a notification that someone in the room
	 * they were chatting in has left 
	 * @param user the user who left the room
	 * @param room_id the id of the room */
	public void notifyLeftRoom (UserSession.Faux from, long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyLeftParams(this.user.id, this.session, from.user_id, from.session, room_id);
			notifyMe("left", params);   
        } else {
            logWrongServer();              
			UserEvent.get().addLeave(this.user.id, from.user_id, room_id, this.session);
		}
	}

	/**
	 * Notify this user that someone has joined them in a room
	 * @param otherUser the user joining the room
	 * @param room_id the id of the room being joined */
	public void notifyJoined (UserSession from, long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyJoinedParams(this.user.id, 
          	    							   this.session, 
          	    							   from.user.id, 
          	    							   from.session,
          	                                   from.user.avatar, 
          	                                   from.user.alias, 
          	                                   from.user.heartbeatServer.uri, 
          	                                   room_id);
			notifyMe("joined", params);   
        } else {
            logWrongServer();              
			UserEvent.get().addJoin(this.user.id, 
									from.user.id, 
									from.user.avatar, 
									from.user.alias, 
									from.user.heartbeatServer.uri,
							        room_id,
							        this.session,
							        from.session);
		}
	}

	/**
	 * Inform this user that one of their friends has logged out 
	 * @param left_user the user_id of the user who has left */
	public void notifyMeLogout (UserSession.Faux from) {
		if (!this.imOnMaster()) {
          	HashMap<String, String> params 
          	    = Notify.getNotifyLogoutParams(this.user.id, this.session, from.user_id, from.session);
			notifyMe("logout", params);   
        } else {
            logWrongServer();              
			UserEvent.get().addUserLogout(this.user.id, from.user_id, this.session);
		}
	}

	/**
	 * Inform this user that one of their friends has logged on
	 * @param newUser the user who has logged on */
	public void notifyMeLogin (UserSession from) {
		String name = from.user.alias;
		String server = from.user.heartbeatServer.uri;
		if (!this.imOnMaster()) {
			HashMap<String, String> params
			    = Notify.getNotifyLoginParams(this.user.id, this.session, from.user.id, from.session, name, server);
			notifyMe("login", params);
	    } else {
			UserEvent.get().addUserLogon(this.id, from.user.id, name, server, this.session, from.session);
		}		
	}
	
	private void logWrongServer () {
	    if (Play.mode != Play.Mode.DEV) {
	        Logger.error("No users should be getting notifications on master");
	    }
	}
	
	/**
	 * Check if this user is listening on the master server
	 * @return <code>true</code> if this user's heartbeat server
	 * 		   matches the master servers uri */
	public boolean imOnMaster () {
        return this.user.heartbeatServer.uri.equals(Server.getMasterServer().uri);
	}
	
	/**
	 * Remove this user from any chat rooms they are in */
	public void removeMeFromRooms () {
	    Set<Room> rooms = this.user.getRooms();
		for (Room r : rooms) {
			if (r.participants.contains(this)) {
				r.removeParticipant(this);
			}
		}
	}	

	/**
	 * Helper for previous notify functions; sends the given parameters
	 * to the /notify/<code>action</code> url of this users heartbeat server
	 * @param action the notify action to take, eg <code>login</code>, <code>logout</code>, etc...
	 * @param params the parameters to pass along to that notified */	
	private void notifyMe (String action, HashMap<String, String> params) {
		String url = this.user.heartbeatServer.uri + "notify/" + action;
		WS.HttpResponse resp = Utility.fetchUrl(url, params);
		JsonObject json = resp.getJson().getAsJsonObject();	
		if (!json.get("status").getAsString().equals("okay")) {
		    Logger.error("bad response from notification (%s)", url);
		}	
	}
	
	/**
	 * log this user out, and notify any of their friends that
	 * are online that they are no longer available */	
	public void logout () {
	    Logger.info(this.user.id + " logging out");
		this.user.online = false;
		this.removeMeFromRooms();
		this.save();
	}
	
	/**
	 * A List of users this user is talking to right now */
	public HashMap<UserSession, Long> getConversants () {
	    Set<Room> roomSet = this.user.getRooms();
	    HashMap<UserSession, Long> users = new HashMap<UserSession, Long>();
	    for (Room r : roomSet) {
            for (UserSession uSess : r.participants) {
                if (uSess.user.id != this.id) {
                    users.put(uSess, r.room_id);
                }
            }
	    }
	    return users;
	}
 
	/**
	 * All the rooms this user is in
	 * @return a list of all the rooms this user is participating in */
	public Set<Room> getRooms () {
	    List<Room> roomList = Room.find(
	        "select distinct r from Room r join r.participants as p where p = ?", this
	    ).fetch();
	    Set<Room> roomSet = new HashSet<Room>(roomList);
	    return roomSet;
	}
	
	/**
	 * All the rooms this user is in
	 * @return a list of all the rooms this user is participating in */
	public Set<Room> getNonGroupRooms () {
	    List<Room> roomList = Room.find(
	        "select distinct r from Room r join r.participants as p where p = ? and r.groupKey = ?", this, ""
	    ).fetch();
	    Set<Room> roomSet = new HashSet<Room>(roomList);
	    return roomSet;
	}
		 
   public String toString () {
       return this.user.id + " (" + this.session + ")";
   }	
	
   public UserSession.Faux toFaux() {
	   return new Faux(this.user.id, this.session);
   }
    
   public static UserSession getFor (long user_id, String session) {
       return UserSession.find("byUser_idAndSession", user_id, session).first();
   }
   
   public static class Faux {
       final public long user_id;
       final public String session;
       public Faux (long u, String s) {
           this.user_id = u;
           this.session = s;
       }
       
       public UserSession toReal () {
           System.out.println("id = " + user_id + ", sess = " + session);
           UserSession sess = UserSession.find("byUser_idAndSession", user_id, session).first();
           if (sess == null) {
               Logger.error("Called toReal() on UserSession.Faux which doesnt map to real object");
           }
    	   return sess;
       }
   }
}
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
import java.util.concurrent.ConcurrentHashMap;
import controllers.*;

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
	
	/**
	 * The user_id, in this case will be the facebook_id
	 */
	@Required	 
    public Long user_id;

	/**
	 * Name of this user */
	@Required
	public String name;
	
	/**
	 * The most current faceook token we used for this user
	 */
	public String facebook_token;
	
	/**
	 * the avatar to display for this user */
	public String avatar;

	/**
	 * this users alias */
	public String alias;
	
	/**
	 * Collection of other users this user is friends with
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	public Set<User> friends;
	
	/**
	 * True if this user is currently online
	 */	
	public boolean online;

	/**
	 * Collection of servers this user is currently participating in chats on,
	 */
	@OneToMany(fetch=FetchType.LAZY)
	public Set<Server> chatServers;

	/**
	 * The server this user was assigned to heartbeat on
	 */	
	@ManyToOne
	public Server heartbeatServer;
	
	public User (Long u) {
		this.user_id = u;
		this.facebook_token = "";
		this.online = false;
		this.friends = new HashSet<User>();
		this.chatServers = new HashSet<Server>();
	}
	
	/**
	 * log this user in, and notify any of their friends that
	 * are online that they are available */	
	public void login () {
		this.online = true;
		for (User friend : this.friends) {
			if (friend.online) {
				friend.notifyMeLogin(this.user_id);
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
			System.out.println(this.user_id + "is logging out but has no friends");
			return;
		}
		for (User friend : this.friends) {
			System.out.println("tell " + friend.user_id + " I'm logging out");
			friend.notifyMeLogout(this.user_id);
		}
		System.out.println("done logging " + this.user_id + " out");
	}	
	
	/**
	 * @return string representation of this user
	 */	
	public String toString () {
		return this.user_id.toString();
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
	 * add the given server to this users collection of chat servers
	 * @param server */
	public void addChatServer (Server server) {
		if (!this.chatServers.contains(server)) {
			System.out.println("adding " + server.name + " to " + this.user_id);
			this.chatServers.add(server);
		}
	}
	
	/**
	 * All the rooms this user is in
	 * @return a list of all the rooms this user is participating in */
	public List<Room> getRooms () {
	    return Room.find(
	        "select distinct r from Room r join r.participants as p where p = ?", this
	    ).fetch();
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
	 * Send this user a notification that someone in the room
	 * they were chatting in has left 
	 * @param user the user who left the room
	 * @param room_id the id of the room */
	public void notifyLeftRoom (User user, Long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("left_user", user.user_id);		
			params.put("room_id", room_id);
			notifyMe("left", params);   
        } else {
			new UserEvent.Leave(this.user_id, user.user_id, room_id);
		}
	}

	/**
	 * Notify this user that someone has joined them in a room
	 * @param otherUser the user joining the room
	 * @param room_id the id of the room being joined */
	public void notifyJoined (User otherUser, Long room_id) {
		if (!this.imOnMaster()) {
          	HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("new_user", otherUser.user_id);		
			params.put("avatar", otherUser.avatar);
			params.put("name", otherUser.alias);
			params.put("room_id", room_id);
			params.put("server", otherUser.heartbeatServer.uri);
			notifyMe("joined", params);   
        } else {
			new UserEvent.Join(this.user_id, 
							   otherUser.user_id, 
							   otherUser.avatar,
							   otherUser.alias,
							   otherUser.heartbeatServer.uri,
							   room_id);
		}
	}

	public void notifyMeLogout (Long left_user) {
		if (!this.imOnMaster()) {
          	HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("left_user", left_user);		
			notifyMe("logout", params);   
        } else {
			new UserEvent.UserLogout(this.user_id, left_user);
		}
	}

	public void notifyMeLogin (Long new_user) {
		User newUser = User.find("byUser_id", new_user).first();
		String name = newUser.name;
		String server = newUser.heartbeatServer.uri;
		if (!this.imOnMaster()) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("new_user", new_user);
			params.put("name", name);
			params.put("server", server);								
			notifyMe("login", params);
	    } else {
			new UserEvent.UserLogon(this.user_id, new_user, name, server);
		}		
	}
	
	public void notifyMe (String action, HashMap<String, Object> params) {
		String url = this.heartbeatServer.uri + "notify/" + action;
		WS.HttpResponse resp = Utility.fetchUrl(url, params);
		JsonObject json = resp.getJson().getAsJsonObject();		
	}
		
	/**
	 * retrieve and log out the given user
	 * @param user_id the user_id of the user to log out
	 * @return true on success, false if the user is not found */
	public static boolean logOutUser (Long user_id) {
		User user = User.find("byUser_id", user_id).first();
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
			user.save();
		}
		return user;
	}
		
	public static class ChatExclusionStrategy implements ExclusionStrategy {

		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		public boolean shouldSkipField(FieldAttributes f) {
		  return f.getName().equals("friends");
		}
 	}
		
}
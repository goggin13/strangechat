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
	 * Collection of other users this user is friends with
	 */
	@ManyToMany(fetch=FetchType.LAZY)
	@Transient
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
		System.out.println("logout " + this.user_id);
		System.out.println(this.friends);
		for (User friend : this.friends) {
			System.out.println("a friend " + friend.user_id + " logs out");
			friend.notifyMeLogout(this.user_id);
		}
	}	
	
	/**
	 * @return string representation of this user
	 */	
	public String toString () {
		return this.user_id.toString();
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

	public void notifyMeLogout (Long left_user) {
		if (Play.mode != Play.Mode.DEV) {
          	HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("left_user", left_user);		
			notifyMe("logout", params);   
        } else {
			new UserEvent.UserLogout(this.user_id, left_user);
		}
	}

	public void notifyMeLogin (Long new_user) {
		if (Play.mode != Play.Mode.DEV) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("for_user", this.user_id);
			params.put("new_user", new_user);		
			notifyMe("login", params);
	    } else {
			new UserEvent.UserLogon(this.user_id, new_user);
		}		
	}
	
	public void notifyMe (String action, HashMap<String, Object> params) {
		String url = this.heartbeatServer.uri + "notify/" + action;
		WS.HttpResponse resp = Utility.fetchUrl(url, params);
		JsonObject json = resp.getJson().getAsJsonObject();		
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
		
}
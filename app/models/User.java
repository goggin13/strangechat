package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;


/**
 * A chat user in the system, who can be online or off, and the maintained meta
 * data relevant to them
 */

@Entity
public class User extends Model {
    public static ArchivedEventStream<UserEvent.AnEvent> userEvents = new ArchivedEventStream<UserEvent.AnEvent>(100);
	
	/**
	 * The user_id, in this case will be the facebook_id
	 */
	@Required	 
    public Long user_id;

	/**
	 * The most current faceook token we used for this user
	 */
	public String facebook_token;
	
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
				new UserEvent.UserLogon(friend.user_id, this.user_id);	
			}
		}
	}
	
	/**
	 * log this user in, and notify any of their friends that
	 * are online that they are available */	
	public void logout () {
		this.online = false;
		System.out.println("logout " + this.user_id);
		System.out.println(this.friends);
		for (User friend : this.friends) {
			System.out.println("a friend " + friend.user_id);
			if (friend.online) {
				new UserEvent.UserLogout(friend.user_id, this.user_id);	
			}
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

	/**
	 * publish an event into the user event stream
	 * @param e the event to publish
	 */	
	public static void publishEvent (UserEvent.AnEvent e) {
		System.out.println("publish event " + e.type);
		User.userEvents.publish(e);
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
	 * Reset the user event queue, flushing out existing events */
	public static void resetEventQueue () {
		User.userEvents = new ArchivedEventStream<UserEvent.AnEvent>(100);
	}
	
}
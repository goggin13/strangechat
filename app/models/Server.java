package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;
import models.*;

import java.lang.reflect .*;
import com.google.gson.reflect.*;
 
/**
 * An instance of a server represents one of the chat servers that
 * the master server will utilize to spawn new chat rooms, and also
 * will be used to assign heart beat servers to.
 */
@Entity
public class Server extends Model {
	
	/** The name of the server, which is also the root domain for that box */
	@Required
	public String name;
	
	/** Number of active rooms on this server */
	public int roomCount;

	/** the next room id to use when we need a new room on this server */
	public Long nextID;
	
	/** 
	 * Servers name
	 * @return string representation of this server 
	 */	
	public String toString () {
		return this.name;
	}
	
	/**
	 * @return return a new room_id and increment the next room id */
	public Long getNextID () {
		Long next = this.nextID;
		this.nextID++;
		this.save();
		return next;
	}
	
	/**
	 * @return a Server instance that this user should use to
	 * to heartbeat against */
	public static Server getMyHeartbeatServer (User user) {
		Server heartbeat = Server.all().first();
		user.heartbeatServer = heartbeat;
		return heartbeat;
	}
	
	/**
	 * @return a Server instance that these users should use to
	 * to chat on */
	public static Server getServerFor (User user1, User user2) {
		Server chatServer = Server.all().first();
		// user1.addChatServer(chatServer);
		// user1.save();
		// user2.addChatServer(chatServer);
		// user2.save();
		return chatServer;
	}
}
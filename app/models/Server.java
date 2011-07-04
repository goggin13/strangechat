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
	
	/** The name of the server */
	@Required
	public String name;
	
	/** URI for the server */
	@Required
	public String uri;
	
	/**
	 * true if this server is the master server */
	public boolean isMaster;
	
	/** Number of active rooms on this server */
	public int roomCount;

	/** the next room id to use when we need a new room on this server */
	public Long nextID = 1L;
	
	/** 
	 * Servers name
	 * @return string representation of this server 
	 */	
	public String toString () {
		return this.name;
	}
	
	/**
	 * Post an event to stream of this server.  Since this
	 * object is of course just meta data for the actual chat server,
	 * we perform a post request in order to get the data in to the server's
	 * event stream */
	public void postEvent (UserEvent.Event e) {
		
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
	 * @Return the master server instance */
	public static Server getMasterServer () {
		Server master = Server.find("byIsMaster", true).first();
		return master;
	}
	
	/**
	 * @return a Server instance that this user should use to
	 * to heartbeat against */
	public static Server getMyHeartbeatServer (User user) {
		Server heartbeat = Server.find("byIsMaster", false).first();
		return heartbeat;
	}
	
	/**
	 * @return a Server instance that these users should use to
	 * to chat on */
	public static Server getServerFor (User user1, User user2) {
		Server chatServer = Server.find("byIsMaster", false).first();
		// user1.addChatServer(chatServer);
		// user1.save();
		// user2.addChatServer(chatServer);
		// user2.save();
		return chatServer;
	}
}
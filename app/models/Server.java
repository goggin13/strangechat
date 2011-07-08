package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;
import models.*;
import play.libs.WS;

import controllers.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
 
/**
 * An instance of a server represents one of the chat servers that
 * the master server will utilize to spawn new chat rooms, and also
 * will be used to assign heart beat servers to.
 */
@Entity
public class Server extends Model {
	private static boolean onMasterServer = false;
	private static boolean onChatServer = false;
	
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
		return this.name + "( " + this.uri + ") - " + (this.isMaster ? "m" : "s");
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
	 * @return true if the current server is a chat server */
	public static boolean imAChatServer () {
		return Server.onChatServer;
	}
	
	/**
	 * @return true if the node we are on is the master server */
	public static boolean onMaster () {
		return Server.onMasterServer;
	}
	
	/**
	 * Mark the current server (e.g. current slot, not current server object),
	 * as being a chat server */
	public static void setChatServer (boolean b) {
		Server.onChatServer = b;
	}

	/**
	 * Mark the current server (e.g. current slot, not current server object),
	 * as being the master server */
	public static void setMasterServer (boolean b) {
		Server.onMasterServer = b;
	}
	
	/**
	 * @return a Server instance that this user should use to
	 * to heartbeat against */
	public static Server getMyHeartbeatServer (User user) {
		Server heartbeat = Server.find("byIsMaster", false).first();
		return heartbeat;
	}
	
}
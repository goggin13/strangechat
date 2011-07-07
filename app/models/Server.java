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
	 * check if this server instance is on the same domain as the current request
	 * @return true if <code>Http.Request.current().host</code> matches this server uri */
	public boolean isCurrent () {
		return uri.indexOf(Index.host()) != -1;
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
	 * @Return a chat server */
	public static Server getAChatServer () {
		Server server = Server.find("byIsMaster", false).first();
		return server;
	}
	
	/**
	 * @return true if the current server is a chat server */
	public static boolean imAChatServer () {
		Long count = Server.count("uri like ? and ismaster = ?", "%" + Index.host() + "%", false);
		return count > 0;
	}
	
	/**
	 * check if the node we are on is the master server
	 * @return true if <code>Http.Request.current().host</code> matches the masters server uri */
	public static boolean onMaster () {
		return getMasterServer().isCurrent();
	}
	
	/**
	 * @return a Server instance that this user should use to
	 * to heartbeat against */
	public static Server getMyHeartbeatServer (User user) {
		Server heartbeat = Server.find("byIsMaster", false).first();
		return heartbeat;
	}
	
}
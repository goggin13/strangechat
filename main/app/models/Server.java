package models;
 
import java.util.List;
import java.util.Random;

import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;
 
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
	
    /** true if this server is a chat server */
    public boolean isChat;	
    
    /** if this is a chat server, represents the overall volume that
     * should be directed to this server; the sum of all the volumes for
     * servers should = 1.0.  Bootstrap.java will log a warning if a .yml 
     * file loads in data which does not adhere to this.   */
    public double volume;
	
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
	 * Return true if this server is the master server */
	public boolean iAmMaster () {
	    return getMasterServer().uri.equals(this.uri);
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
        Random generator = new Random();
        double r = generator.nextDouble();
	    List<Server> heartbeat_servers = Server.find("ischat = ? order by volume asc", true).fetch(100);
	    int len = heartbeat_servers.size();
	    double total = 0;
	    for (int i = 0; i < len; i++) {
	        Server server = heartbeat_servers.get(i);
	        if (r <= (total + server.volume)) {
	            return server;
	        }
	    }
	    Server server = heartbeat_servers.get(len - 1);
	    return server;
	}

	/**
	 * @return a List of chat servers */
	public static List<Server> getChatServers () {
	    return Server.find("byIsChat", true).fetch(100);
	}
}
package controllers;

import play.*;
import play.mvc.*;

import java.util.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import models.*;

/**
 * This controller handles requests for new rooms, and updates information about
 * the servers */
public class Servers extends Index {
	
	/**
	 * <code>user_1</code> requests a room with <code>user_2</code>
	 * Find a server and a room for user_1 and user_2 to use.  Give higher preference
	 * to a server they are both already on, or are both heartbeating on. If either user
	 * is offline, the request is not honored, and an error message is returned.  
	 * If the request is successful, the server and room name will be returned in response
	 * to this request.  In addition, an event is generated for user_2 to indicate they should
	 * start listening to this room.
	 * 
	 * @param user_1 the two users to get the room for
	 * @param user_2 
	 * @param callback optional JSONP callback
	 */
	public static void getRoomFor (Long user_1, Long user_2, String callback) {
		User user1 = User.find("byUser_idAndOnline", user_1, true).first();
		User user2 = User.find("byUser_idAndOnline", user_2, true).first();
		if (user1 == null || user2 == null) {
			returnFailed((user1 == null ? user_1 : user_2) + " is no longer logged in", callback);
		}
		
		Server server = Server.getServerFor(user1, user2);
		Long nextID = server.getNextID();
		Room room = new Room(server, nextID);
		room.participants.add(user1);
		room.participants.add(user2);
		room.save();
		
		new UserEvent.ListenTo(user_2, server.name, nextID);
		
		HashMap<String, String> resp = new HashMap<String, String>();
		resp.put("server", server.name);
		resp.put("room_id", nextID.toString());
		
		renderJSONP(
			resp, 
			new TypeToken<HashMap<String, String>>() {}.getType(),
			callback		
		);
	}
	
 	/**
	 * remove <code>user_id</code> from the room
	 * 
	 * @param server	the name of the server the room belongs to 
	 * @param room_id   the id of the relevant room
	 * @param user_id 	the user who is leaving the room
	 * @param callback	optional JSONP callback
	 */
	public static void leaveRoom (String server, Long room_id, Long user_id, String callback) {
		User user = User.find("byUser_idAndOnline", user_id, true).first();
		if (user == null) {
			returnFailed(user_id + " is no longer logged in", callback);
		}
		Server chatServer = Server.find("byName", server).first();		
		if (chatServer == null) {
			returnFailed("unknown server, " + server, callback);
		}		
		Room room = Room.find("byRoom_idAndServer", room_id, chatServer).first();
		if (room == null) {
			returnFailed("no room " + room_id + " on server " + chatServer.name, callback);
		}
		if (room.participants.contains(user)) {
			room.participants.remove(user);
		}
		returnOkay(callback);
	}
}
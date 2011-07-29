package models;
 
import java.util.*;
import play.libs.F.*;
import play.Logger;
import enums.Power;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class maintains the collection of heartbeats and offers some
 * helper functions */
public class HeartBeat {
	/** Amount of time a user can go without heartbeating before they are removed */
	public static final int HEALTHY_HEARTBEAT = 6;
	
	/** A map of all the latest user_ids to heartbeats on this server */
	public static AbstractMap<Long, Date> heartbeats = new ConcurrentHashMap<Long, Date>();
	
	/** A map of all the latest user_id_room_id to last heartbeat in that room on this server */
	public static AbstractMap<String, Date> roombeats = new ConcurrentHashMap<String, Date>();
	
	/**
	 * Update the heartbeat for the given user in the given room
	 * @param room_id the id of the room to beat in
	 * @param user_id the id of the user to beat for */
	public static void beatInRoom (Long room_id, Long user_id) {
		String key = room_id.toString() + "_" + user_id.toString();
		roombeats.put(key, new Date());	
	}    
	
	public static void beatFor (Long for_user) {
	    heartbeats.put(for_user, new Date());
	}
}
package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.Logger;
import play.db.jpa.*;
import com.google.gson.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A chat room, and the participants contained 
 */
@Entity
public class Room extends Model {
    /** Unique id for rooms */
	public static AtomicLong nextRoomID = new AtomicLong(1);	

	/** Map of user ids to the last 10 people they talked to */
	public static AbstractMap<Long, List<Long>> recentMeetings = new ConcurrentHashMap<Long, List<Long>>();
		
	/** set of users in this chatroom */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	public Set<User> participants;
	
	/** id of this room */
	@Required
	public Long room_id;
	
	public Room (Long room_id) {
		this.room_id = room_id;
		this.participants = new HashSet<User>();
	}

    /**
     * Add the two given users to this room, and notify them that eachother
     * has joined 
     * @param user_id1
     * @param user_id2 */
	public void addUsers (Long user_id1, Long user_id2) {
    	User user1 = User.find("byUser_id", user_id1).first();
    	User user2 = User.find("byUser_id", user_id2).first();
    	this.participants.add(user1);
    	this.participants.add(user2);
    	this.save();
    	user1.notifyJoined(user2, room_id);
    	user2.notifyJoined(user1, room_id);
	    Room.updateRecentMeetingsFor(user1.user_id, user2.user_id);
	    Room.updateRecentMeetingsFor(user2.user_id, user1.user_id);	    
	}
			
	/**
	 * Remove a user from this room, and notify the other participants they have left
	 * @param user the user to remove */
	public void removeParticipant (User user) {
		if (!this.participants.contains(user)) {
		    System.out.println("not in room");
			return;
		}
		this.participants.remove(user);
		if (this.participants.size() > 0) {
			this.save();
			for (User u : this.participants) {
		    System.out.println("tell " + u.user_id + " I peaced " + room_id);
				u.notifyLeftRoom(user, room_id);
			}			
		} else {
			this.delete();
		}		
	}

    /**
     * Check if the two users have met with one another in the last
     * <code>n</code> attempts
     * @param user1
     * @param user2
     * @param n the number of meetings ago to consider, 0 indexed. */
    public static boolean hasMetRecently (Long user_id1, Long user_id2, int n) {
        int meeting1 = Room.lastMeetingBetween(user_id1, user_id2);
        int meeting2 = Room.lastMeetingBetween(user_id2, user_id1);        
        return (meeting1 > -1 && meeting1 <= n)
               || (meeting2 > -1 && meeting2 <= n); 
    }
    
    /**
     * How many interactions ago <code>user_id1</code> met with <code>met_with_id</code>
     * @param user_id
     * @param met_with_id
     * @return if user_id spoke with met_with_id, then 3 other users, returns 3, e.g.;
     *         = -1 if user_id never spoke with them */
    public static int lastMeetingBetween (Long user_id, Long met_with_id) {
        if (!Room.recentMeetings.containsKey(user_id)) {
            return -1;
        }
        List<Long> meetings = Room.recentMeetings.get(user_id);
        int index = meetings.indexOf(met_with_id);
        return index == -1 ? -1 : meetings.size() - 1 - index;
    }

	/**
	 * Update this users list of recent meetings, adding the given user id
	 * @param user_id the user to update the meetings for
	 * @param met_with_id the user id of the user they met with */
	public static void updateRecentMeetingsFor (Long user_id, Long met_with_id) {
        List<Long> meetings;

        if (Room.recentMeetings.containsKey(user_id)) {
            meetings = Room.recentMeetings.get(user_id);
        } else {
            meetings = new LinkedList<Long>();
        }
        meetings.add(met_with_id);
        Room.recentMeetings.put(user_id, meetings);
	}

	/**
	 * Create a new room for the two given users.  Notify both of the users
	 * after the room is created.
	 * @param user_id1
	 * @param user_id2 */
	public static void createRoomFor (Long user_id1, Long user_id2) {
    	Long room_id = nextRoomID.incrementAndGet();
    	Room r = new Room(room_id);
    	r.addUsers(user_id1, user_id2);	    
	}
	
	/**
	 * Remove the given user from the given room
	 * @param user_id the user_id of the User to remove from the room
	 * @param room_id the id of the room to remove from
	 * @return true on success, false if user_id or room_id does not exist */
	public static boolean removeUserFrom (Long room_id, Long user_id) {
		Room room = Room.find("byRoom_id", room_id).first();
		User user = User.find("byUser_id", user_id).first();
		if (user == null) {
			return false;
		}
		if (room == null) {
			return false;
		}
		room.removeParticipant(user);
		return true;
	}
	
}
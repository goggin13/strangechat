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

	/** set of users in this chatroom */
	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	public Set<User> participants;
	
	/** id of this room */
	@Required
	public long room_id;
	
	public Room (long room_id) {
		this.room_id = room_id;
		this.participants = new HashSet<User>();
	}

    /**
     * Return <code>True</code> if this room has no participants in it */
    public boolean isEmpty () {
        if (this.participants.size() == 0) {
            return true;
        } else {
            boolean active = false;
            for (User p : this.participants) {
                if (p.online) {
                    active = true;
                }
            }
            return active;
        }
    }

    /**
     * Add the two given users to this room, and notify them that eachother
     * has joined 
     * @param user_id1
     * @param user_id2 */
	public void addUsers (long user_id1, long user_id2) {
        User user1 = User.findById(user_id1);
        User user2 = User.findById(user_id2);
        this.participants.add(user1);
        this.participants.add(user2);     
        this.save();
        user1.notifyJoined(user2, room_id);
        user2.notifyJoined(user1, room_id);
        Room.updateRecentMeetingsFor(user1.id, user2.id);
        Room.updateRecentMeetingsFor(user2.id, user1.id);        
	}
			
	/**
	 * Remove a user from this room, and notify the other participants they have left
	 * @param user the user to remove */
	public void removeParticipant (User user) {
		if (!this.participants.contains(user)) {
			return;
		}
		this.participants.remove(user);
		if (this.participants.size() > 0) {
			this.save();
			for (User u : this.participants) {
				u.notifyLeftRoom(user, room_id);
                user.notifyLeftRoom(u, room_id);
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
     * @param n the number of meetings ago to consider as "recently", 0 indexed. */
    public static boolean hasMetRecently (long user_id1, long user_id2, int n) {
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
    public static int lastMeetingBetween (long user_id, long met_with_id) {
        User user = User.findById(user_id);
        User met_with = User.findById(met_with_id);
        List<User> recentMeetings = user.recentMeetings;
        int index = recentMeetings.indexOf(met_with);
        return index == -1 ? -1 : recentMeetings.size() - 1 - index;
    }

	/**
	 * Update this users list of recent meetings, adding the given user id
	 * @param user_id the user to update the meetings for
	 * @param met_with_id the user id of the user they met with */
	public static void updateRecentMeetingsFor (long user_id, long met_with_id) {
        User user = User.findById(user_id);
        User met_with = User.findById(met_with_id);
        
        user.recentMeetings.add(met_with);
        if (user.recentMeetings.size() > 10) {
            user.recentMeetings.remove(0);
        }
        user.save();
	}

    /**
     * Check if the 2 given users are currently speaking together in a room */
    public static boolean areSpeaking (long u1, long u2) {
        User user1 = User.findById(u1);
        User user2 = User.findById(u2);
        Set<Room> rooms_1 = user1.getRooms();
        rooms_1.retainAll(user2.getRooms());
        return rooms_1.size() > 0;
    }

	/**
	 * Create a new room for the two given users.  Notify both of the users
	 * after the room is created.
	 * @param user_id1
	 * @param user_id2 */
	public static void createRoomFor (long user_id1, long user_id2) {
    	long room_id = nextRoomID.incrementAndGet();
    	Room r = new Room(room_id);
    	r.addUsers(user_id1, user_id2);	    
	}
	
	/**
	 * Remove the given user from the given room
	 * @param user_id the user_id of the User to remove from the room
	 * @param room_id the id of the room to remove from
	 * @return true on success, false if user_id or room_id does not exist */
	public static boolean removeUserFrom (long room_id, long user_id) {
		Room room = Room.find("byRoom_id", room_id).first();
		User user = User.findById(user_id);
		if (user == null) {
			return false;
		}
		if (room == null) {
			return false;
		}
		room.removeParticipant(user);
		return true;
	}
	
	public String toString () {
	    return this.id + " => ("+ this.participants.toString() + ")";
	}
	
}
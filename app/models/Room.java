package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;


/**
 * A chat room, and the participants contained 
 */
@Entity
public class Room extends Model {
		
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
			}			
		} else {
			this.delete();
		}		
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
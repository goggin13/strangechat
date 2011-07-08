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
	@ManyToMany(fetch=FetchType.LAZY)
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
	
}
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
	
	/** the server this chatroom lives on */
	@Required
	@ManyToOne
	public Server server;
	
	/** set of users in this chatroom */
	@ManyToMany(fetch=FetchType.LAZY)
	public Set<User> participants;
	
	/** id of this room */
	@Required
	public Long room_id;
	
	public Room (Server server, Long room_id) {
		this.server = server;
		this.room_id = room_id;
		this.participants = new HashSet<User>();
	}
	
}
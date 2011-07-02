package models;
 
import java.util.*;
import play.libs.F.*;

/* 
 * A wrapper for the UserEvent classes.  See comment on {@link AnEvent} for 
 * more detail 
 */
public class UserEvent {
	
	/**
	 * UserEvents are dropped into the {@link User}.userEvents queue, which 
	 * is listened to by chat clients for notifications, including friends signing on
	 * or leaving, or new chat rooms to listen to.  These events will also be served
	 * serialized into JSON objects.  They are immediately pushed into the User.userEvents
	 * stream when they are created.  
	 */
	public static abstract class AnEvent {	
		/**
		 * A string describing the type of the event
		 */
		final public String type;
	
		/**
		 * the user_id this event is pertintent to
		 */
		final public Long user_id;

		/**
		 * timestamp the event was created
		 */
		final public Long timestamp;
	
		public AnEvent (String type, Long user_id) {
	        this.type = type;
			this.user_id = user_id;
	        this.timestamp = System.currentTimeMillis();
			User.publishEvent(this);
	    }
	}
	
	/**
	 * This event indicates to clients that they should begin
	 * listening the described server and room_id.  When ClientA
	 * initiates a chat with ClientB, ClientB will receieve an event of this 
	 * type.
	 */
	public static class ListenTo extends AnEvent {
		public final String server;
		public final Long room_id;
		
		public ListenTo (Long user_id, String server, Long room_id) {
			super("listento", user_id);
			this.server = server;
			this.room_id = room_id;
		}
	}
}
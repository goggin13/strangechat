package models;
 
import java.util.*;
import play.libs.F.*;
import play.Logger;

/* 
 * A wrapper for the UserEvent classes.  See comment on {@link AnEvent} for 
 * more detail */
public class UserEvent {
	public static ArchivedEventStream<UserEvent.Event> userEvents = new ArchivedEventStream<UserEvent.Event>(100);
	
	/**
	 * UserEvents are dropped into the userEvents queue, which 
	 * is listened to by chat clients for notifications, including friends signing on
	 * or leaving, or new chat rooms to listen to.  These events will also be served
	 * serialized into JSON objects.  They are immediately pushed into the userEvents
	 * stream when they are created.  */
	public static abstract class Event {	
		/** A string describing the type of the event */
		final public String type;
	
		/** the user_id this event is pertintent to */
		final public Long user_id;

		/** timestamp the event was created */
		final public Long timestamp;
	
		public Event (String type, Long user_id) {
	        this.type = type;
			this.user_id = user_id;
	        this.timestamp = System.currentTimeMillis();
	    }
	
		public String toString () {
			return this.user_id + " ( " + this.type + " )";
		}
	}
	
	/**
	 * This event indicates to clients that this user has just logged
	 * into the system and is ready to chat */
	public static class UserLogon extends Event {
		/** The user id of the user who just logged on */
		public final Long new_user;
		/** the name of the user logging on */
		public final String name;
		/** server this user is located on */
		final public String server;
		
		public UserLogon (Long user_id, Long new_user, String name, String server) {
			super("userlogon", user_id);
			this.new_user = new_user;
			this.name = name;
			this.server = server;
			userEvents.publish(this);
		}
		
		public String toString () {
			return super.toString() + " : " + this.new_user + " has logged in ";
		}		
	}	
	
	/**
	 * This event indicates to clients that this user has just logged
	 * out of the system */
	public static class UserLogout extends Event {
		/** The user id of the user who just logged out */
		public final Long left_user;
		
		public UserLogout (Long user_id, Long left_user) {
			super("userlogout", user_id);
			this.left_user = left_user;
			userEvents.publish(this);
		}
		
		public String toString () {
			return super.toString() + " : " + this.left_user + " has logged out ";
		}		
	}	
	
	/**
	 * Represents a direct message from one user to another (not in a chatroom) */
    public static class DirectMessage extends Event {
		/** the user_id of the user who sent the message */
        public final Long from;
		/** the text of the message */
 		public final String text;
		
        public DirectMessage(Long to, Long from, String msg) {
            super("directmessage", to);
            this.from = from;
            this.text = msg;
			userEvents.publish(this);
        }

		public String toString () {
			return super.toString() 
				   + " : message from " + this.from + ", " + this.text;
		}
        
    }
	    
	/**
	 * Represents a direct message from one user to another in a chatroom */
    public static class RoomMessage extends Event {
		/** the user_id of the user who sent the message */
        public final Long from;
		/** the text of the message */
 		public final String text;
		/** optional room id that this message is pertinent to */
		public final Long room_id;
		
        public RoomMessage(Long to, Long from, Long room_id, String msg) {
            super("roommessage", to);
            this.from = from;
            this.text = msg;
			this.room_id = room_id;
			userEvents.publish(this);
        }

		public String toString () {
			return super.toString() 
				   + " : message from " + this.from + ", " + this.text
				   + ", in room " + this.room_id;
		}
        
    }
	
	/** 
	 * represents a user joining the chat room */
    public static class Join extends Event {
        /** the user id of the joining user */
        public final Long new_user;
        /** an optional url displaying this new users avatar */
		public final String avatar;
		/** the name of the user joining */
		public final String alias;
		/** the server the new user is on */
		public final String server;
		/** the room id that you are now chatting in */
		public final Long room_id;
		
        public Join (Long for_user, Long new_user, String avatar, String name, String server, Long room_id) {
			super("join", for_user);
            this.new_user = new_user;
			this.avatar = avatar;
			this.alias = name;
			this.server = server;
			this.room_id = room_id;
			userEvents.publish(this);
        }

		public String toString () {
			return super.toString() + " : " + this.new_user + " is joining room " + this.room_id;
		}
        
    }

	/**
	 * Indicates that the user is typing */
	public static class UserIsTyping extends Event {
        /** user id of the user leaving */
        final public Long typing_user;
        /** room id the event is for */
		final public Long room_id;
		
        public UserIsTyping (Long for_user, Long typing_user, Long room_id) {
            super("useristyping", for_user);
            this.typing_user = typing_user;
			this.room_id = room_id;
			userEvents.publish(this);
        }

		public String toString () {
			return super.toString() + " : " + this.typing_user + " is typing all up in room " + this.room_id;
		}		
	}

	/**
	 * Represents a users heartbeat in a room */
    public static class Test extends Event {		
        public Test () {
            super("nothing", -1L);
			userEvents.publish(this);
        }
    }
    
	/**
	 * Represents a user leaving the chat room */
    public static class Leave extends Event {
        /** user id of the user leaving */
        final public Long left_user;
        /** room id that was left */
		final public Long room_id;
		
        public Leave(Long for_user, Long left_user, Long room_id) {
            super("leave", for_user);
            this.left_user = left_user;
			this.room_id = room_id;
			userEvents.publish(this);
        }

		public String toString () {
			return super.toString() + " : " + this.left_user + " left room " + this.room_id;
		}
    }
	
	/**
     * For long polling, as we are sometimes disconnected, we need to pass 
     * the last event seen id, to be sure to not miss any message.  Gets the
	 * messages that have been published to the event stream for the chat room
	 * @param lastReceived	the id of the last message the caller has seen.  Messages
	 * 						with ids greater than lastReceived are returned
	 * @return list of messages with ids > lastReceived
     */
    public static Promise<List<IndexedEvent<UserEvent.Event>>> nextMessages (long lastReceived) {
        return userEvents.nextEvents(lastReceived);
    }
	
	/**
     * Just used for admin purposes, return entire event stream
	 * @return list of all messages in the queue
     */
    public static List<UserEvent.Event> currentMessages () {
		List<UserEvent.Event> events = userEvents.archive();
		Collections.reverse(events);
        return events;
    }
	
	/**
	 * Reset the user event queue, flushing out existing events */
	public static void resetEventQueue () {
		UserEvent.userEvents = new ArchivedEventStream<UserEvent.Event>(100);
	}
}
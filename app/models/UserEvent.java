package models;
 
import java.util.*;
import play.libs.F.*;
import play.Logger;
import enums.Power;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;

/* 
 * A wrapper for the UserEvent classes.  See comment on {@link AnEvent} for 
 * more detail */
public class UserEvent {
    private static final int streamSize = 10000;
	public static ArchivedEventStream<UserEvent.Event> userEvents = new ArchivedEventStream<UserEvent.Event>(streamSize);
	public static ArchivedEventStream<UserEvent.Event> adminEvents = new ArchivedEventStream<UserEvent.Event>(streamSize);	
	
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
		
		/** current session id, optional */
		final public String session_id;		
		
		public Event (String type, Long user_id, String session_id) {
	        this.type = type;
			this.user_id = user_id;
			this.session_id = session_id;
	        this.timestamp = System.currentTimeMillis();
	    }
	
	    public void publishMe () {
	        System.out.println(this);
	        userEvents.publish(this);
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
		
		public UserLogon (Long user_id, Long new_user, String name, String server, String session_id) {
			super("userlogon", user_id, session_id);
			this.new_user = new_user;
			this.name = name;
			this.server = server;
			publishMe();
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
		
		public UserLogout (Long user_id, Long left_user, String session_id) {
			super("userlogout", user_id, session_id);
			this.left_user = left_user;
			publishMe();
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
            super("directmessage", to, "");
            this.from = from;
            this.text = msg;
			publishMe();
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
            super("roommessage", to, "");
            this.from = from;
            this.text = msg;
			this.room_id = room_id;
			publishMe();
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
		
        public Join (Long for_user, Long new_user, String avatar, String name, String server, Long room_id, String session_id) {
			super("join", for_user, session_id);
            this.new_user = new_user;
			this.avatar = avatar;
			this.alias = name;
			this.server = server;
			this.room_id = room_id;

			// as soon as this event is created, we heartbeat for the given user; if they never received this event,
			// we see their heartbeat fail and notify the other user
            User.beatInRoom(room_id, this.user_id);		
			publishMe();
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
		/** the text they have typed so far */
		final public String text;
		
        public UserIsTyping (Long for_user, Long typing_user, String text, Long room_id) {
            super("useristyping", for_user, "");
            this.typing_user = typing_user;
			this.room_id = room_id;
			this.text = text;
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + this.typing_user + " is typing all up in room " + this.room_id;
		}		
	}

	/**
	 * Indicates that the user is heartbeating; only used for admin tracking purposes */
	public static class HeartBeat extends Event {
        /** user id of the user heart beating */
        final public Long for_user_id;
		
        public HeartBeat (Long for_user) {
            super("heartbeat", -1L, "");
            this.for_user_id = for_user;
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + "heartbeat";
		}		
	}

	/**
	 * Dummy event, seems to help keep things popping off stack */
    public static class KeepItMoving extends Event {		
        public KeepItMoving () {
            super("nothing", -1L, "");
			publishMe();
        }
    }
    
	/**
	 * Represents a user leaving the chat room */
    public static class Leave extends Event {
        /** user id of the user leaving */
        final public Long left_user;
        /** room id that was left */
		final public Long room_id;
		
        public Leave(Long for_user, Long left_user, Long room_id, String session_id) {
            super("leave", for_user, session_id);
            this.left_user = left_user;
			this.room_id = room_id;
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + this.left_user + " left room " + this.room_id;
		}
    }
	
	/** 
	 * Notifies a user that they have recieved a new super power */
	public static class NewPower extends Event {
   	    /** db id of the power for when they use it */
	    final public Long power_id;
	    /** details about the super power */
	    final public SuperPower superPower;
	    
	    public NewPower (Long for_user, SuperPower sp, Long power_id, String session_id) {
	        super("newpower", for_user, session_id);
	        this.superPower = sp;
	        this.power_id = power_id;
	        publishMe();
	    }
	    
		public String toString () {
			return super.toString() + " : " + this.superPower.name + " awarded";
		}	    
	}

    /**
     * Takes a JSON string and deserializes it into a list of events 
     * @param jsonStr the string of JSON to convert
     * @return list of deserialized events */
    public static List<UserEvent.Event> deserializeEvents (String jsonStr) {
        Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SuperPower.class, new SuperPowerDeserializer())
                    .create();
                    
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(jsonStr).getAsJsonArray();

        List<UserEvent.Event> events = new LinkedList<UserEvent.Event>();
        for (JsonElement e : array) {
            String type = e.getAsJsonObject().get("type").getAsString();
            Type t = typeStringToType(type);
            if (t != null) {
                UserEvent.Event event = gson.fromJson(e, t);
                events.add(event);
            }
        }     
        return events;
    }
	    
	/**
	 * Get the type token, used for JSON serializing, from the string
	 * describing the type of the class 
	 * @param type description of one of the inner classes of UserEvent
	 * @return the appropriate TypeToken, or null if there was no match */
    public static Type typeStringToType (String type) {
        Type t = null;
        
        if (type.equals("heartbeat")) {
            t = new TypeToken<UserEvent.HeartBeat>() {}.getType();
        } else if (type.equals("join")) {
            t = new TypeToken<UserEvent.Join>() {}.getType();   
        } else if (type.equals("leave")) {
            t = new TypeToken<UserEvent.Leave>() {}.getType(); 
        } else if (type.equals("useristyping")) {
            t = new TypeToken<UserEvent.UserIsTyping>() {}.getType(); 
        } else if (type.equals("userlogon")) {
            t = new TypeToken<UserEvent.UserLogon>() {}.getType(); 
        } else if (type.equals("userlogout")) {   
            t = new TypeToken<UserEvent.UserLogout>() {}.getType();                                                                           
        } else if (type.equals("directmessage")) {  
            t = new TypeToken<UserEvent.DirectMessage>() {}.getType();                                                                                            
        } else if (type.equals("roommessage")) {
            t = new TypeToken<UserEvent.RoomMessage>() {}.getType(); 
        } else if (type.equals("newpower")) {    
            t = new TypeToken<UserEvent.NewPower>() {}.getType();             
        }
        
        return t;
    }
	
	/**
	 */
	public static class SuperPowerDeserializer implements JsonDeserializer<SuperPower> {
	    public SuperPower deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String name = json.getAsJsonObject().get("name").getAsString();
            return Power.valueOf(name.replace(' ', '_').toUpperCase()).getSuperPower();
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
		System.out.println("currently " + events.size());
        return events;
    }
	
	/**
	 * @return the id of the message at the top of the current event queue */
	public static Long lastID () {
		List<IndexedEvent> events = userEvents.availableEvents(0L);
		if (events.size() > 0) {
			return events.get(events.size() - 1).id;			
		} else {
			return 0L;
		}

	}
	
	/**
	 * Reset the user event queue, flushing out existing events */
	public static void resetEventQueue () {
		UserEvent.userEvents = new ArchivedEventStream<UserEvent.Event>(streamSize);
	}
}
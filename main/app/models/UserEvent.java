package models;
 
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.AbstractMap;

import play.Logger;
import play.libs.F.ArchivedEventStream;
import play.libs.F.IndexedEvent;
import play.libs.F.Promise;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import enums.Power;
import controllers.Index;

/* 
 * A wrapper for the UserEvent classes.  See comment on {@link Event} for 
 * more detail */
public class UserEvent {
    private static final int adminStreamSize = 2000;
    private static final int userStreamSize = 100;
    final private ArchivedEventStream<UserEvent.Event> adminEvents = new ArchivedEventStream<UserEvent.Event>(adminStreamSize);
	private static AbstractMap<Long, ArchivedEventStream<UserEvent.Event>> eventStreams = 
	        new ConcurrentHashMap<Long, ArchivedEventStream<UserEvent.Event>>();
	        
	/**
     * For long polling, as we are sometimes disconnected, we need to pass 
     * the last event seen id, to be sure to not miss any message.  Gets the
	 * messages that have been published to the event stream for the chat room
	 * @param lastReceived	the id of the last message the caller has seen.  Messages
	 * 						with ids greater than lastReceived are returned
	 * @return list of messages with ids > lastReceived
     */
    public Promise<List<IndexedEvent<UserEvent.Event>>> nextEvents (long user_id, long lastReceived) {
        ArchivedEventStream<UserEvent.Event> stream = getOrCreateStream(user_id);
        Promise<List<IndexedEvent<UserEvent.Event>>> nextEvents = stream.nextEvents(lastReceived);
        return nextEvents;
    }

	/**
     * Just used for admin purposes, return entire event stream
	 * @return list of all messages in the queue
     */
    public List<UserEvent.Event> currentMessages () {
		List<UserEvent.Event> events = adminEvents.archive();
		Collections.reverse(events);
        return events;
    }
    
    public List<IndexedEvent> availableEvents (long lastReceived) {
        return adminEvents.availableEvents(lastReceived);
    }

    public void publish (Event e) {
        adminEvents.publish(e);
    }

    private ArchivedEventStream<UserEvent.Event> getOrCreateStream (long user_id) {
        ArchivedEventStream<UserEvent.Event> eventStream;
        if (eventStreams.containsKey(user_id)) {
          // System.out.println("return existing stream for " + user_id);
          eventStream = eventStreams.get(user_id);  
        } else {
          // System.out.println("start new stream for " + user_id);
          eventStream = new ArchivedEventStream<UserEvent.Event>(userStreamSize); 
          eventStreams.put(user_id, eventStream);
        }
        return eventStream;
    }

	/**
	 * Reset the user event queue, flushing out existing events */
    public void resetEventQueue () {
        for (int i = 0; i < adminStreamSize; i++) {
            publish(new DummyEvent());
        }
        eventStreams.clear();
    }

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
		final public long from;

		/** timestamp the event was created */
		final public long timestamp;
		
		/** current session id, optional */
		final public String session_id;		
		
		public Event (String type, long user_id, String session_id) {
	        this.type = type;
			this.from = user_id;
			this.session_id = session_id;
	        this.timestamp = System.currentTimeMillis();
	    }
	
	    public void publishMe () {
            UserEvent.get().publish(this);
	    }
	    
		public String toString () {
			return this.from + " - " + this.session_id + " ( " + this.type + " )";
		}
		
	}
	
	/**
	 * This event indicates to clients that this user has just logged
	 * into the system and is ready to chat */
	public static class UserLogon extends Event {
		/** The user id of the user who just logged on */
		public final long new_user;
		/** the name of the user logging on */
		public final String name;
		/** server this user is located on */
		final public String server;
		/** the other user's session */
		public final String new_session;		
		
		public UserLogon (long user_id, long new_user, String name, String server, String session_id, String new_session) {
			super("userlogon", user_id, session_id);
			this.new_user = new_user;
			this.name = name;
			this.server = server;
			this.new_session = new_session;
			publishMe();
		}
		
		public String toString () {
			return super.toString() + " : " + this.new_user + " has logged in ";
		}		
	}	
	
	public void addUserLogon (long user_id, long new_user, String name, String server, String session_id, String new_session) {
        new UserLogon(user_id, new_user, name, server, session_id, new_session);
	}
	
	/**
	 * This event indicates to clients that this user has just logged
	 * out of the system */
	public static class UserLogout extends Event {
		/** The user id of the user who just logged out */
		public final long left_user;
		
		public UserLogout (long user_id, long left_user, String session_id) {
			super("userlogout", user_id, session_id);
			this.left_user = left_user;
			publishMe();
		}
		
		public String toString () {
			return super.toString() + " : " + this.left_user + " has logged out ";
		}		
	}	
	
	public void addUserLogout (long user_id, long left_user, String session_id) {
        new UserLogout(user_id, left_user, session_id);
	}
	
	/**
	 * Represents a direct message from one user to another (not in a chatroom) */
    public static class DirectMessage extends Event {
		/** the user_id of the user who sent the message */
        public final long from;
		/** the text of the message */
 		public final String text;
		
        public DirectMessage(long to, long from, String msg, String session) {
            super("directmessage", to, session);
            this.from = from;
            this.text = msg;
			publishMe();
        }

		public String toString () {
			return super.toString() 
				   + " : message from " + this.from + ", " + this.text;
		}
        
    }
	    
	public void addDirectMessage (long to, long from, String msg, String session) {
        new DirectMessage(to, from, msg, session);
	}    
	    
	/**
	 * Represents a direct message from one user to another in a chatroom */
    public static class RoomMessage extends Event {
		/** the text of the message */
 		public final String text;
		
        public RoomMessage(long from, String msg) {
            super("roommessage", from, "");
            this.text = msg;
			publishMe();
        }

		public String toString () {
			return super.toString() 
				   + " : message from " + this.from + ", " + this.text;
		}
		
        // protected void finalize() {
            // Logger.info("finalising RoomMessage class");
        // }        
        
    }
	
	public void addRoomMessage (long from, String msg) {
        new RoomMessage(from, msg);
	}	
	
	/** 
	 * represents a user joining the chat room */
    public static class Join extends Event {
        /** the user id of the joining user */
        public final long new_user;
        /** an optional url displaying this new users avatar */
		public final String avatar;
		/** the other user's session */
		public final String new_session;
		/** the name of the user joining */
		public final String alias;
		/** the server the new user is on */
		public final String server;
		/** the room id that you are now chatting in */
		public final long room_id;
		
        public Join (long for_user, long new_user, String avatar, String name, String server, long room_id, String session_id, String new_session) {
			super("join", for_user, session_id);
            this.new_user = new_user;
			this.avatar = avatar;
			this.alias = name;
			this.server = server;
			this.room_id = room_id;
			this.new_session = new_session;

			// as soon as this event is created, we heartbeat for the given user; if they never received this event,
			// we see their heartbeat fail and notify the other user
            models.HeartBeat.beatInRoom(room_id, for_user, session_id);    
            	
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + this.new_user + " is joining room " + this.room_id;
		}
        
    }

	public void addJoin (long for_user, 
						 long new_user, 
						 String avatar, 
						 String name, 
						 String server, 
						 long room_id, 
						 String session_id,
						 String other_session) {
        new Join(for_user, new_user, avatar, name, server, room_id, session_id, other_session);
	}

	/**
	 * Indicates that the user is typing */
	public static class UserIsTyping extends Event {
        /** user id of the user leaving */
        final public long typing_user;
        /** room id the event is for */
		final public long room_id;
		/** the text they have typed so far */
		final public String text;
		
        public UserIsTyping (long for_user, long typing_user, String text, long room_id, String session) {
            super("useristyping", for_user, session);
            this.typing_user = typing_user;
			this.room_id = room_id;
			this.text = text;
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + this.typing_user + " is typing all up in room " + this.room_id;
		}		
	}

	public void addUserIsTyping (long for_user, long typing_user, String text, long room_id, String session) {
        new UserIsTyping(for_user, typing_user, text, room_id, session);
	}

	/**
	 * Indicates that the user is heartbeating; only used for admin tracking purposes */
	public static class HeartBeat extends Event {
        /** user id of the user heart beating */
        final public long for_user_id;
		
        public HeartBeat (long for_user) {
            super("heartbeat", -1L, "");  // -1 so we don't bother sending this back to anyone
            this.for_user_id = for_user;
            publishMe();
        }

		public String toString () {
			return super.toString() + " : " + "heartbeat - " + for_user_id;
		}		
	}

	public void addHeartBeat (long for_user) {
        new HeartBeat(for_user);
	}

	/**
	 * Represents a user leaving the chat room */
    public static class Leave extends Event {
        /** user id of the user leaving */
        final public long left_user;
        /** room id that was left */
		final public long room_id;
		
        public Leave(long for_user, long left_user, long room_id, String session_id) {
            super("leave", for_user, session_id);
            this.left_user = left_user;
			this.room_id = room_id;
			publishMe();
        }

		public String toString () {
			return super.toString() + " : " + this.left_user + " left room " + this.room_id;
		}
    }
	
	public void addLeave (long for_user, long left_user, long room_id, String session_id) {
        new Leave(for_user, left_user, room_id, session_id);
	}	
	
	/** 
	 * Notifies a user that they have recieved a new super power */
	public static class NewPower extends Event {
	    /** details about the super power */
	    final public SuperPower superPower;
	    /** details about the super power */
	    final public StoredPower storedPower;	    
	    
	    public NewPower (long for_user, StoredPower stored, String session_id) {
	        super("newpower", for_user, session_id);
	        this.superPower = stored.getSuperPower();
	        this.storedPower = stored;
	    }

	    public String toJson () {
    		GsonBuilder gsonBuilder = new GsonBuilder().setExclusionStrategies(new User.ChatExclusionStrategy());
    		Gson gson = gsonBuilder.create();
            String json = gson.toJson(
                this,
                new TypeToken<NewPower>() {}.getType()
            );
            return json;
	    }
	    
		public String toString () {
			return super.toString() + " : " + this.superPower.name + " awarded (L " + this.storedPower.level +")";
		}	    
	}

    // public void addNewPower (long for_user, SuperPower sp, long power_id, int level, String session_id) {
        // new NewPower(for_user, sp, power_id, level, session_id);
    // }

	/** 
	 * Notifies a user that they have recieved a new super power */
	private static class DummyEvent extends Event {
	    
	    public DummyEvent () {
	        super("dummy", -1, null);
	        publishMe();
	    }
	    
		public String toString () {
			return "dummy event";
		}	    
	}

    public static class AcceptRequest extends Event {
        public AcceptRequest (long for_user) {
            super("acceptrequest", for_user, null);
            publishMe();
        }     
    }
    
	/** 
	 * Notifies a user that a super power was used */
	public static class UsedPower extends Event {
	    
	    /** details about the super power */
	    final public SuperPower superPower;
	    
	    /** the result of using the power */
	    final public String result;
	    
	    /** The level of the new super power */
	    final public int level;	    
	    
	    public UsedPower (long by_user, SuperPower sp, int level, String result) {
	        super("usedpower", by_user, null);
	        this.superPower = sp;
	        this.result = result;
	        this.level = level;
	        publishMe();
	    }
	    
	    public String toJson () {
	        Gson gson = new Gson();
            String json = gson.toJson(
                this,
                new TypeToken<UsedPower>() {}.getType()
            );
            return json;
	    }
	    	    
	}

	public void addUsedPower (long for_user, long by_user, long room_id, SuperPower sp, int level, String result, String session_id) {
        // new UsedPower(for_user, by_user, room_id, sp, level, result, session_id);
	}

    /**
     * Takes a JSON string and deserializes it into a list of events 
     * @param jsonStr the string of JSON to convert
     * @return list of deserialized events */
    public static UserEvent.Event deserializeEvent (String jsonStr) {
        Gson gson = new GsonBuilder()
                    .registerTypeAdapter(SuperPower.class, new SuperPowerDeserializer())
                    .create();
                    
        JsonParser parser = new JsonParser();
        JsonObject jsonObj = parser.parse(jsonStr).getAsJsonObject();

        String type = jsonObj.getAsJsonObject().get("type").getAsString();
        Type t = typeStringToType(type);
        if (t != null) {
            UserEvent.Event event = gson.fromJson(jsonObj, t);
            return event;
        }
        return null;
    }
	    
	/**
	 * Get the type token, used for JSON serializing, from the string
	 * describing the type of the class 
	 * @param type description of one of the inner classes of UserEvent
	 * @return the appropriate TypeToken, or null if there was no match */
    public static Type typeStringToType (String type) {
        Type t = null;
        
        if (type.equals("AcceptRequest")) {
            t = new TypeToken<UserEvent.AcceptRequest>() {}.getType();                                                                                            
        } else if (type.equals("roommessage")) {
            t = new TypeToken<UserEvent.RoomMessage>() {}.getType(); 
        } else if (type.equals("useristyping")) {
            t = new TypeToken<UserEvent.UserIsTyping>() {}.getType();            
        } else if (type.equals("usedpower")) {    
            t = new TypeToken<UserEvent.UsedPower>() {}.getType();             
        }
        
        return t;
    }
	
	public static class SuperPowerDeserializer implements JsonDeserializer<SuperPower> {
	    public SuperPower deserialize (JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            String name = json.getAsJsonObject().get("name").getAsString();
            return Power.valueOf(name.replace(' ', '_').toUpperCase()).getSuperPower();
	    }
	}

	static UserEvent instance = null;
    public static UserEvent get() {
        if(instance == null) {
            instance = new UserEvent();
        }
        return instance;
    }
}
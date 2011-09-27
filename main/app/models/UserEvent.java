package models;
 
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import models.powers.StoredPower;
import models.powers.SuperPower;
import play.libs.F.ArchivedEventStream;
import play.libs.F.IndexedEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import enums.Power;

/* 
 * A wrapper for the UserEvent classes.  See comment on {@link Event} for 
 * more detail */
public class UserEvent {
    private static final int adminStreamSize = 2000;
    final private ArchivedEventStream<UserEvent.Event> adminEvents = new ArchivedEventStream<UserEvent.Event>(adminStreamSize);
	        
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
		
		public String toJson () {
		    return toJson(new TypeToken<Event>() {});
		}
		
		public String toJson (TypeToken t) {
	        Gson gson = new GsonBuilder()
	                        .setExclusionStrategies(new User.ChatExclusionStrategy())
	                        .create();
            String json = gson.toJson(
                this,
                t.getType()
            );
            return json;
	    }
		
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
		
	    public String toJson () {
            return toJson(new TypeToken<RoomMessage>() {});
	    }   
        
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
            return toJson(new TypeToken<NewPower>() {});
	    }
	    
		public String toString () {
			return super.toString() + " : " + this.superPower.name + " awarded (L " + this.storedPower.level +")";
		}	    
	}

	public static class Broadcast extends Event {
	    final public String text;
	    
	    public Broadcast (String t) {
	        super("broadcast", -1, null);
	        text = t;
	    }
	    
	    public String toJson () {
            return toJson(new TypeToken<Broadcast>() {});
	    }	    
	    
		public String toString () {
			return "broadcast - " + text;
		}	    
	}


	public static class NewCoins extends Event {
	    final public int amount;
	    
	    public NewCoins (long by_user, int a) {
	        super("newcoins", by_user, null);
            amount = a;
	    }
	    
	    public String toJson () {
            return toJson(new TypeToken<NewCoins>() {});
	    }	    
	    
		public String toString () {
			return amount + "new coints";
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
	    
	    /** user_id of the person it was used on */
	    final public long used_on;
	    
	    public UsedPower (long by_user, long used_on, SuperPower sp, int level, String result) {
	        super("usedpower", by_user, null);
	        this.superPower = sp;
	        this.result = result;
	        this.level = level;
	        this.used_on = used_on;
	        publishMe();
	    }
	    
	    public String toJson () {
            return toJson(new TypeToken<UsedPower>() {});
	    }
	    	    
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
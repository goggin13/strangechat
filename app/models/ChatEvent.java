package models;
 
import java.util.*;
import play.libs.F.*;

/**
 * An event that occurs in a chat room. They are published
 * into the event stream and returned to clients in the chat 
 * room. */
public class ChatEvent {
 	private static final ArchivedEventStream<ChatRoom.Event> chatEvents = new ArchivedEventStream<ChatRoom.Event>(100);

    /**
     * For long polling, as we are sometimes disconnected, we need to pass 
     * the last event seen id, to be sure to not miss any message.  Gets the
	 * messages that have been published to the event stream for the chat room
	 * @param lastReceived	the id of the last message the caller has seen.  Messages
	 * 						with ids greater than lastReceived are returned
	 * @return list of messages with ids > lastReceived
     */
    public static Promise<List<IndexedEvent<ChatRoom.Event>>> nextMessages (long lastReceived) {
        return chatEvents.nextEvents(lastReceived);
    }

	/**
	 * An event that occurs in a chat room, and is pushed
	 * into the event stream */
    public static abstract class AnEvent {
        /** string describing the type of the event */
        final public String type;
        /** time the event was published */
        final public Long timestamp;
        /** the room_id of the pertinent room */
        final public Long room_id;

        public Event(String type, Long id) {
            this.type = type;
			this.room_id = id;
            this.timestamp = System.currentTimeMillis();
			chatEvents.publish(this);
        }
    }
    
	/** 
	 * represents a user joining the chat room */
    public static class Join extends Event {
        /** the user id of the joining user */
        final public String user;
        
        public Join(String user, Long room_id) {
            super("join", room_id);
            this.user = user;
        }
        
    }
    
	/**
	 * Represents a user leaving the chat room */
    public static class Leave extends Event {
        /** user id of the user leaving */
        final public String user;
        
        public Leave(String user, Long room_id) {
            super("leave", room_id);
            this.user = user;
        }
    }
    
	/**
	 * Represenst a message in the chat room */
    public static class Message extends Event {
		/** the user_id of the user who sent the message */
        final public String user;
		/** the text of the message */
        final public String text;
        
        public Message(String user, String text, Long room_id) {
            super("message", room_id);
            this.user = user;
            this.text = text;
        }
        
    }
}
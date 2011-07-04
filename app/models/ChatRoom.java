package models;

import java.util.*;
import java.text.Format;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import play.libs.*;
import play.libs.F.*;

/**
 * A room containing users, and generating events to the
 * ChatEvent stream */
public class ChatRoom {
    /** Reserved name for admin user */
	private static final String ADMIN_NAME = "admin";

    /** A map of all the current chat room ids to chatroom objects */
	private static final AbstractMap<Long, ChatRoom> chatRooms = new ConcurrentHashMap<Long, ChatRoom>();

    /** amount of time a user is allowed to pause heartbeating
     * before they are flushed  */	
	private static final int ALLOWED_PAUSE_SECS = 5;

	/** counter to increment new ids for chat rooms */
	private static AtomicLong next_id = new AtomicLong(0);
	
	/** id of this room */
	private final Long id;
	/** user_ids participating in this room */
	private ArrayList<String> participants;
	/** map of user_ids to the time of their last heartbeat */
	private AbstractMap<String, Date> heartbeats = new ConcurrentHashMap<String, Date>();
	/** timestamp of last activity in this room */
	private Date lastActivity;
	
	public ChatRoom(Long id) {
		this.id = id;
		this.lastActivity = new Date();
		this.participants = new ArrayList<String>();
		chatRooms.put(this.id, this);
	}

	/** 
	 * @return the id of this room */
	public Long getID () {
		return this.id;
	}

	/**
	 * Set the last activity stamp for this room the current time */
	private void setLastActivity () {
		this.lastActivity = new Date();
	}

	private void removeParticipant (String user) {
			this.participants.remove(user);
	}

	private void addParticipant (String user) {
		if (!this.userIsIn(user)) {
			this.participants.add(user);
			this.heartbeats.put(user, new Date());
		}
	}
	public ArrayList<String> getParticipants () {
		return this.participants;
	}
	
	public String getParticipant (int index) {
		return this.participants.get(index);
	}
	
	public int countParticipants () {
		return this.participants.size();
	}

	public boolean userIsIn (String user) {
		return this.participants.contains(user);
	}
	
	public Date getLastActivity () {
		return this.lastActivity;
	}

	public Long delayInSecs () {
		Long diff = new Date().getTime() - this.lastActivity.getTime();
		diff = diff / 1000;
		return diff;
	}

	private boolean isOld () {
		return this.delayInSecs() > ChatRoom.ALLOWED_PAUSE_SECS 
			   || this.countParticipants() == 0;
	}

	private void destroyIfOld () {
		if (this.isOld()) {
			this.destroy();
		}
	}

	public void destroy () {
		ChatRoom.chatRooms.remove(this.id);
		ArrayList<String> ppl = this.getParticipants();
		for (String person : ppl) {
			chatEvents.publish(new Leave(person, this.id));
		}
	}

	// keep this room alive
	public void beat (String user) {
		this.heartbeats.put(user, new Date());
		this.setLastActivity();
		checkHeartbeats();
	}
	
	// if there are any stale users shut the room down
	private void checkHeartbeats () {
		Iterator iter = heartbeats.keySet().iterator();
		while (iter.hasNext()) {
		    String user = (String)iter.next(); 
		    if (!user.equals(ChatRoom.ADMIN_NAME)) {
			    Date lastBeat = (Date)heartbeats.get(user);
				Long secondsAgo = Utility.diffInSecs(new Date(), lastBeat);
				if (secondsAgo > ALLOWED_PAUSE_SECS) {
					this.destroy();
					return;
				}
			}
		}
	}

    /**
     * For WebSocket, when a user join the room we return a continuous event stream
     * of ChatEvent
     */
    public void join(String user) {
		this.addParticipant(user);
		this.setLastActivity();
		new ChatEvent.Join(user, this.id);
    }

    /**
     * A user leave the room
     */
    public void leave(String user) {
		this.removeParticipant(user);
		this.setLastActivity();
        new ChatEvent.Leave(user, this.id);
    }
    
    /**
     * A user say something on the room
     */
    public void say(String user, String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
		this.setLastActivity();
        new ChatEvent.Message(user, text, this.id));
    }
    
    // ~~~~~~~~~ Chat room factory
    public static ChatRoom get(Long id) {
		if (id == -1) {
			id = ChatRoom.getNewID();
		}
		if (chatRooms.containsKey(id)) {
			return chatRooms.get(id);
		} else {
			return new ChatRoom(id);
		}
    }

	public static boolean areTalking (String user1, String user2) {
		AbstractMap<Long, ChatRoom> chatRooms = ChatRoom.getAll();
		Iterator iter = chatRooms.keySet().iterator();
		while (iter.hasNext()) {
			ChatRoom room = chatRooms.get(iter.next());
			if (room.userIsIn(user1) && room.userIsIn(user2)) {
				return true;
			}
		}
		return false;
	}

	public static synchronized ChatRoom getLonelyRoom (String user) {
		AbstractMap<Long, ChatRoom> chatRooms = ChatRoom.getAll();
		Iterator iter = chatRooms.keySet().iterator();
		while (iter.hasNext()) {
			ChatRoom room = chatRooms.get(iter.next());
			room.destroyIfOld();
			if (room.countParticipants() == 1 
					&& !room.userIsIn(user)
					&& !ChatRoom.areTalking(user, room.getParticipant(0))) {
				return room;
			}
		}
		return ChatRoom.get(-1L);
	}

	public static synchronized Long getNewID () {
		return next_id.incrementAndGet();
	}

	public static AbstractMap<Long, ChatRoom> flushOldRooms () {
		AbstractMap<Long, ChatRoom> chatRooms = ChatRoom.getAll();
		Iterator iter = chatRooms.keySet().iterator();
		while (iter.hasNext()) {
			ChatRoom room = chatRooms.get(iter.next());
			room.destroyIfOld();
		}
		return ChatRoom.getAll();
	}

	public static AbstractMap<Long, ChatRoom> getAll() {
		return chatRooms;
	}

}


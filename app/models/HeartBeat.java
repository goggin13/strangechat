package models;
 
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class maintains the collection of heartbeats and offers some
 * helper functions */
public class HeartBeat {
	/** Amount of time a user can go without heartbeating before they are removed */
	public static final int HEALTHY_HEARTBEAT = 6;
	
	/** A map of all the latest user_ids to heartbeats on this server */
	public static List<HeartBeat> heartbeats = new CopyOnWriteArrayList<HeartBeat>();
	    
    public long user_id;
    public long room_id;
    public String session;
    public Date time;
    
    public HeartBeat (long u, String s, long r) {
        this.user_id = u;
        this.session = s;
        this.room_id = r;
        this.time = new Date();
    }
    
    public boolean isOld () {
        long diff = Utility.diffInSecs(new Date(), this.time);
        return diff > HEALTHY_HEARTBEAT;
    }
    
    public boolean equals (Object obj) {
        if (obj == null ||
            !(obj instanceof HeartBeat)) {
            return false;
        }
        HeartBeat other = (HeartBeat)obj;
        return other.user_id == this.user_id 
               && (other.room_id == this.room_id)
               && other.session.equals(this.session);
    }
    
    public void remove () {
    	heartbeats.remove(this);
    }
    
    public String toString () {
        return "<3 " + (this.room_id > 0 ? "[" + this.room_id + "] " : "") + this.user_id + "( " + this.session + " )";
    }
    	
	/**
	 * Update the heartbeat for the given user in the given room
	 * @param room_id the id of the room to beat in
	 * @param user_id the id of the user to beat for */
	public static void beatInRoom (Long room_id, Long user_id, String session_id) {
	    int index = getHeartbeat(user_id, session_id, room_id);
	    HeartBeat hb = new HeartBeat(user_id, session_id, room_id);
	    if (index > -1) {
	        heartbeats.set(index, hb);
	    } else {
	        heartbeats.add(hb);
	    }
	    
	}    
	
	public static void beatInRoom (Long room_id, UserSession.Faux sess) {
	    beatInRoom(room_id, sess.user_id, sess.session);
	}
	
	public static void beatFor (UserSession.Faux sess) {
	    beatFor(sess.user_id, sess.session);
	}
	
	public static void beatFor (Long user_id, String session_id) {
        beatInRoom(-1L, user_id, session_id);
	}
	
	public static boolean isAlive (long user_id, String session) {
	    for (HeartBeat beat : heartbeats) {
	        if (beat.user_id == user_id &&
	            beat.session.equals(session)) {
	                return true;
	        }    
	    }
	    return false;
	}
	
	public static int indexOf (UserSession.Faux sess) {
	    int i = -1;
        for (HeartBeat beat : heartbeats) {
            i++;
            if (beat.user_id == sess.user_id 
                && beat.session.equals(sess.session)) {
                return i;
            }
        }
        return -1;
	}
	
	public static void removeAll (UserSession.Faux sess) {
	    int index = indexOf(sess);
	    while (index > -1) {
	        heartbeats.remove(index);
	        index = indexOf(sess);
	    } 
	}
	
	public static List<HeartBeat> getHeartBeats () {
	    return heartbeats;
	}

	private static int getHeartbeat (long user_id, String session, long room_id) {
	    HeartBeat hb = new HeartBeat(user_id, session, room_id);
	    return heartbeats.indexOf(hb);
	}
	
}
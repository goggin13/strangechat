package models;
 
import java.util.AbstractMap;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * The queue of people waiting for rooms to join */
public class WaitingRoom {
    
    /** How many meetings, 0 indexed, ago users must have interacted before they
	 *  can be matched again.  0 means users can talk, then they have to talk to 
	 *  at least one other person each.  -1 means they can be paired in back
	 * 
	 *  THIS IS SET IN BOOTSTRAP.JAVA;  YOU MUST MAKE CHANGES THERE OR THEY
	 *  WILL BE OVERRIDDEN */
   public int remeetEligible = 0;
	
	/** Maximum number of pending spots in the waiting room a single user can occupy */
   public int spotsPerUser = 2;
    
   /** list of ids of people waiting to be matched up with someone to chat */
   private List<UserSession.Faux> waitingRoom = new CopyOnWriteArrayList<UserSession.Faux>();
   
   /**
    * Return how many spots the given user is occupying, either in a room
    * or in line to a room 
    * @param user_id to id of the user to count 
    * @return how many times they appear in the queue */
   public int countSpots (User user) {
       int count = 0;
       for (UserSession.Faux slot : waitingRoom) {
           if (slot.user_id == user.id) {
               count++;
           }
       }
       return count + user.getNonGroupRooms().size();
   }
   
   /**
    * @return the index of the given user in the waiting room, or -1 if not in line */
   public int indexOf (long user_id, String session) {
       int i = -1;
       for (UserSession.Faux slot : waitingRoom) {
           i++;
           if (slot.user_id == user_id && slot.session.equals(session)) {
               return i;
           }
       }
       return -1;
   }
   
   /**
    * @return <code>true</code> if room is empty */
   public boolean empty () {
       return waitingRoom.size() == 0;
   }
   
	/**
	 * For super hero chat, indicate you are ready to start chatting.  Someone else will be
	 * paired with you immediately if they are available, or whenever they do become available
	 * @param user_id your user_id, so the random returned user isn't you 
	 * @param callback optional JSONP callback*/
	public synchronized void requestRandomRoom (UserSession user) {
	    
		UserSession.Faux other = null;
		for (UserSession.Faux slot : waitingRoom) {
			if (canBePaired(user, slot.toReal())) {
				other = slot;
				remove(slot.user_id, slot.session, false);
				break;
			}
		}			

		if (other != null) {
		    UserSession otherSess = other.toReal();
		    Room.createRoomFor(user, otherSess);	
        } else {
            while (countSpots(user.user) >= spotsPerUser) {
                remove(user.user.id, user.session, false);
            } 
            waitingRoom.add(user.toFaux());            
        }
	}   
   
   /**
    * Removes the given user from the waiting room
    * @param user_id the id to remove from the room 
    * @param removeAll if true, remove all of the occurences of this user,
    *                  else just one */
   public void remove (long user_id, String session, boolean removeAll) {
       int index = indexOf(user_id, session);
       while (index > -1) {
           waitingRoom.remove(index);
           if (!removeAll) {
               return;
           }
           index = indexOf(user_id, session);
       } 
   }
   
   	/**
	 * Helper for <code>requestRandomRoom</code>
	 * @return true if these users are eligible to be paired in a room right now */
	public boolean canBePaired (UserSession user1, UserSession user2) {
	    return !(user1.user.equals(user2.user))
		        && UserExclusion.canSpeak(user1.user.id, user2.user.id)
		        && (remeetEligible == -1 
		            || !Room.hasMetRecently(user1.user.id, user2.user.id, remeetEligible))
               && !Room.areSpeaking(user1.user, user2.user);
	}
   
   public void flush () {
       waitingRoom = new CopyOnWriteArrayList<UserSession.Faux>();
   }
   
   public String toString () {
       return waitingRoom.toString();
   }
      
   private static WaitingRoom instance;
   public static WaitingRoom get () {
       if (instance == null) {
           instance = new WaitingRoom();
       }
       return instance;
   }
    
}

    
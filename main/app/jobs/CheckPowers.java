package jobs;
 
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.User;
import models.UserEvent;
import play.Logger;
import play.jobs.Every;
import play.jobs.Job;
import play.libs.F.IndexedEvent;


@Every("10s")
public class CheckPowers extends Job {
    private static int counter = 0;
    private static AbstractMap<Long, User> myUsers = new HashMap<Long, User>();
    
    private static long lastReceived = 0L;
    public static final String REVEAL_CODE = "!@#$%^&$#@";
    public static final String DATA_CODE = "*&^%$!";
    public static final int HEARTBEAT_INTERVAL = 5; 
    
    public void doJob () {
        processUpdates(getEvents());

        for (User u : myUsers.values()) {
            u.checkForNewPowers();
        }
        myUsers.clear(); 
        
        // call every 3 hours (60 * 60 * 3 / 5)
        if (counter++ % 2160 == 0) {
            System.gc();
        }       
    }
    
    private static void processUpdates (List<UserEvent.Event> events) {
        for (UserEvent.Event event : events) {
            if (event instanceof UserEvent.RoomMessage) {
                UserEvent.RoomMessage rm = (UserEvent.RoomMessage)event;
                if (rm.from < 0) {
                    continue;
                }
                User from = getUser(rm.from); 
                if (from == null) {
                    continue;
                }
                String text = rm.text;
                
                if (text.equals(REVEAL_CODE)) {
                    from.offersMadeCount += 1;
                } else if (text.length() >= DATA_CODE.length()
                           && text.substring(0, DATA_CODE.length()).equals(DATA_CODE)
                           && text.indexOf("facebook_id") > -1) {   
                    // only count this for one user; there will be a corresponding event
                    // for the other one
                    from.revealCount += 1;                       
                } else {
                    from.messageCount += 1;             
                }                           
                putUser(from); 
            } else if (event instanceof UserEvent.AcceptRequest) {
                UserEvent.AcceptRequest j = (UserEvent.AcceptRequest)event;
                if (j.from < 0) {
                     continue;
                 }
                User user = getUser(j.from);     
                if (user != null) {
                    user.joinCount += 1;
                    putUser(user);                     
                }
            } else if (event instanceof UserEvent.UsedPower) {
                if (event.from < 0) {
                    continue;
                }                
                getUser(event.from);
            } else if (event instanceof UserEvent.UserIsTyping) {
                if (event.from < 0) {
                    continue;
                }                
                User from = getUser(event.from);              
                if (from != null) {
                    from.chatTime += 15;
                    putUser(from);                  
                }                
            } else if (!(event instanceof UserEvent.Event)) {
                Logger.error("Processing super power check and encountered a bad event");
            } 
        }
    }
    
    private static void putUser (User u) {
        u.save();
        myUsers.put(u.id, u);
    }
    
    private static User getUser (long id) {
        User u;
        if (myUsers.containsKey(id)) {
            u = myUsers.get(id);
        } else {
            u = User.findById(id);
            if (u == null) {
                Logger.warn("attempted to process event for non-existant user (%s)", id);
            } else {
                myUsers.put(id, u);
            }
        }
        return u;
    }
    
    private static List<UserEvent.Event> getEvents () {
        List<UserEvent.Event> events;
        
        List<IndexedEvent> indexedEvents = UserEvent.get().availableEvents(lastReceived);    
        lastReceived = indexedEvents.size() > 0 
                       ? indexedEvents.get(indexedEvents.size() - 1).id
                       : lastReceived;   
        events = new LinkedList<UserEvent.Event>();
        for (IndexedEvent e : indexedEvents) {
            events.add((UserEvent.Event)e.data);
        }
        return events;
    }
    
}
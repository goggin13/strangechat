package jobs;
 
import play.jobs.*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import models.*;
import controllers.Index;
import java.lang.reflect .*;


@Every("10s")
public class CheckPowers extends Job {
    private static Set<Long> myUsers = new HashSet<Long>();
    
    private static long lastReceived = 0L;
    public static final String REVEAL_CODE = "!@#$%^&$#@";
    public static final String DATA_CODE = "*&^%$!";
    public static final int HEARTBEAT_INTERVAL = 5; 
    
    public void doJob () {
            
        processUpdates(getEvents());

        for (Long id : myUsers) {
           User u = User.findById(id);
           if (u != null) {
              u.checkForNewPowers();
           }
        }
        myUsers.clear();        
    }
    
    private static void processUpdates (List<UserEvent.Event> events) {
        for (UserEvent.Event event : events) {
            if (event instanceof UserEvent.RoomMessage) {
                UserEvent.RoomMessage rm = (UserEvent.RoomMessage)event;
                if (rm.from == -1) {
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
                from.save();
            } else if (event instanceof UserEvent.AcceptRequest) {
                UserEvent.AcceptRequest j = (UserEvent.AcceptRequest)event;
                User user = getUser(j.from);     
                if (user != null) {
                    user.joinCount += 1;
                    user.save();                    
                }
            } else if (!(event instanceof UserEvent.Event)) {
                Logger.error("Processing super power check and encountered a bad event");
            } 
        }
    }
    
    private static User getUser (long id) {
        User u = User.findById(id);
        if (u == null) {
            Logger.warn("attempted to process event for non-existant user (%s)", id);
            return null;
        }
        myUsers.add(id);
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
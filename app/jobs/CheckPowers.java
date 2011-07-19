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


@Every("5s")
public class CheckPowers extends Job {
	private static HashMap<String, Long> lastReceived = new HashMap<String, Long>();
	private static HashMap<Long, User> myUsers = new HashMap<Long, User>();
	
	public void doJob() {
		
		if (!Server.onMaster()) {
			return;
		}
		
		System.out.println("CHECKPOWERS");
		List<Server> chatServers = Server.getChatServers();
		for (Server s : chatServers) {
		    processUpdates(getEvents(s));
		}
		for (Long id : myUsers.keySet()) {
		    User u = User.findById(id);
		    if (u != null) {
		        System.out.println("saving user " + u.id);    
		        u.save();
	        }
	        u.checkForNewPowers();
		}
		myUsers.clear();
		System.out.println("DONE CHECKPOWERS");
		
    }
    
    private static void processUpdates (List<IndexedEvent> events) {
        for (IndexedEvent event : events) {
            if (event.data instanceof UserEvent.KeepItMoving) {
                continue;
            }
            
            if (event.data instanceof UserEvent.RoomMessage) {
                UserEvent.RoomMessage rm = (UserEvent.RoomMessage)event.data;
                User to = getUser(rm.user_id);
                User from = getUser(rm.from);                
                to.gotMessageCount += 1;
                from.messageCount += 1;
            } else if (event.data instanceof UserEvent.Join) {
                UserEvent.Join j = (UserEvent.Join)event.data;
                User user = getUser(j.user_id);                
                user.joinCount += 1;
            } else if (!(event.data instanceof UserEvent.Event)) {
                Logger.error("Processing super power check and encountered a bad event");
            }
	    }
    }
    
    private static User getUser (Long id) {
        if (myUsers.containsKey(id)) {
            return myUsers.get(id);
        }
        User u = User.findById(id);
        if (u == null) {
            Logger.error("attempted to process event for non-existant user (%s)", id);
            return null;
        }
        myUsers.put(id, u);
        return u;
    }
    
    private static List<IndexedEvent> getEvents (Server s) {
        Long last = getLastReceived(s.uri);
	    List<IndexedEvent> events;
	    if (s.iAmMaster()) {
	        events = UserEvent.userEvents.availableEvents(last);
	    } else {
	        String url = s.uri + "notify/adminlisten";
	        HashMap<String, Object> params = new HashMap<String, Object>();
	        params.put("lastreceived", last);
	        WS.HttpResponse resp = Utility.fetchUrl(url, params);
	        Gson gson = new Gson();
	        Type listType = new TypeToken<List<IndexedEvent>>() {}.getType();
	        events = gson.fromJson(resp.getString(), listType);
	    }
	    if (events.size() > 0) {
	        lastReceived.put(s.uri, events.get(events.size() - 1).id);
        }
	    return events;
    }
    
    private static Long getLastReceived (String url) {
        if (lastReceived.containsKey(url)) {
            return lastReceived.get(url);
        } else {
            return 0L;
        }
    }
    
}
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

@Every("5s")
public class CheckPulses extends Job {
	private static Long lastReceived = 0L;
	
	public void doJob() {
		// maybe help events from getting stuck in the queue?
		// UserEvent.userEvents.publish(new UserEvent.Test());
		if (!Server.imAChatServer()) {
			return;
		}
		
		for (Long user_id : User.heartbeats.keySet()) {
			Date lastBeat = User.heartbeats.get(user_id);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			if (diff > User.HEALTHY_HEARTBEAT) {
				User.heartbeats.remove(user_id);
				broadcastLogout(user_id);
			}
		}
    }
	
	private static void broadcastLogout (Long user_id) {
		if (Server.onMaster()) {
			User.logOutUser(user_id);
		} else {	
			String url = Server.getMasterServer().uri + "logout?facebook_id=" + user_id;
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("facebook_id", user_id);
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
			System.out.println("response from logout request : " + json);
		}
	}

}
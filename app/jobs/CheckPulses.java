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

@Every("5s")
public class CheckPulses extends Job {
	
	public void doJob() {
		for (Long user_id : User.heartbeats.keySet()) {
			Date lastBeat = User.heartbeats.get(user_id);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			System.out.println(user_id + "'s heartbeat is " + diff + " seconds old");
			if (diff > User.HEALTHY_HEARTBEAT) {
				broadcastLogout(user_id);
				User.heartbeats.remove(user_id);
			}
		}
    }
	
	private static void broadcastLogout (Long user_id) {
		String url = Server.getMasterServer().uri + "logout";
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("facebook_id", user_id);
		WS.HttpResponse resp = Utility.fetchUrl(url, params);
		JsonObject json = resp.getJson().getAsJsonObject();
		System.out.println("BROADCASTING");
	}

}
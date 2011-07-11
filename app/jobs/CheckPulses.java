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
		if (!Server.imAChatServer()) {
			return;
		}
		
		// maybe help events from getting stuck in the queue?		
		UserEvent.userEvents.publish(new UserEvent.Test());
		
		// check user heartbeats
		for (Long user_id : User.heartbeats.keySet()) {
			Date lastBeat = User.heartbeats.get(user_id);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			if (diff > User.HEALTHY_HEARTBEAT) {
				User.heartbeats.remove(user_id);
				broadcastLogout(user_id);
			}
		}
		
		
		// check heartbeats in rooms
		for (String key : User.roombeats.keySet()) {
			Date lastBeat = User.roombeats.get(key);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			System.out.println(key + " => " + diff);
			if (diff > User.HEALTHY_HEARTBEAT) {
				String parts[] = key.split("_");
				User.roombeats.remove(key);
				Long room_id = Long.parseLong(parts[0]);
				Long user_id = Long.parseLong(parts[1]);
				broadcastLeaveRoom(room_id, user_id);
				
			}
		}
    }

	private static void broadcastLeaveRoom (Long room_id, Long user_id) {
		if (Server.onMaster()) {
			
			if (Room.removeUserFrom(room_id, user_id)) {
				System.out.println("1:removing " + user_id + " from " + room_id);
			} else {
				System.out.println("2:removing " + user_id + " from " + room_id);
			}
		} else {	
			String url = Server.getMasterServer().uri + "leaveroom";
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("user_id", user_id);
			params.put("room_id", room_id);
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
		}
	}
	
	private static void broadcastLogout (Long user_id) {
		if (Server.onMaster()) {
			User.logOutUser(user_id);
		} else {	
			String url = Server.getMasterServer().uri + "signout";
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("facebook_id", user_id);
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
		}
	}

}
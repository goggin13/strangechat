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
		
		for (Long user_id : HeartBeat.heartbeats.keySet()) {
			Date lastBeat = HeartBeat.heartbeats.get(user_id);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			if (diff > HeartBeat.HEALTHY_HEARTBEAT) {
				HeartBeat.heartbeats.remove(user_id);
				broadcastLogout(user_id);
			}
		}
		
		// check heartbeats in rooms
		for (String key : HeartBeat.roombeats.keySet()) {
			Date lastBeat = HeartBeat.roombeats.get(key);
			Long diff = Utility.diffInSecs(new Date(), lastBeat);
			if (diff > HeartBeat.HEALTHY_HEARTBEAT) {
				String parts[] = key.split("_");
				HeartBeat.roombeats.remove(key);
				Long room_id = Long.parseLong(parts[0]);
				Long user_id = Long.parseLong(parts[1]);
				Logger.info("broadcast leave room, " + user_id + " from " + room_id);
				broadcastLeaveRoom(room_id, user_id);
			}
		}

        // this prevents memory from bloating up and bloating up, but I have no clue why
        // it's necessary.  Sounds like its not usually necessary
        // http://stackoverflow.com/questions/66540/system-gc-in-java
        System.gc();
    }

	private static void broadcastLeaveRoom (Long room_id, Long user_id) {
		if (Server.onMaster()) {
			Room.removeUserFrom(room_id, user_id);
		} else {	
			String url = Server.getMasterServer().uri + "leaveroom";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", user_id.toString());
			params.put("room_id", room_id.toString());
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
		}
	}
	
	private static void broadcastLogout (Long user_id) {
		if (Server.onMaster()) {
		    User u = User.findById(user_id);
		    u.logout();
		} else {	
			String url = Server.getMasterServer().uri + "signout";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", user_id.toString());
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			JsonObject json = resp.getJson().getAsJsonObject();
		}
	}

}
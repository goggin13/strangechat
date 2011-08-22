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
import controllers.Users;

public class CheckPulses extends Job {
	private static int counter;
	
	public void doJob() {
        if (!Server.imAChatServer()) {
         return;
        }
        
        List<HeartBeat> heartbeats = HeartBeat.getHeartBeats();
        for (HeartBeat beat : heartbeats) {
            if (beat.isOld()) {
                if (beat.room_id > 0) {
                	broadcastLeaveRoom(beat.room_id, beat.user_id, beat.session);
                } else {
                	broadcastLogout(beat.user_id, beat.session);
                }
                beat.remove();
            }
        }
        
        // call every 3 hours (60 * 60 * 3 / 5)
        if (counter++ % 2160 == 0) {
            System.gc();
        }
        
    }

	private static void broadcastLeaveRoom (Long room_id, Long user_id, String session) {
		if (Server.onMaster()) {
		    Room r = Room.find("byRoom_id", room_id).first();
		    UserSession sess = UserSession.getFor(user_id, session);
            if (r != null && sess != null) {
    		    r.removeParticipant(sess);			
    		}
		} else {	
			String url = Server.getMasterServer().uri + "leaveroom";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", user_id.toString());
			params.put("session", session);
			params.put("room_id", room_id.toString());
			Utility.fetchUrl(url, params);
		}
	}
	
	private static void broadcastLogout (Long user_id, String session) {
		if (Server.onMaster()) {
		    WaitingRoom.get().remove(user_id, session, true);
		    UserSession sess = UserSession.getFor(user_id, session);
		    HeartBeat.removeAll(new UserSession.Faux(user_id, session));
		    if (sess != null) {
    		    sess.logout();
    		    sess.delete();		        
		    }
		} else {	
		    HeartBeat.removeAll(new UserSession.Faux(user_id, session));
			String url = Server.getMasterServer().uri + "signout";
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", user_id.toString());
			params.put("session", session);			
			Utility.fetchUrl(url, params);
		}
	}
	
}
package controllers;

import java.util.HashMap;
import java.util.List;

import models.HeartBeat;
import models.Room;
import models.Server;
import models.BlackList;
import models.User;
import models.UserEvent;
import models.WaitingRoom;
import models.pusher.Pusher;
import play.Play;
import play.mvc.Before;
import play.data.validation.Required;

/**
 * Demo page and home page, which is blank for now */
public class Application extends Index {

	@Before
	public static void checkAuth () {
		Index.checkAuthentication();
	}

	private static HashMap<String, String> getMasterStats () {
		HashMap<String, String> stats = new HashMap<String, String>();
		stats.put("users", User.count() + "");
		return stats;
	}
		
	public static void broadcast (String broadcast, boolean json) {
	    String channel = "presence-SHCH-broadcast" + (Play.mode == Play.Mode.DEV ? "-local" : "");
	    UserEvent.Broadcast broadcastEvent = new UserEvent.Broadcast(broadcast);
	    new Pusher().trigger(channel, "broadcast", broadcastEvent.toJson());
	    render();
	}
		
	public static void pusherDemo () {
	    render();
	}
	
	public static void banUser (@Required long ban_user_id) {
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    String channel = "presence-SHCH-broadcast" + (Play.mode == Play.Mode.DEV ? "-local" : "");
	    UserEvent.Broadcast broadcastEvent = new UserEvent.Broadcast(ban_user_id + "");
	    User u = User.findById(ban_user_id);
	    if (u != null) {
	        new BlackList(u).save();
	    }
	    new Pusher().trigger(channel, "blacklist", broadcastEvent.toJson());
	    returnOkay();   
	}
	
    public static void index() {
		HashMap<String, String> masterStats = getMasterStats();
		boolean isDev = Play.mode == Play.Mode.DEV;
		User admin = User.find("byAlias", "SHCH_ADMIN_USER").first();
        render(masterStats, isDev, admin);
    }

	public static void demo () {
		render();
	}
	
	public static void specrunner () {
		render();
	}
	
	public static void greenscript () {
	    render();
	}
	
}
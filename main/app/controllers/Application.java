package controllers;

import java.util.HashMap;
import java.util.List;

import models.HeartBeat;
import models.Room;
import models.Server;
import models.User;
import models.UserEvent;
import models.WaitingRoom;
import models.pusher.*;

import play.Play;
import play.mvc.Before;

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
		stats.put("online", User.count("select count(distinct user_id) from UserSession") + "");
		stats.put("sessions", User.count("select count(*) from UserSession") + "");		
		stats.put("rooms", Room.count() + "");
		stats.put("waiting room", WaitingRoom.get().toString());
		return stats;
	}

	private static HashMap<String, String> getChatStats () {
		HashMap<String, String> stats = new HashMap<String, String>();
		stats.put("heartbeats", HeartBeat.heartbeats.size() + "");
		return stats;
	}
	
	public static void broadcast (String broadcast, boolean json) {
	    if (broadcast != null) {
	        User.broadcast(broadcast);
	    }
        boolean amMaster = Server.onMaster();
        if (!json) {
            render(amMaster, broadcast);
        } else {
            returnOkay(null);
        }
	}
		
	public static void pusherDemo () {
	    render();
	}
	
    public static void index() {
		boolean amMaster = Server.onMaster();
		boolean amChat = Server.imAChatServer();
		HashMap<String, String> masterStats = getMasterStats();
		HashMap<String, String> chatStats = getChatStats();
		List<UserEvent.Event> events = UserEvent.get().currentMessages();
		boolean isDev = Play.mode == Play.Mode.DEV;
        render(amMaster, masterStats, amChat, chatStats, events, isDev);
    }

	public static void demo () {
		render();
	}
	
	public static void specrunner () {
	    System.out.println("SPECRUNNER");
		render();
	}
	
	public static void greenscript () {
	    render();
	}
	
}
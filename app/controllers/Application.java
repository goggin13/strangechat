package controllers;

import play.*;
import play.mvc.*;
import play.libs.WS;
import models.*;
import java.util.*;

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
		stats.put("online", User.count("online", true) + "");
		stats.put("rooms", Room.count() + "");
		return stats;
	}

	private static HashMap<String, String> getChatStats () {
		HashMap<String, String> stats = new HashMap<String, String>();
		stats.put("heartbeats", User.heartbeats.size() + "");
		return stats;
	}
	
    public static void index() {
		boolean amMaster = Server.onMaster();
		boolean amChat = Server.imAChatServer();
		HashMap<String, String> masterStats = getMasterStats();
		HashMap<String, String> chatStats = getChatStats();
		List<UserEvent.Event> events = UserEvent.currentMessages();
        render(amMaster, masterStats, amChat, chatStats, events);
    }

	public static void demo () {
		render();
	}
	
}
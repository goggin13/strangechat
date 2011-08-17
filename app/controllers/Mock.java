package controllers;

import models.User;
import models.UserEvent;
import models.WaitingRoom;
import play.Play;
import play.mvc.Before;
import play.test.Fixtures;

/**
 * Utilized by functional tests to perform data loading */
public class Mock extends Index {
	    
	@Before
	protected static void checkMode() {
        if (Play.mode != Play.Mode.DEV) {
            notFound();
        }
    }

    public static void testbroadcast () {
        User.broadcast("test broadcast");
    }

	public static void resetEventQueue () {
        UserEvent.get().resetEventQueue();
        WaitingRoom.get().flush();
		returnOkay(null);
	}

    public static void init () {
        UserEvent.get().resetEventQueue();
        WaitingRoom.get().flush();
		Fixtures.deleteDatabase();
        Fixtures.loadModels("data-full.yml");
    }

    public static void initblacklist () {
		Fixtures.deleteDatabase();
        Fixtures.loadModels("data-blacklisted.yml");
    }
        
}
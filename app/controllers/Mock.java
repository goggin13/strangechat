package controllers;
import play.*;
import play.mvc.*;
import java.util.*;
import play.test.*;
import models.*;
import models.powers.*;
import enums.*;

/**
 * Utilized by functional tests to perform data loading */
public class Mock extends Index {
	private static Long pmo_db_id = 1L;
	private static Long k_db_id = 2L;
	    
	@Before
	protected static void checkMode() {
        if (Play.mode != Play.Mode.DEV) {
            notFound();
        }
    }

	public static void resetEventQueue () {
		UserEvent.resetEventQueue();
		returnOkay(null);
	}

    public static void init () {
		UserEvent.resetEventQueue();
		Fixtures.deleteAll();
        Fixtures.load("data-full.yml");
    }
        
}
package controllers;

import jobs.CheckPowers;
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

	public static void checkPowers () {
	    new CheckPowers().now();
	    returnOkay();
	}

    public static void init () {
		Fixtures.deleteDatabase();
        // Fixtures.loadModels("data-full.yml");
    }

    public static void initblacklist () {
		Fixtures.deleteDatabase();
        Fixtures.loadModels("data-blacklisted.yml");
    }
        
}
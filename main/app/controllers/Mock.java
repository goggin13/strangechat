package controllers;

import jobs.*;
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
        System.out.println("DELETE DATABASE");
        Fixtures.deleteDatabase();
    }

    public static void initblacklist () {
		Fixtures.deleteDatabase();
        Fixtures.loadModels("data-blacklisted.yml");
    }
        
}
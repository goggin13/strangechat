package controllers;
import play.*;
import play.mvc.*;
import java.util.*;
import play.test.*;

/**
 * Utilized by functional tests to perform data loading */
public class Mock extends Controller {
	
	@Before
	protected static void checkMode() {
        if (Play.mode != Play.Mode.DEV) {
            notFound();
        }
		Fixtures.deleteAll();
    }

    public static void init () {
        Fixtures.load("data.yml");
    }

    public static void initfull () {
        Fixtures.load("data-full.yml");
    }
}
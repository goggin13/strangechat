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
    
    public static void loaddummyicebreakers() {
        User pmo = User.findById(pmo_db_id);
        User kk = User.findById(k_db_id);
        
        IceBreaker ip = new IceBreaker();
        
        // sp.used = MindReader.ICE_BREAKERS_LEVEL_2 + 1;
        for (int i = 0; i < MindReader.ICE_BREAKERS_REQUIRED + 1; i++) {
            ip.grantTo(pmo, 1);
        }
        
        StoredPower sp = StoredPower.find("byOwnerAndPower", pmo, Power.ICE_BREAKER).first();
        for (int i = 0; i < MindReader.ICE_BREAKERS_REQUIRED + 1; i++) {
            sp.use(pmo, kk);
        }        
    }

    public static void loaddummyicebreakers2() {
        // Fixtures.load("dummy-icebreakers.yml");        
        User pmo = User.findById(pmo_db_id);
        User kk = User.findById(k_db_id);
        
        IceBreaker ip = new IceBreaker();
        
        // sp.used = MindReader.ICE_BREAKERS_LEVEL_2 + 1;
        for (int i = 0; i < MindReader.ICE_BREAKERS_LEVEL_2 + 1; i++) {
            ip.grantTo(pmo, 1);
        }
        
        StoredPower sp = StoredPower.find("byOwnerAndPower", pmo, Power.ICE_BREAKER).first();
        for (int i = 0; i < MindReader.ICE_BREAKERS_LEVEL_2 + 1; i++) {
            sp.use(pmo, kk);
        }
    }
    
}
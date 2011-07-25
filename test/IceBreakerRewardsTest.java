import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import models.powers.*;
import jobs.*;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import controllers.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import play.libs.*;
import play.libs.F.*;

public class IceBreakerRewardsTest extends MyFunctionalTest {
	private static Long power_id = 0L;
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
    private int assertResponseHasIceBreaker (int level, int lastReceived, int sleepFor) {
        Promise<String> p = new CheckPowers().now();
        goToSleep(sleepFor);
        
        JsonArray arr = getWholeListenResponse(pmo_db_id, lastReceived);
		int received = 0;
		int lastLevel = 0;
		System.out.println(arr);
		for (JsonElement event : arr) {
		    JsonObject data = event.getAsJsonObject().get("data").getAsJsonObject();
		    if (data.get("type").getAsString().equals("newpower")) {
                JsonObject newPower = data.get("superPower").getAsJsonObject();
		        if (newPower.get("name").getAsString().equals("Ice Breaker")) {
		            lastLevel = data.get("level").getAsInt();
		            if (level != IceBreaker.levelCount) {
		                assertEquals(level, data.get("level").getAsInt());
	                }
		            received++;
		            power_id = data.get("power_id").getAsLong();
		        }
		    }
		    lastReceived = event.getAsJsonObject().get("id").getAsInt();
		}
		
		System.out.println("testing level " + level);
        if (level == IceBreaker.levelCount) {
            assertEquals(level, lastLevel);
            assertTrue(received > 0);
        } else {
            int count = 1 + (level == IceBreaker.bonusLevel ? IceBreaker.bonus : 0);        
    		assertEquals(count, received);
        }
        
		return lastReceived;      
    }
    
 	@Test
	public void testIceBreaker () {
	    
	    int lastReceived = 0;
	    for (int level = 1; level < IceBreaker.levelCount; level++) {
	        double time = IceBreaker.levels.get(level) - IceBreaker.levels.get(level-1);
	        double iters = Math.ceil(time / 5);
	        
	        for (int i = 0; i < iters; i++) {
    		    heartbeatForRoom(pmo_db_id, 15L);
    		}

    		// and now after we wait, PMO should have a superpower notifications
    		lastReceived = assertResponseHasIceBreaker(level, lastReceived, 1);
	    }
	    
	    // now should get one at intervals until the final level	    
	    
	    // we'll test a few intervals than fast forwad to the end
	    int time = IceBreaker.penultimateLevelInterval;
	    double iters = Math.ceil(time / 5);
	    int trialIters = 3;
	    for (int i = 0; i < trialIters; i++) {
	        
	        for (int j = 0; j < iters; j++) {
    		    heartbeatForRoom(pmo_db_id, 15L);
    		}
    		
    		System.out.println("TRIAL ITER " + i);
    		lastReceived = assertResponseHasIceBreaker(IceBreaker.levelCount - 1, lastReceived, 1);
	    }
	    
	    // now we should progress to final level
	    double totalTime = IceBreaker.levels.get(IceBreaker.levelCount) 
	                       - IceBreaker.levels.get(IceBreaker.levelCount - 1);
	    double totalIters = Math.ceil(totalTime / 5);
	    
	    for (int i = 0; i < (totalIters - trialIters) + 1; i++) {
		    heartbeatForRoom(pmo_db_id, 15L);
		}
		
	    lastReceived = assertResponseHasIceBreaker(IceBreaker.levelCount, lastReceived, 3);
        
        // now I should be able to use them infinitely
        for (int i = 0; i < 100; i++) {
            JsonObject json = usePower (power_id, pmo_db_id, rando_2_db, 15L);
            assertEquals("okay", json.get("message").getAsString());
            assertEquals("okay", json.get("status").getAsString());
        }

        
	} 


	
}
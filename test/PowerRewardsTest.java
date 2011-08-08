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

public class PowerRewardsTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testCloning () {
		
		// chat 20 messages so we can get a new power (woo hoo!)
		int msgCount = Cloning.CHAT_MSGS_REQUIRED + 2;
		for (int i = 0; i < msgCount; i++) {
     		HashMap<String, String> params = new HashMap<String, String>();
     	    params.put("for_user", k_db_id + "");
     	    params.put("from_user", pmo_db_id + "");
     	    params.put("msg", "hello,world");
     	    params.put("room_id", "15");
     		postAndAssertOkay("/roommessage", params);		                                 
		}
		
		// if we listen to the admin stream, there should be msgCount + 3 events
		// 1 for each message + 1 for extra admin message (lastReceived)
		JsonArray events = getAdminListenResponse(0);
		assertEquals(msgCount + 1, events.size());

        assertResponseContains(pmo_db_id, "Cloning", 1, 0);  	
	} 

    private int assertResponseHasEmotion (Long user_id, int level, int lastReceived) {
        return assertResponseContains(user_id, "Emotion", level, lastReceived);     
    }
    
 	@Test
	public void testEmotions () {
        // Emotions dont currently level up, so only do for 1
        int lastReceived = 0;
        Long lastLevelTime = 0L;
        for (int level = 1; level <= 1; level++) {
            Long currentLevelTime = Emotion.levels.get(level);
            Long timeRequired = currentLevelTime - lastLevelTime;
            double iters = Math.ceil(timeRequired / 5.0);
            
            for (int i = 0; i < iters; i++) {
                heartbeatForRoom(pmo_db_id, 15L);
            }
        
            // and now after we wait, PMO should have a superpower notifications
            lastReceived = assertResponseHasEmotion(pmo_db_id, level, lastReceived);
        }
	}

    // @Test
    // public void testMindReader () {
    //     
    //     for (int i = 0; i < MindReader.ICE_BREAKERS_REQUIRED; i++) {
    //         JsonObject json = usePower(1, pmo_db_id, k_db_id, 15L);
    //         assertEquals("okay", json.get("message").getAsString());
    //         assertEquals("okay", json.get("status").getAsString());
    //     }
    //     assertResponseContains(pmo_db_id, "Mind Reader", 1, 0);         
    //     
    //     
    //     for (int i = 0; i < MindReader.ICE_BREAKERS_LEVEL_2; i++) {
    //         JsonObject json = usePower (power_id, pmo_db_id, k_db_id, 15L);
    //         assertEquals("okay", json.get("message").getAsString());
    //         assertEquals("okay", json.get("status").getAsString());
    //     }
    //     assertResponseContains(pmo_db_id, "Mind Reader", 2, 0);     
    // }
    //  
	
}
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
     	    params.put("for_user", k_db_id.toString());
     	    params.put("from_user", pmo_db_id.toString());
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

    //      @Test
    // public void testMindReader () {
    //     
    //         earnIceBreakers(pmo_db_id, k_db_id, MindReader.ICE_BREAKERS_REQUIRED, 0);
    //         for (int i = 0; i < MindReader.ICE_BREAKERS_REQUIRED; i++) {
    //             JsonObject json = usePower (power_id, pmo_db_id, k_db_id, 15L);
    //             assertEquals("okay", json.get("message").getAsString());
    //             assertEquals("okay", json.get("status").getAsString());
    //         }
    //         assertResponseContains(pmo_db_id, "Mind Reader", 1, 0);         
    //         
    //         
    //         earnIceBreakers(pmo_db_id, k_db_id, MindReader.ICE_BREAKERS_LEVEL_2, 0);
    //         for (int i = 0; i < MindReader.ICE_BREAKERS_LEVEL_2; i++) {
    //             JsonObject json = usePower (power_id, pmo_db_id, k_db_id, 15L);
    //             assertEquals("okay", json.get("message").getAsString());
    //             assertEquals("okay", json.get("status").getAsString());
    //         }
    //         assertResponseContains(pmo_db_id, "Mind Reader", 2, 0);     
    // }
    //  
		

	
}
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
	    Users.remeetEligible = -1;
	    
		// pmo and kk register to for rooms
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);	    
	}
	       
	@Test
	public void testXRayVision () {
	    
		// PMO should have an event waiting notifying him kk joined
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		Long room_id = data.get("room_id").getAsLong();
		String with_user = data.get("new_user").getAsString();
		
		// chat 20 messages so we can get a new power (woo hoo!)
		int msgCount = 55;
		for (int i = 0; i < msgCount; i++) {
     		HashMap<String, String> params = new HashMap<String, String>();
     	    params.put("for_user", with_user.toString());
     	    params.put("from_user", pmo_db_id.toString());
     	    params.put("msg", "hello,world");
     	    params.put("room_id", room_id.toString());
     		postAndAssertOkay("/roommessage", params);		                                 
		}
		
		// if we listen to the admin stream, there should be msgCount + 3 events
		// 1 for each message + 2 joins + 1 for extra admin message (lastReceived)
		JsonArray events = getAdminListenResponse(0);
		assertEquals(msgCount + 3, events.size());

        // keep them alive and wait for checkpowers to run
		heartbeatForRoom(pmo_db_id, room_id);
		heartbeatForRoom(k_db_id, room_id);
		Promise<String> p = new CheckPowers().now();
        goToSleep(2);
		// and now after we wait, PMO should have a superpower notifications
		data = getListenResponse(pmo_db_id, 0);
		assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("X Ray Level 1", newPower.get("name").getAsString());		
	} 

 	@Test
	public void testIceBreaker () {
	    
		// PMO should have an event waiting notifying him kk joined
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		Long room_id = data.get("room_id").getAsLong();
		String with_user = data.get("new_user").getAsString();
		
		IceBreaker.CHAT_SECONDS_REQUIRED = 1;
		
        // keep them alive and wait for checkpowers to run
		for (int i = 0; i < 1; i++) {
		    heartbeatForRoom(pmo_db_id, room_id);
		    heartbeatForRoom(k_db_id, room_id);
		    goToSleep(2);
		}
		
		Promise<String> p = new CheckPowers().now();
        goToSleep(2);
        
		// and now after we wait, PMO should have a superpower notifications
		data = getListenResponse(pmo_db_id, 0);
		assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("Ice Breaker", newPower.get("name").getAsString());		
	} 

 	@Test
	public void testMindReader () {
        
	    // give PMO 3 used ice breakers
        GET("/mock/loaddummyicebreakers");

		Promise<String> p = new CheckPowers().now();
        goToSleep(2);
        
		// and now after we wait, PMO should have a superpower notifications
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("Mind Reader", newPower.get("name").getAsString());		
	}
	
}
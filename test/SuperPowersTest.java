import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;
import controllers.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class SuperPowersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    Users.remeetEligible = -1;
	}
	    
 	@Test
	public void testXRayVision () {
		// pmo and kk register to for rooms
	    getAndValidateResponse("/requestrandomroom?user_id=" + pmo_db_id);
	    JsonObject data = getAndValidateResponse("/requestrandomroom?user_id=" + k_db_id);
	    
		// PMO should have an event waiting notifying him kk joined
		data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		Long room_id = data.get("room_id").getAsLong();
		String with_user = data.get("new_user").getAsString();
		
		// chat 20 messages so we can get a new power (woo hoo!)
		int msgCount = 55;
		for (int i = 0; i < msgCount; i++) {
		    String msgUrl = "/roommessage?for_user=" + with_user + 
		                                 "&from_user=" + pmo_db_id +
		                                 "&msg=hello,world" + 
		                                 "&room_id=" + room_id; 
		    getAndValidateResponse(msgUrl);
		}
		
		// if we listen to the admin stream, there should be msgCount + 2 events
		// 1 for each message + 2 joins
		JsonArray events = getAdminListenResponse(0);
		assertEquals(msgCount + 2, events.size());

        // keep them alive and wait for checkpowers to run
		for (int i = 0; i < 4; i++) {
		    heartbeatForRoom(pmo_db_id, room_id);
		    heartbeatForRoom(k_db_id, room_id);
		    goToSleep(3);
		}
		
		// and now after we wait, PMO should have a superpower notifications
		data = getListenResponse(pmo_db_id, 0);
		assertEquals("newpower", data.get("type").getAsString());
        assertEquals("Level 1 X-Ray Vision", data.get("powerName").getAsString());
	}
	
}
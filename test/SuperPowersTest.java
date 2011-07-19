import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import controllers.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import play.libs.*;
import play.libs.F.*;

public class SuperPowersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    Users.remeetEligible = -1;
	}
	       
	@Test
	public void testGetAdminEvents () {
	    // put some events in there
        heartbeatFor(pmo_db_id);
    	notifyLogin(pmo_db_id, k_db_id); 
    	notifyLogout(pmo_db_id, k_db_id);
    	notifyMessage(pmo_db_id, k_db_id, "helloworld");
    	notifyChatMessage(k_db_id, pmo_db_id, "helloworld", 45L);
        notifyTyping(pmo_db_id, k_db_id, 15L, "helloworld");
        notifyJoined(pmo_db_id, k_db_id, "www.avatar.com", "kristen", "chat1.com", 15L, "asdfsadf");
        notifyLeft(pmo_db_id, k_db_id, 15L);
        
        System.out.println("SP COUNT : " + StoredPower.count());    
        StoredPower sp = StoredPower.all().first();
        notifyNewPower(rando_1_db, sp, "sadfasdf");
        sp = (StoredPower)StoredPower.all().from(1).fetch(1).get(0);
        notifyNewPower(rando_1_db, sp, "sadfasdf");

        String url = "/adminlisten?lastReceived=0";
        String jsonStr = getAndValidateInner(url);
        List<UserEvent.Event> events = UserEvent.deserializeEvents(jsonStr);

        UserEvent.DirectMessage lastMsg = (UserEvent.DirectMessage)events.get(events.size() - 1);
        Long last = Long.parseLong(lastMsg.text);

        int i = 0;
        assertEquals("heartbeat", events.get(i++).type);
        assertEquals("userlogon", events.get(i++).type);
        assertEquals("userlogout", events.get(i++).type);
        
        // jsut make sure type casting one doesn't blow up
        UserEvent.DirectMessage dm = (UserEvent.DirectMessage)events.get(i++); 
        assertEquals("directmessage", dm.type);
        assertEquals("helloworld", dm.text);
        
        assertEquals("roommessage", events.get(i++).type);
        assertEquals("useristyping", events.get(i++).type);
        assertEquals("join", events.get(i++).type);
        assertEquals("leave", events.get(i++).type);

        UserEvent.NewPower np = (UserEvent.NewPower)events.get(i++); 
        assertEquals("newpower", np.type);
        assertEquals("Ice Breaker", np.superPower.name);

        np = (UserEvent.NewPower)events.get(i++); 
        assertEquals("newpower", np.type);
        assertEquals("X Ray Level 1", np.superPower.name);        
      
        // last message will always be a direct message indicating what the last recieved was
        assertEquals("directmessage", events.get(i++).type);
        
        JsonObject data = getListenResponse(rando_1_db, 0);
        assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("X Ray Level 1", newPower.get("name").getAsString());        
	}    
	
 	@Test
	public void testXRayVision () {
		// pmo and kk register to for rooms
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);
	    
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
		for (int i = 0; i < 4; i++) {
		    heartbeatForRoom(pmo_db_id, room_id);
		    heartbeatForRoom(k_db_id, room_id);
		    goToSleep(3);
		}
		
		// and now after we wait, PMO should have a superpower notifications
		data = getListenResponse(pmo_db_id, 0);
		assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("Ice Breaker", newPower.get("name").getAsString());		
	} 
	
}
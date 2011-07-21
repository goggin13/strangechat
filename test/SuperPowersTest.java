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
import enums.*;
import models.powers.*;
import jobs.*;

public class SuperPowersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    Users.remeetEligible = -1;
	}
	       
    @Test
    public void testEnumsMatchPowers () {
        assertEquals(Power.ICE_BREAKER, new IceBreaker().getPower());
        assertEquals(Power.MIND_READER, new MindReader().getPower());                
        assertEquals(Power.X_RAY_LEVEL_1, new XRayLevelOne().getPower());        
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
        
        StoredPower sp = StoredPower.all().first();
        notifyNewPower(rando_1_db, sp, "sadfasdf");
        sp = (StoredPower)StoredPower.all().from(2).fetch(1).get(0);
        notifyNewPower(rando_1_db, sp, "sadfasdf");

        // now get the events and check they match up
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
	public void testUsePowers () {
        // first lets earn some x-ray-vision
        for (int i = 0; i < 55; i++) {
            notifyChatMessage(rando_1_db, rando_2_db, "hello world", 15L);
        }
        Promise<String> p = new CheckPowers().now();
        goToSleep(2);

        // and now after we wait, should have a superpower notifications
        JsonObject data = getListenResponse(rando_1_db, 0);
        assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("X Ray Level 1", newPower.get("name").getAsString());
        Long power_id = data.get("power_id").getAsLong();

        // now use it!
        JsonObject json = usePower (power_id, rando_1_db, rando_2_db, 15L);
        assertEquals("okay", json.get("message").getAsString());
        assertEquals("okay", json.get("status").getAsString());
    
        // twice wont work though
        json = usePower (power_id, rando_1_db, rando_2_db, 15L);
        assertEquals("You don't have any of that power remaining!", json.get("message").getAsString());
        assertEquals("error", json.get("status").getAsString());
        
        // both users should have events waiting for them about the use of it
        data = getListenResponse(rando_2_db, 0);
        assertEquals("usedpower", data.get("type").getAsString());
        assertEquals("15", data.get("room_id").getAsString());
        assertEquals("X-Ray", data.get("result").getAsString());
        JsonObject power = data.get("superPower").getAsJsonObject();
        assertEquals("X Ray Level 1", power.get("name").getAsString());
        
        data = getListenResponse(rando_1_db, 0);
        assertEquals("usedpower", data.get("type").getAsString());               
	}
	 
    // @Test
    // public void testUseIceBreakerTwiceInARow () {
    //         // first lets earn some x-ray-vision
    //         for (int i = 0; i <= (XRayLevelOne.CHAT_MESSAGES_REQUIRED * 3); i++) {
    //             notifyChatMessage(rando_1_db, rando_2_db, "hello world", 15L);
    //         }
    //         Promise<String> p = new CheckPowers().now();
    //         goToSleep(2);
    //         p = new CheckPowers().now();
    //         goToSleep(2);        
    // 
    //         // and now after we wait, should have a superpower notifications
    //         JsonObject data = getListenResponse(rando_1_db, 0);
    //         assertEquals("newpower", data.get("type").getAsString());
    //         JsonObject newPower = data.get("superPower").getAsJsonObject();
    //         assertEquals("X Ray Level 1", newPower.get("name").getAsString());
    //         Long power_id = data.get("power_id").getAsLong();
    //              
    //         JsonObject json = usePower(power_id, pmo_db_id, k_db_id, 15L);
    //         assertEquals("okay", json.get("message").getAsString());    
    // 
    //         json = usePower(power_id, pmo_db_id, k_db_id, 15L);
    //         assertEquals("okay", json.get("message").getAsString());       
    // 
    //         json = usePower (power_id, rando_1_db, rando_2_db, 15L);
    //         assertEquals("You don't have any of that power remaining!", json.get("message").getAsString());
    //         assertEquals("error", json.get("status").getAsString());             
    // }
		
}
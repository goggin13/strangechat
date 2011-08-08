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
        assertEquals(Power.X_RAY_VISION, new XRayVision().getPower());        
        assertEquals(Power.CLONING, new Cloning().getPower());        
        assertEquals(Power.OMNISCIENCE, new Omniscience().getPower());                        
        assertEquals(Power.EMOTION, new Emotion().getPower());                        
    }
    
	@Test
	public void testGetAdminEvents () {
	    // put some events in there
        heartbeatFor(pmo_db_id);
        heartbeatForRoom(pmo_db_id, 15L);
        heartbeatFor(pmo_db_id);  // these heartbeats without rooms shouldn't show up
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


        // last one should be last received, for admin listeners
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
        assertEquals("X Ray Vision", np.superPower.name);        
      
        // last message will always be a direct message indicating what the last recieved was
        assertEquals("directmessage", events.get(i++).type);
        
        JsonObject data = getListenResponse(rando_1_db, 0);
        assertEquals("newpower", data.get("type").getAsString());
        JsonObject newPower = data.get("superPower").getAsJsonObject();
        assertEquals("X Ray Vision", newPower.get("name").getAsString());        
	}    
	
	@Test
	public void testUsePowers () {

        earnAndUseIceBreakers(pmo_db_id, rando_2_db, 5);
        
        // both users should have events waiting for them about the use of it
        JsonObject data = getListenResponse(rando_2_db, 0);
        assertEquals("usedpower", data.get("type").getAsString());
        assertEquals("15", data.get("room_id").getAsString());
        
        // just make sure result is there, its a random msg so we wont test for it
        String result = data.get("result").getAsString();
        JsonObject power = data.get("superPower").getAsJsonObject();
        assertEquals("Ice Breaker", power.get("name").getAsString());
        
        data = getListenResponse(pmo_db_id, 0);
        assertEquals("usedpower", data.get("type").getAsString());               
	}
	
	protected void pairUsersInRoom (Long user1, Long user2) {
	    // pmo and KK request rooms
		requestRoomFor(user1);
	    requestRoomFor(user2);
		
		// they should get matched up.
		JsonObject data = getListenResponse(user1, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(user2.toString(), data.get("new_user").getAsString());

		data = getListenResponse(user2, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(user1.toString(), data.get("new_user").getAsString());
	}	
	
	@Test
	public void testUsePowersInMultiRooms () {
        pairUsersInRoom(pmo_db_id, k_db_id);
        pairUsersInRoom(pmo_db_id, rando_1_db);
		
		// earn emotion power
		Long firstLevelTime = Emotion.levels.get(1);
        double iters = Math.ceil(firstLevelTime / 5.0);
        for (int i = 0; i < iters; i++) {
            heartbeatForRoom(pmo_db_id, 15L);
        }
    
        // and now after we wait, PMO should have a superpower notifications
        assertResponseContains(pmo_db_id, "Emotion", 1, 0);        
        // now use it without specifying a room
        usePower(power_id, pmo_db_id, -1L, -1L);
        
        // both kk and rando should get notifications
        JsonObject data = getListenItem("usedpower", pmo_db_id, 0);
		JsonObject sp = data.get("superPower").getAsJsonObject();
        assertEquals("Emotion", sp.get("name").getAsString());
        
        data = getListenItem("usedpower", rando_1_db, 0);
		sp = data.get("superPower").getAsJsonObject();
        assertEquals("Emotion", sp.get("name").getAsString());
	}
}
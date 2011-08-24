import java.util.List;

import models.StoredPower;
import models.UserEvent;
import models.WaitingRoom;
import models.powers.Cloning;
import models.powers.Emotion;
import models.powers.IceBreaker;
import models.powers.MindReader;
import models.powers.Omniscience;
import models.powers.XRayVision;

import org.junit.Test;

import com.google.gson.JsonObject;

import enums.Power;

public class SuperPowersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    WaitingRoom.get().remeetEligible = -1;
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
        heartbeatFor(pmo_db_id, pmo_session);
        heartbeatForRoom(pmo_db_id, pmo_session, 15L);
        heartbeatFor(pmo_db_id, pmo_session);  // these heartbeats without rooms shouldn't show up
        heartbeatFor(pmo_db_id, pmo_session);        
    	notifyLogin(pmo_db_id, k_db_id); 
    	notifyLogout(pmo_db_id, k_db_id);
    	heartbeatFor(pmo_db_id, pmo_session);
    	heartbeatFor(k_db_id, k_session);
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
        System.out.println(events);
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

        long power_id = 1;  // hardcoded for PMO

        // now use them!
        for (int i = 0; i < 5; i++) {
            JsonObject json = usePower(power_id, pmo_db_id, pmo_session, rando_2_db, rando_2_session, 15L);
            assertEquals("okay", json.get("message").getAsString());
            assertEquals("okay", json.get("status").getAsString());
        }
        
        // both users should have events waiting for them about the use of it
        JsonObject data = getListenItem("usedpower", rando_2_db, 0);
        assertEquals("15", data.get("room_id").getAsString());
        
        // just make sure result is there, its a random msg so we wont test for it
        String result = data.get("result").getAsString();
        JsonObject power = data.get("superPower").getAsJsonObject();
        assertEquals("Ice Breaker", power.get("name").getAsString());
        
        data = getListenResponse(pmo_db_id, 0);
        assertEquals("usedpower", data.get("type").getAsString());               
	}
	
	protected void pairUsersInRoom (Long user1, String sess1, Long user2, String sess2) {
	    // pmo and KK request rooms
		requestRoomFor(user1, sess1);
	    requestRoomFor(user2, sess2);
		
		// they should get matched up.
		JsonObject data = getListenItem("join", user1, 0);
		assertEquals(user2.toString(), data.get("new_user").getAsString());

		data = getListenItem("join", user2, 0);
		assertEquals(user1.toString(), data.get("new_user").getAsString());
	}
	
	@Test
	public void testUsePowersInMultiRooms () {
        pairUsersInRoom(pmo_db_id, pmo_session, k_db_id, k_session);
        pairUsersInRoom(pmo_db_id, pmo_session, rando_1_db, rando_1_session);
		
		// earn emotion power
		Long firstLevelTime = Emotion.levels.get(1);
        double iters = Math.ceil(firstLevelTime / 5.0);
        for (int i = 0; i < iters; i++) {
            heartbeatForRoom(pmo_db_id, pmo_session, 15L);
        }
    
        // and now after we wait, PMO should have a superpower notifications
        assertResponseContains(pmo_db_id, "Emotion", 1, 0);  
              
        // now use it without specifying a room
        usePower(power_id, pmo_db_id, pmo_session, -1L, "", -1L);
        
        // both kk and rando should get notifications
        JsonObject data = getListenItem("usedpower", pmo_db_id, 0);
		JsonObject sp = data.get("superPower").getAsJsonObject();
        assertEquals("Emotion", sp.get("name").getAsString());
        
        data = getListenItem("usedpower", rando_1_db, 0);
		sp = data.get("superPower").getAsJsonObject();
        assertEquals("Emotion", sp.get("name").getAsString());
	}
	
}
import java.util.HashMap;

import models.powers.Cloning;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PowerRewardsTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testCloning () {
		signoutUser(pmo_db_id, pmo_session);
        signoutUser(k_db_id, k_session);
		signoutUser(rando_1_db, rando_1_session);
				
		// chat 20 messages so we can get a new power (woo hoo!)
		int msgCount = Cloning.CHAT_MSGS_REQUIRED + 2;
		for (int i = 0; i < msgCount; i++) {
		    heartbeatFor(pmo_db_id, pmo_session);
		    heartbeatFor(k_db_id, k_session);
     	    notifyChatMessage(pmo_db_id, k_db_id, "hello world", 15);
		}
		
		// if we listen to the admin stream, there should be msgCount + 3 events
		// 1 for each message + 1 for extra admin message (lastReceived)
		JsonArray events = getAdminListenResponse(0);
        // assertEquals(msgCount + 1, events.size());

        JsonObject cloning = assertResponseContainsInner(pmo_db_id, "Cloning", 1, 0);  
        long power_id = cloning.get("power_id").getAsLong();
        
	    heartbeatFor(pmo_db_id, pmo_session);
	    heartbeatFor(k_db_id, k_session);
	    heartbeatFor(rando_1_db, rando_1_session);
	            
        // start two rooms
		requestRoomFor(pmo_db_id, pmo_session);
	    requestRoomFor(k_db_id, k_session);
        
        JsonObject data = getListenItem("join", pmo_db_id, 0);
        assertEquals(k_db_id, data.get("new_user").getAsLong());
        data = getListenItem("join", k_db_id, 0);
        assertEquals(pmo_db_id, data.get("new_user").getAsLong());
        
        requestRoomFor(pmo_db_id, pmo_session);
        requestRoomFor(rando_1_db, rando_1_session);
        
        // data = getListenItem("join", pmo_db_id, 0);
        // assertEquals(rando_1_db, data.get("new_user").getAsLong());
        data = getListenItem("join", rando_1_db, 0);
        assertEquals(pmo_db_id, data.get("new_user").getAsLong());
        
        usePower(power_id, pmo_db_id, pmo_session, -1, "", -1);
        
        data = getListenItem("usedpower", pmo_db_id, 0);
        assertEquals(pmo_db_id, data.get("by_user").getAsLong());
        assertEquals("Cloning", data.get("superPower").getAsJsonObject().get("name").getAsString());
        
        data = getListenItem("usedpower", k_db_id, 0);
        assertEquals(pmo_db_id, data.get("by_user").getAsLong());
        assertEquals("Cloning", data.get("superPower").getAsJsonObject().get("name").getAsString());
                
        data = getListenItem("usedpower", rando_1_db, 0);
        assertEquals(pmo_db_id, data.get("by_user").getAsLong());        
        assertEquals("Cloning", data.get("superPower").getAsJsonObject().get("name").getAsString());        
	} 

    private int assertResponseHasEmotion (Long user_id, int level, int lastReceived) {
        return assertResponseContains(user_id, "Emotion", level, lastReceived);     
    }
    
    // @Test
    // public void testEmotions () {
    //     // Emotions dont currently level up, so only do for 1
    //     int lastReceived = 0;
    //     Long lastLevelTime = 0L;
    //     for (int level = 1; level <= 1; level++) {
    //         Long currentLevelTime = Emotion.levels.get(level);
    //         Long timeRequired = currentLevelTime - lastLevelTime;
    //         double iters = Math.ceil(timeRequired / 5.0);
    //         
    //         for (int i = 0; i < iters; i++) {
    //             heartbeatForRoom(pmo_db_id, pmo_session, 15L);
    //         }
    //     
    //         // and now after we wait, PMO should have a superpower notifications
    //         lastReceived = assertResponseHasEmotion(pmo_db_id, level, lastReceived);
    //     }
    // }

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
     
	
}
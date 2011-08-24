import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.User;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GroupChatTests extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
    private long joinGroupChat (long user_id, String sess, String key) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id + "");
        params.put("session", sess);
        params.put("key", key);
        JsonObject jsonObj = getJsonObj("/group", params);
        assertEquals("okay", jsonObj.get("status").getAsString());
        long room_id = jsonObj.get("message").getAsLong();
        return room_id;
    }
    
    private void checkForJoins (long for_user, long u1, long u2, long u3, long room_id) {
        JsonArray jsonArr = getWholeListenResponse(for_user, 0);
        List<Long> users = new LinkedList<Long>();
        users.add(u1);
        users.add(u2);
        users.add(u3);
        int i = 0;
        for (long u : users) {
            JsonObject data1 = jsonArr.get(i++).getAsJsonObject().get("data").getAsJsonObject();
            assertEquals("join", data1.get("type").getAsString());
            assertEquals(u, data1.get("new_user").getAsLong());
            assertEquals(room_id, data1.get("room_id").getAsLong());
        }
    }
    
    private void checkChatMessage (long user_id, long room_id) {
        JsonObject msg = getListenItem("roommessage", user_id, 0);
        assertEquals("test message", msg.get("text").getAsString());
        assertEquals(pmo_db_id, msg.get("from").getAsLong());
        assertEquals(room_id, msg.get("room_id").getAsLong());
    }

	@Test
	public void testJoining () {   
        long room_id1 = joinGroupChat(pmo_db_id, pmo_session, "hashedkey!@#$");
        long room_id2 = joinGroupChat(k_db_id, k_session, "hashedkey!@#$");
        long room_id3 = joinGroupChat(rando_1_db, rando_1_session, "hashedkey!@#$");
        long room_id4 = joinGroupChat(rando_2_db, rando_2_session, "hashedkey!@#$");
        assertEquals(room_id1, room_id2);
        assertEquals(room_id2, room_id3);
        assertEquals(room_id3, room_id4);
        
        // check everyone got the join notifications
        checkForJoins(pmo_db_id, k_db_id, rando_1_db, rando_2_db, room_id1);
        checkForJoins(k_db_id, pmo_db_id, rando_1_db, rando_2_db, room_id1);        
        checkForJoins(rando_1_db, pmo_db_id, k_db_id, rando_2_db, room_id1);
        checkForJoins(rando_2_db, pmo_db_id, k_db_id, rando_1_db, room_id1);
                                
        // now messaging them all at once should get messages to all of them
        // In a live setting, we won't be able to assume all the other members of
        // the group chat are on the same server, so the clients will have to
        // bundle messages to the right servers.  
        List<Long> users = new LinkedList<Long>();
        users.add(rando_2_db);
        users.add(k_db_id);
        users.add(rando_1_db);
        
        heartbeatFor(pmo_db_id, pmo_session);
        // send a message to the group and check they all got it
        notifyChatMessage(pmo_db_id, users, "test message", room_id1);
        checkChatMessage(k_db_id, room_id1);
        checkChatMessage(rando_1_db, room_id1);
        checkChatMessage(rando_2_db, room_id1);                
        
    } 
    
    @Test
    public void testBroadcastToGroups () {
        
        long room_id1 = joinGroupChat(pmo_db_id, pmo_session, "hashedkey!@#$");
        long room_id2 = joinGroupChat(k_db_id, k_session, "hashedkey!@#$");
        long room_id3 = joinGroupChat(rando_1_db, rando_1_session, "hashedkey!@#$");
        long room_id4 = joinGroupChat(rando_2_db, rando_2_session, "hashedkey!@#$");
        assertEquals(room_id1, room_id2);
        assertEquals(room_id2, room_id3);
        assertEquals(room_id3, room_id4);
        
        // check everyone got the join notifications
        checkForJoins(pmo_db_id, k_db_id, rando_1_db, rando_2_db, room_id1);
        checkForJoins(k_db_id, pmo_db_id, rando_1_db, rando_2_db, room_id1);        
        checkForJoins(rando_1_db, pmo_db_id, k_db_id, rando_2_db, room_id1);
        checkForJoins(rando_2_db, pmo_db_id, k_db_id, rando_1_db, room_id1);
                
        GET("/mock/testbroadcast");
        
        JsonObject data = getListenItem("roommessage", pmo_db_id, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
        
        data = getListenItem("roommessage", k_db_id, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
        
        data = getListenItem("roommessage", rando_1_db, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
        
        data = getListenItem("roommessage", rando_2_db, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());        
    }
    
}
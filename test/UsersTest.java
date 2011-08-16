import java.util.HashMap;

import models.WaitingRoom;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UsersTest extends MyFunctionalTest {
		
    @org.junit.Before
    public void setUp() {
        GET("/mock/init");
        WaitingRoom.get().remeetEligible = -1;
    }
    
	@Test
	public void testSigninResponse () {
		
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_2 + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);		
	    
	    System.out.println(jsonObj);
		assertEquals(1, jsonObj.entrySet().size());
		
		JsonObject caller = jsonObj.get(fb_2_db_id + "").getAsJsonObject();
        assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
        String session_id = caller.get("session_id").getAsString();
		
		// I have an ice breaker, so that should show up
        JsonObject powers = caller.get("superPowerDetails").getAsJsonObject();
        JsonObject power = powers.get("Ice Breaker").getAsJsonObject();
        assertEquals("Ice Breaker", power.get("name").getAsString());
		assertEquals("true", power.get("infinite").getAsString());	
		
		JsonArray myPowers = caller.get("superPowers").getAsJsonArray();
        JsonObject myPower = myPowers.get(0).getAsJsonObject();
        assertEquals("ICE_BREAKER", myPower.get("power").getAsString());
        Long power_id = myPower.get("id").getAsLong();
        
	} 
	
	@Test
	public void testBadLogout () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", "23423424312");

		JsonObject jsonObj = postAndValidateResponse("/signout", params);		
		assertEquals("No valid user, session data passed (user_id, session)", 
		             jsonObj.get("message").getAsString());
		
        params.put("session", "sadfsadf34rfdsv");
        jsonObj = postAndValidateResponse("/signout", params);		
		assertEquals("23423424312 and sadfsadf34rfdsv do not map to a valid session", 
		             jsonObj.get("message").getAsString());
	} 

	@Test
	public void testGoodLogout () {
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("session", pmo_session);
		postAndAssertOkay("/signout", params);
	} 
	
	@Test
	public void testMeetUpFunction () {
		// pmo registers to get paired
	    requestRoomFor(pmo_db_id, pmo_session);

		// kk registers to get paired
	    requestRoomFor(k_db_id, k_session);
		
		// now they should both have events waiting for them about the other joining
		
		// PMO should have an event waiting notifying him kk joined
		JsonObject data = getListenItem("join", pmo_db_id, 0);
		assertEquals(k_db_id + "", data.get("new_user").getAsString());
	   
		// kk should have an event waiting notifying her pmo joined
		data = getListenItem("join", k_db_id, 0);
		assertEquals(pmo_db_id + "", data.get("new_user").getAsString());		
		
		GET("/mock/reseteventqueue");
		
		// now if kk logs out, pmo should get a notification telling him she left
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", k_db_id + "");
	    params.put("session", k_session);
		postAndAssertOkay("/signout", params);
		
		// PMO should have an event waiting notifying him kk peaced
		JsonArray events = getWholeListenResponse(pmo_db_id, 0);
		
		data = events.get(0).getAsJsonObject().get("data").getAsJsonObject();
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_db_id + "", data.get("left_user").getAsString());
		
	} 
	
	@Test
	public void testMeetUpFunctionRespectsRemeetEligible () {	
	    // reset meetings and waiting room, and turn eligible to require 1 meeting apart
	    WaitingRoom.get().remeetEligible = 0;	
	    WaitingRoom.get().flush();
	    
	    // pmo requests a room
		requestRoomFor(pmo_db_id, pmo_session);
		
		// kk registers to get paired
	    requestRoomFor(k_db_id, k_session);
		
		// they should get matched up.
		JsonObject data = getListenItem("join", pmo_db_id, 0);
		assertEquals(k_db_id + "", data.get("new_user").getAsString());

		data = getListenItem("join", k_db_id, 0);
	    Long room_id = data.get("room_id").getAsLong();
		assertEquals(pmo_db_id + "", data.get("new_user").getAsString());
		
		// but if they go again, shouldn't get eachother
	    requestRoomFor(pmo_db_id, pmo_session);
	    requestRoomFor(k_db_id, k_session);
	    
	    // fb_1 registers to get paired and should get one of them
	    requestRoomFor(rando_1_db, rando_1_session);
	    data = getListenItem("join", rando_1_db, 0);
		Long newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1.equals(k_db_id) || newUser1.equals(pmo_db_id));
		
	    requestRoomFor(rando_2_db, rando_2_session);
	    data = getListenItem("join", rando_2_db, 0);
		Long newUser2 = data.get("new_user").getAsLong();
		assertTrue(newUser2.equals(k_db_id) || newUser2.equals(pmo_db_id));
		assertFalse(newUser1.equals(newUser2));
		
		GET("/leaveroom?user_id=" + pmo_db_id + "&room_id=" + room_id);
		GET("/leaveroom?user_id=" + k_db_id + "&room_id=" + room_id);		
        
        // But now they should be eligible again
	    requestRoomFor(pmo_db_id, pmo_session);
	    requestRoomFor(k_db_id, k_session);
        
        // they should get matched up.
        data = getListenItem("join", pmo_db_id, 0);
        assertEquals(k_db_id + "", data.get("new_user").getAsString());
        room_id = data.get("room_id").getAsLong();
        
        data = getListenItem("join", k_db_id, 0);
        assertEquals(pmo_db_id + "", data.get("new_user").getAsString());
                
        // fade out of room
		GET("/leaveroom?user_id=" + pmo_db_id + "&room_id=" + room_id);
		GET("/leaveroom?user_id=" + k_db_id + "&room_id=" + room_id);

        JsonArray p_resp = getWholeListenResponse(pmo_db_id, 0);
        JsonArray k_resp = getWholeListenResponse(k_db_id, 0);
        
        // But not this time!!!
	    requestRoomFor(pmo_db_id, pmo_session);
	    requestRoomFor(k_db_id, k_session); 
        
        // should have 1 more for the leave, but no join
        assertEquals(p_resp.size(), getWholeListenResponse(pmo_db_id, 0).size());
        assertEquals(k_resp.size(), getWholeListenResponse(k_db_id, 0).size());
                
        // we'll ensure that they did not get paired, by sending another
        // join request, which should be filled
	    requestRoomFor(fb_1_db_id, fb_1_session);
	    data = getListenItem("join", fb_1_db_id, 0);
		newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1 == pmo_db_id || newUser1 == k_db_id);		        
	}
	
}
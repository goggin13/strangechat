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
import jobs.*;

public class UsersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    Users.remeetEligible = -1;
	}
	    	
/* 	@Test
	public void testSigninResponse () {
		
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", fb_id_2 + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);		
	
		assertEquals(1, jsonObj.entrySet().size());
		
		JsonObject caller = jsonObj.get(fb_2_db_id + "").getAsJsonObject();
        assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
        String session_id = caller.get("session_id").getAsString();
		
		System.out.println(caller);			
		
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
		assertEquals("user 23423424312 not found", jsonObj.get("message").getAsString());
	} 
	
	@Test
	public void testGoodLogout () {
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", k_db_id + "");
		postAndAssertOkay("/signout", params);
	} 
	
	@Test
	public void testMeetUpFunction () {
		// pmo registers to get paired
	    requestRoomFor(pmo_db_id);

		// kk registers to get paired
	    requestRoomFor(k_db_id);
		
		// now they should both have events waiting for them about the other joining
		
		// PMO should have an event waiting notifying him kk joined
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_db_id + "", data.get("new_user").getAsString());
	   
		// kk should have an event waiting notifying her pmo joined
		data = getListenResponse(k_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(pmo_db_id + "", data.get("new_user").getAsString());		
		
		GET("/mock/reseteventqueue");
		
		// now if kk logs out, pmo should get a notification telling him she left
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", k_db_id + "");
		postAndAssertOkay("/signout", params);
		
		// PMO should have an event waiting notifying him kk peaced
		JsonArray events = getWholeListenResponse(pmo_db_id, 0);
		
		data = events.get(0).getAsJsonObject().get("data").getAsJsonObject();
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_db_id + "", data.get("left_user").getAsString());
		
	} 
	*/
	@Test
	public void testMeetUpFunctionRespectsRemeetEligible () {	
	    // reset meetings and waiting room, and turn eligible to require 1 meeting apart
	    Users.remeetEligible = 0;	
	    Users.waitingRoom = new CopyOnWriteArrayList<Long>(); 
	    
	    // pmo requests a room
		requestRoomFor(pmo_db_id);
		
		// kk registers to get paired
	    requestRoomFor(k_db_id);
		
		// they should get matched up.
		JsonObject data = getListenItem("join", pmo_db_id, 0);
		assertEquals(k_db_id + "", data.get("new_user").getAsString());

		data = getListenItem("join", k_db_id, 0);
	    Long room_id = data.get("room_id").getAsLong();
		assertEquals(pmo_db_id + "", data.get("new_user").getAsString());
		
		// but if they go again, shouldn't get eachother
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);
	    
	    // fb_1 registers to get paired and should get one of them
	    requestRoomFor(rando_1_db);
	    data = getListenItem("join", rando_1_db, 0);
		Long newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1.equals(k_db_id) || newUser1.equals(pmo_db_id));
		
	    requestRoomFor(rando_2_db);
	    data = getListenItem("join", rando_2_db, 0);
		Long newUser2 = data.get("new_user").getAsLong();
		assertTrue(newUser2.equals(k_db_id) || newUser2.equals(pmo_db_id));
		assertFalse(newUser1.equals(newUser2));		
		
		GET("/leaveroom?user_id=" + pmo_db_id + "&room_id=" + room_id);
		GET("/leaveroom?user_id=" + k_db_id + "&room_id=" + room_id);		
        
        // But now they should be eligible again
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);
        
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
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id); 
        
        // should have 1 more for the leave, but no join
        assertEquals(p_resp.size(), getWholeListenResponse(pmo_db_id, 0).size());
        assertEquals(k_resp.size(), getWholeListenResponse(k_db_id, 0).size());
                
        // we'll ensure that they did not get paired, by sending another
        // join request, which should be filled
	    requestRoomFor(fb_1_db_id);
	    data = getListenItem("join", fb_1_db_id, 0);
		newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1 == pmo_db_id || newUser1 == k_db_id);
		        
	}
	
}
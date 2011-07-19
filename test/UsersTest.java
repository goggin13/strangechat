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


public class UsersTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	    Users.remeetEligible = -1;
	}
	    
 	@Test
	public void testSigninResponse () {
		
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("facebook_id", fb_id_2.toString());
	    params.put("name", "Matthew Goggin");
	    params.put("access_token", facebook_token_2);	    
	    params.put("updatefriends", "true");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);		
	
		assertEquals(4, jsonObj.entrySet().size());
		
		JsonObject pmo = jsonObj.get(pmo_db_id.toString()).getAsJsonObject();
		assertEquals("Patrick Moberg", pmo.get("name").getAsString());
		assertEquals(chatURI, pmo.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
	
		JsonObject kk = jsonObj.get(k_db_id.toString()).getAsJsonObject();
		assertEquals("Kristen Diver", kk.get("name").getAsString());
		assertEquals(chatURI, kk.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());

		JsonObject caller = jsonObj.get(fb_2_db_id.toString()).getAsJsonObject();
		assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
		String session_id = caller.get("session_id").getAsString();
		
		// and now id 1 logs in
        params = new HashMap<String, String>();
	    params.put("facebook_id", fb_id_1.toString());
	    params.put("name", "Matt Goggin");
	    params.put("access_token", facebook_token_1);	    
	    params.put("updatefriends", "true");
		jsonObj = postAndValidateResponse("/signin", params);
		
		// should include my fake account
		assertEquals(2, jsonObj.entrySet().size());
		JsonObject goggin = jsonObj.get(fb_2_db_id.toString()).getAsJsonObject();
		
		assertEquals("Matthew Goggin", goggin.get("name").getAsString());
		assertEquals(chatURI, goggin.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());

		caller = jsonObj.get(fb_2_db_id.toString()).getAsJsonObject();
		assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
		
		// there should be an event waiting for user 2 telling them that user 1
		// logged in
		JsonObject data = getListenResponse(fb_2_db_id, 0);
		assertEquals("userlogon", data.get("type").getAsString());
		assertEquals(fb_1_db_id.toString(), data.get("new_user").getAsString());
		assertEquals(session_id, data.get("session_id").getAsString());
	} 

	@Test
	public void testBadTokenResponse () {		
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("facebook_id", fb_id_1.toString());
	    params.put("name", "Matt Goggin");
	    params.put("access_token", "ANINVALIDTOKENASdfasdf");	    
	    params.put("updatefriends", "true");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);		
		
		assertEquals("error", jsonObj.get("status").getAsString());
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
	    params.put("user_id", k_db_id.toString());
		postAndAssertOkay("/signout", params);

		// PMO should have an event waiting notifying him k logged out
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
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
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
	   
		// kk should have an event waiting notifying her pmo joined
		data = getListenResponse(k_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(pmo_db_id.toString(), data.get("new_user").getAsString());		
		
		GET("/mock/reseteventqueue");
		
		// now if kk logs out, pmo should get a notification telling him she left
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", k_db_id.toString());
		postAndAssertOkay("/signout", params);
		
		// PMO should have an event waiting notifying him kk peaced
		JsonArray events = getWholeListenResponse(pmo_db_id, 0);
		
		data = events.get(0).getAsJsonObject().get("data").getAsJsonObject();
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
		
		// and an additional one saying she logged out, since they are friends (awww)
        data = events.get(1).getAsJsonObject().get("data").getAsJsonObject();
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	} 
	
	@Test
	public void testMeetUpFunctionRespectsRemeetEligible () {	
	    // reset meetings and waiting room, and turn eligible to require 1 meeting apart
	    Users.remeetEligible = 0;	
	    User.waitingRoom = new CopyOnWriteArrayList<Long>(); 
	    Room.recentMeetings = new ConcurrentHashMap<Long, List<Long>>();	    
	    
	    // pmo requests a room
		requestRoomFor(pmo_db_id);
		
		// kk registers to get paired
	    requestRoomFor(k_db_id);
		
		// they should get matched up.
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());

		data = getListenResponse(k_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(pmo_db_id.toString(), data.get("new_user").getAsString());
		
		GET("/mock/reseteventqueue");
		
		// but if they go again, shouldn't get eachother
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);
	    
	    // fb_1 registers to get paired and should get one of them
	    requestRoomFor(rando_1_db);
	    data = getListenResponse(rando_1_db, 0);
		assertEquals("join", data.get("type").getAsString());
		Long newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1.equals(k_db_id) || newUser1.equals(pmo_db_id));
		
	    requestRoomFor(rando_2_db);
	    data = getListenResponse(rando_2_db, 0);
		assertEquals("join", data.get("type").getAsString());
		Long newUser2 = data.get("new_user").getAsLong();
		assertTrue(newUser2.equals(k_db_id) || newUser2.equals(pmo_db_id));
		assertFalse(newUser1.equals(newUser2));		
		
        GET("/mock/reseteventqueue");
        
        // But now they should be eligible again
	    requestRoomFor(pmo_db_id);
	    requestRoomFor(k_db_id);
        
        // they should get matched up.
        data = getListenResponse(pmo_db_id, 0);
        assertEquals("join", data.get("type").getAsString());
        assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
        
        data = getListenResponse(k_db_id, 0);
        assertEquals("join", data.get("type").getAsString());
        assertEquals(pmo_db_id.toString(), data.get("new_user").getAsString());
	}
}
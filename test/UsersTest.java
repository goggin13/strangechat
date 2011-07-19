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
		String url = "/signin?facebook_id=" + fb_id_2 + "&access_token=" + facebook_token_2 + "&name=Matthew Goggin&updatefriends=true";
	    JsonObject jsonObj = getAndValidateResponse(url);
	
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
		url = "/signin?facebook_id=" + fb_id_1 + "&access_token=" + facebook_token_1 + "&name=Matt Goggin&updatefriends=true";
	    jsonObj = getAndValidateResponse(url);
		
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
		String url =  "/signin?facebook_id=" + fb_id_1 + "&access_token=ANINVALIDTOKENASdfasdf&updatefriends=true";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("error", jsonObj.get("status").getAsString());
	} 
	
	@Test
	public void testBadLogout () {
		String bad_id = "23423424312";
		String url = "/signout?user_id=" + bad_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("user " + bad_id + " not found", jsonObj.get("message").getAsString());
	} 
	
	@Test
	public void testGoodLogout () {
		String url =  "/signout?user_id=" + k_db_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		// PMO should have an event waiting notifying him k logged out
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	} 
		
	@Test
	public void testMeetUpFunction () {
        
		// pmo registers to get paired
		String url =  "/requestrandomroom?user_id=" + pmo_db_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		// kk registers to get paired
		url =  "/requestrandomroom?user_id=" + k_db_id;
	    jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
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
		url =  "/signout?user_id=" + k_db_id;
	    jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
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
	    getAndValidateResponse("/requestrandomroom?user_id=" + pmo_db_id);
		
		// kk registers to get paired
	    getAndValidateResponse("/requestrandomroom?user_id=" + k_db_id);
		
		// they should get matched up.
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());

		data = getListenResponse(k_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(pmo_db_id.toString(), data.get("new_user").getAsString());
		
		GET("/mock/reseteventqueue");
		
		// but if they go again, shouldn't get eachother
	    getAndValidateResponse("/requestrandomroom?user_id=" + pmo_db_id);
	    getAndValidateResponse("/requestrandomroom?user_id=" + k_db_id);
	    
	    // fb_1 registers to get paired and should get one of them
	    getAndValidateResponse("/requestrandomroom?user_id=" + rando_1_db);
	    data = getListenResponse(rando_1_db, 0);
		assertEquals("join", data.get("type").getAsString());
		Long newUser1 = data.get("new_user").getAsLong();
		assertTrue(newUser1.equals(k_db_id) || newUser1.equals(pmo_db_id));
		
	    getAndValidateResponse("/requestrandomroom?user_id=" + rando_2_db);
	    data = getListenResponse(rando_2_db, 0);
		assertEquals("join", data.get("type").getAsString());
		Long newUser2 = data.get("new_user").getAsLong();
		assertTrue(newUser2.equals(k_db_id) || newUser2.equals(pmo_db_id));
		assertFalse(newUser1.equals(newUser2));		
		
        GET("/mock/reseteventqueue");
        
        // But now they should be eligible again
        getAndValidateResponse("/requestrandomroom?user_id=" + pmo_db_id);
        getAndValidateResponse("/requestrandomroom?user_id=" + k_db_id);
        
        // they should get matched up.
        data = getListenResponse(pmo_db_id, 0);
        assertEquals("join", data.get("type").getAsString());
        assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
        
        data = getListenResponse(k_db_id, 0);
        assertEquals("join", data.get("type").getAsString());
        assertEquals(pmo_db_id.toString(), data.get("new_user").getAsString());
	}
}
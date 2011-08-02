import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;
import java.util.*;
import play.libs.WS;
import java.lang.reflect .*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import jobs.*;
import play.libs.F.*;
import models.powers.*;

public class NotificationTests extends MyFunctionalTest {
			
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	
	@Test
	public void testHeartbeatFadeout () {
		// send one heartbeat to register
		HashMap<String, String> params = new HashMap<String, String>();
	    params.put("for_user", k_db_id.toString());
		postAndAssertOkay("/heartbeat", params);
		
		goToSleep(9);
        Promise<String> p = new CheckPulses().now();
        goToSleep(2);		
        
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}
		
	@Test
	public void testNotifyLogout () {
		notifyLogout(pmo_db_id, k_db_id);		

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}

	@Test
	public void testNotifyLogin () {
        notifyLogin(pmo_db_id, k_db_id);
        
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogon", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
	}

	@Test
	public void testNotifyMessage () {
        notifyMessage(pmo_db_id, k_db_id, "helloworld");

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("directmessage", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
	} 
    
	@Test
	public void testNotifyChatMessage () {
        notifyChatMessage(k_db_id, pmo_db_id, "helloworld", 45L);
        
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("roommessage", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
		assertEquals("45", data.get("room_id").getAsString());			
	} 

	@Test
	public void testNotifyJoined () {
        notifyJoined(pmo_db_id, k_db_id, "www.avatar.com", "kristen", "chat1.com", 15L, "asdfsadf");

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
		assertEquals("www.avatar.com", data.get("avatar").getAsString());
		assertEquals("15", data.get("room_id").getAsString());
		assertEquals("kristen", data.get("alias").getAsString());
		assertEquals("chat1.com", data.get("server").getAsString());		
	}

	@Test
	public void testNotifyLeft () {
        notifyLeft(pmo_db_id, k_db_id, 15L);

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());		
	} 

	@Test
	public void testNotifyTyping () {
        notifyTyping(pmo_db_id, k_db_id, 15L, "helloworld");
        
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("useristyping", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("typing_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());	
		assertEquals("helloworld", data.get("text").getAsString());			
	}
	
	@Test
	public void testLoginAndDontHeartbeat () {
    	// first id 2 logs in        
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("facebook_id", k_id.toString());
	    params.put("name", "Kristen");
	    params.put("updatefriends", "false");
		postAndValidateResponse("/signin", params);

    	// and now id 1 logs in
        params = new HashMap<String, String>();
	    params.put("facebook_id", pmo_id.toString());
	    params.put("name", "PMO");
	    params.put("updatefriends", "false");
		postAndValidateResponse("/signin", params);        

        // a couple heartbeats to stay alive while kk fades out
        for (int i = 1; i <= 4; i++) {
            heartbeatFor(pmo_db_id);
            goToSleep(3);
        }
        Promise<String> p = new CheckPulses().now();
        goToSleep(2);
        
    	// there should be an event waiting for user 2 telling them that user 1
    	// logged in
    	JsonObject data = getListenItem("userlogout", pmo_db_id, 0);
        // assertEquals("userlogout", data.get("type").getAsString());
    	assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}

	@Test
	public void testNotifyUsedPower () {
	    SuperPower sp = new IceBreaker();
        notifyUsedPower(pmo_db_id, k_db_id, 15L, sp, 2, "resultstring", "sessionid");
        
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("usedpower", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("by_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());	
		assertEquals("sessionid", data.get("session_id").getAsString());			
		assertEquals("resultstring", data.get("result").getAsString());
		assertEquals("2", data.get("level").getAsString());
	}	

}
import java.util.HashMap;

import jobs.CheckPulses;
import models.SuperPower;
import models.User;
import models.powers.IceBreaker;

import org.junit.Test;

import play.libs.F.Promise;

import com.google.gson.JsonObject;

public class NotificationTests extends MyFunctionalTest {
			
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	        
    @Test
    public void testNotifyLogout () {
        notifyLogout(pmo_db_id, k_db_id);       

        JsonObject data = getListenResponse(pmo_db_id, 0);
        assertEquals("userlogout", data.get("type").getAsString());
        assertEquals(k_db_id + "", data.get("left_user").getAsString());
    }
    
    @Test
    public void testNotifyLogin () {
        notifyLogin(pmo_db_id, k_db_id);
        
        JsonObject data = getListenResponse(pmo_db_id, 0);
        assertEquals("userlogon", data.get("type").getAsString());
        assertEquals(k_db_id + "", data.get("new_user").getAsString());
    }

    @Test
    public void testNotifyMessage () {
        notifyMessage(pmo_db_id, k_db_id, "helloworld");

        JsonObject data = getListenItem("directmessage", k_db_id, 0);
        assertEquals(pmo_db_id + "", data.get("from").getAsString());
        assertEquals("helloworld", data.get("text").getAsString()); 
    } 
    
    @Test
    public void testNotifyChatMessage () {
        heartbeatFor(k_db_id, k_session);
        heartbeatFor(pmo_db_id, pmo_session);
        notifyChatMessage(k_db_id, pmo_db_id, "helloworld", 45L);
        
        JsonObject data = getListenResponse(pmo_db_id, 0);
        assertEquals("roommessage", data.get("type").getAsString());
        assertEquals(k_db_id + "", data.get("from").getAsString());
        assertEquals("helloworld", data.get("text").getAsString()); 
        assertEquals("45", data.get("room_id").getAsString());          
    } 

    @Test
    public void testNotifyJoined () {
        notifyJoined(pmo_db_id, k_db_id, "www.avatar.com", "kristen", "chat1.com", 15L, "asdfsadf");

        JsonObject data = getListenResponse(pmo_db_id, 0);
        assertEquals("join", data.get("type").getAsString());
        assertEquals(k_db_id + "", data.get("new_user").getAsString());
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
        assertEquals(k_db_id + "", data.get("left_user").getAsString());
        assertEquals("15", data.get("room_id").getAsString());      
    } 

    @Test
    public void testNotifyTyping () {
        notifyTyping(pmo_db_id, k_db_id, 15L, "helloworld");
        
        JsonObject data = getListenResponse(pmo_db_id, 0);
        assertEquals("useristyping", data.get("type").getAsString());
        assertEquals(k_db_id + "", data.get("typing_user").getAsString());
        assertEquals("15", data.get("room_id").getAsString());  
        assertEquals("helloworld", data.get("text").getAsString());         
    }
    
	@Test
	public void testLoginAndDontHeartbeat () {
    	// first id 2 logs in        
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", k_id + "");
	    params.put("alias", "Kristen");
		postAndValidateResponse("/signin", params);

    	// and now id 1 logs in
        params = new HashMap<String, String>();
	    params.put("user_id", pmo_id + "");
	    params.put("alias", "PMO");
		postAndValidateResponse("/signin", params);        

	    // pmo requests a room
		requestRoomFor(pmo_db_id, pmo_session);
	    requestRoomFor(k_db_id, k_session);

        // a couple heartbeats to stay alive while kk fades out
        for (int i = 1; i <= 4; i++) {
            heartbeatFor(pmo_db_id, pmo_session);
            goToSleep(3);
        }
        Promise<String> p = new CheckPulses().now();
        goToSleep(2);
        
    	// there should be an event waiting for user 2 telling them that user 1
    	// logged in
    	JsonObject data = getListenItem("leave", pmo_db_id, 0);
    	assertEquals(k_db_id + "", data.get("left_user").getAsString());
	}
	
    @Test
    public void testNotifyUsedPower () {
        SuperPower sp = new IceBreaker();
            notifyUsedPower(pmo_db_id, k_db_id, 15L, sp, 2, "resultstring", "not used");
            
     JsonObject data = getListenResponse(pmo_db_id, 0);
     assertEquals("usedpower", data.get("type").getAsString());
     assertEquals(k_db_id + "", data.get("by_user").getAsString());
     assertEquals("15", data.get("room_id").getAsString());  
     assertEquals(pmo_session, data.get("session_id").getAsString());            
     assertEquals("resultstring", data.get("result").getAsString());
     assertEquals("2", data.get("level").getAsString());
    }    
    
    @Test
    public void testNotifyBroadcast () {
        // first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", pmo_id + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
		
		params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_1 + "");
	    params.put("alias", "Matthew Goggin");
		jsonObj = postAndValidateResponse("/signin", params);
		
		params = new HashMap<String, String>();
	    params.put("sign_in_id", rando_1 + "");
	    params.put("alias", "Matthew Goggin");
		jsonObj = postAndValidateResponse("/signin", params);
		
        GET("/mock/testbroadcast");
        
        JsonObject data = getListenItem("roommessage", pmo_db_id, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
        
        data = getListenItem("roommessage", fb_1_db_id, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
        
        data = getListenItem("roommessage", rando_1_db, 0);
        assertEquals(User.admin_id, data.get("from").getAsLong());
        assertEquals("test broadcast", data.get("text").getAsString());
		
    }
}
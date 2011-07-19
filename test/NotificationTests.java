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

public class NotificationTests extends MyFunctionalTest {
			
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	
	@Test
	public void testHeartbeatFadeout () {
		// send one heartbeat to register
		String url = "/heartbeat?for_user=" + k_db_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
        goToSleep(12);		
		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}
	
	@Test
	public void testNotifyLogout () {
		String url = "/notify/logout?for_user=" + pmo_db_id + "&left_user=" + k_db_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}

	@Test
	public void testNotifyLogin () {
		String url = "/notify/login?for_user=" + pmo_db_id + "&new_user=" + k_db_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("userlogon", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
	}

	@Test
	public void testNotifyMessage () {
		String url = "/notify/message?for_user=" + pmo_db_id + "&from_user=" + k_db_id + "&msg=helloworld&";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("directmessage", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
	} 

	@Test
	public void testNotifyChatMessage () {
		String url = "/notify/roommessage?for_user=" + pmo_db_id + "&from_user=" + k_db_id + "&msg=helloworld&room_id=45";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("roommessage", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
		assertEquals("45", data.get("room_id").getAsString());			
	}

	@Test
	public void testNotifyJoined () {
		String url = "/notify/joined?for_user=" + pmo_db_id + "&new_user=" + k_db_id + "&avatar=www.avatar.com&room_id=14&name=kristen&server=chat1.com";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("new_user").getAsString());
		assertEquals("www.avatar.com", data.get("avatar").getAsString());
		assertEquals("14", data.get("room_id").getAsString());
		assertEquals("kristen", data.get("alias").getAsString());
		assertEquals("chat1.com", data.get("server").getAsString());		
	}

	@Test
	public void testNotifyLeft () {
		String url = "/notify/left?for_user=" + pmo_db_id + "&left_user=" + k_db_id + "&room_id=15";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());		
	} 

	@Test
	public void testNotifyTyping () {
		String url = "/notify/useristyping?for_user=" + pmo_db_id + "&user_id=" + k_db_id + "&room_id=15&text=helloworld";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_db_id, 0);
		assertEquals("useristyping", data.get("type").getAsString());
		assertEquals(k_db_id.toString(), data.get("typing_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());	
		assertEquals("helloworld", data.get("text").getAsString());			
	}

	@Test
	public void testLoginAndDontHeartbeat () {
    	// first id 2 logs in
    	String url = "/signin?facebook_id=" + k_id + "&=Kristen&updatefriends=false";
        JsonObject jsonObj = getAndValidateResponse(url);
        
    	// and now id 1 logs in
    	url = "/signin?facebook_id=" + pmo_id + "&name=PMO&updatefriends=false";
        jsonObj = getAndValidateResponse(url);

        // a couple heartbeats to stay alive while kk fades out
        for (int i = 1; i <= 4; i++) {
            heartbeatFor(pmo_db_id);
            goToSleep(3);
        }
        
    	// there should be an event waiting for user 2 telling them that user 1
    	// logged in
    	JsonObject data = getListenResponse(pmo_db_id, 0);
    	assertEquals("userlogout", data.get("type").getAsString());
    	assertEquals(k_db_id.toString(), data.get("left_user").getAsString());
	}
	
	@Test
	public void testNotifyNewPower () {
	    HashMap<String, String> params = new HashMap<String, String>();
	    StoredPower storedPower = StoredPower.all().first();
        Gson gson = new Gson();
        Type powerType = new TypeToken<StoredPower>() {}.getType();
        
        String powerJson = gson.toJson(storedPower, powerType);
        params.put("storedPowerJSON", powerJson);
        params.put("for_user", rando_1_db.toString());
        params.put("session_id", "sadfsafd");

        Response response = POST("/notify/newpower", params); 
        assertIsOk(response); 
        JsonObject json = new JsonParser().parse(response.out.toString()).getAsJsonObject();
        assertEquals("okay", json.get("status").getAsString());

        JsonObject data = getListenResponse(rando_1_db, 0);
    	assertEquals("newpower", data.get("type").getAsString());
    	assertEquals("Ice Breaker", data.get("powerName").getAsString());        
	}
	
}
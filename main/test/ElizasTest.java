
import java.util.HashMap;
import org.junit.Test;

import models.eliza.Eliza;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class ElizasTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testEliza () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("session", pmo_session);
	    params.put("for_user", k_db_id + "");	    
	    params.put("for_session", k_session);
	    params.put("room_id", "15");	    
	    params.put("qry", "whats up eliza");
		JsonObject jsonObj = postAndValidateResponse("/eliza", params);   
		
		JsonArray data = getWholeListenResponse(k_db_id, 0);
		
		JsonObject rm1 = data.get(0).getAsJsonObject().get("data").getAsJsonObject();
        String msg = rm1.get("text").getAsString();
        assertEquals(pmo_db_id, rm1.get("from").getAsLong());
        assertEquals("@Azile whats up eliza", msg);
		
		JsonObject rm2 = data.get(1).getAsJsonObject().get("data").getAsJsonObject();
		String r = rm2.get("text").getAsString();
		assertEquals(Eliza.user_id, rm2.get("from").getAsLong());
		assertTrue(r.length() > 0);
	
		JsonObject rm3 = getListenItem("roommessage", pmo_db_id, 0);
		String r2 = rm3.get("text").getAsString();
		assertEquals(Eliza.user_id, rm3.get("from").getAsLong());
		assertTrue(r2.length() > 0);
		assertEquals(r, r2);
	} 
	
	@Test
	public void testGetBotRoom () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("session", pmo_session);
	    params.put("bot_id", "843ad5ae9e34019a");	    
		JsonObject jsonObj = postAndValidateResponse("/elizas/requestbotroom", params);   
		
		JsonObject data = getListenItem("join", pmo_db_id, 0);
		assertEquals("6", data.get("new_user").getAsString());
		assertEquals("http://bnter.com/web/assets/images/4571__w320_h320.png", data.get("avatar").getAsString());
		assertEquals("Test", data.get("alias").getAsString());
		assertEquals("http://localhost:9000/", data.get("server").getAsString());
		long room_id = data.get("room_id").getAsLong();
		assertTrue(room_id > 0);
		
		// now talk to it
		params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("session", pmo_session);
	    params.put("room_id", room_id + "");	    
	    params.put("qry", "whats up eliza");
	    params.put("bot_id", "843ad5ae9e34019a");
	    params.put("bot_user_id", "6");
		jsonObj = postAndValidateResponse("/elizas/talkto", params);   
		
		JsonObject rm3 = getListenItem("roommessage", pmo_db_id, 0);
		String r2 = rm3.get("text").getAsString();
		assertTrue(r2.length() > 0);
		assertEquals(room_id, rm3.get("room_id").getAsLong());
		assertEquals("6", rm3.get("from").getAsString());
		
	}	
}
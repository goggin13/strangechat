import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import models.powers.*;
import jobs.*;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import controllers.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import play.libs.*;
import play.libs.F.*;

public class ElizasTest extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testEliza () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("from_user", k_db_id + "");	    
	    params.put("room_id", "15");	    
	    params.put("qry", "whats up eliza");
		JsonObject jsonObj = postAndValidateResponse("/eliza", params);   
		
		JsonArray data = getWholeListenResponse(pmo_db_id, 0);
		
		JsonObject rm1 = data.get(0).getAsJsonObject().get("data").getAsJsonObject();
        String msg = rm1.get("text").getAsString();
        assertEquals("@Eliza whats up eliza", msg);
		
		JsonObject rm2 = data.get(1).getAsJsonObject().get("data").getAsJsonObject();
		String r = rm2.get("text").getAsString();
		assertTrue(r.length() > 0);
	
		JsonObject rm3 = getListenItem("roommessage", k_db_id, 0);
		String r2 = rm3.get("text").getAsString();
		assertTrue(r2.length() > 0);
		assertEquals(r, r2);
	} 
	
	
	@Test
	public void testGetBotRoom () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
	    params.put("bot_id", "843ad5ae9e34019a");	    
		JsonObject jsonObj = postAndValidateResponse("/elizas/requestbotroom", params);   
		
		JsonObject data = getListenItem("join", pmo_db_id, 0);
		assertEquals("6", data.get("new_user").getAsString());
		assertEquals("http://www.celebrific.com/wp-content/uploads/2010/11/425.the_.incredible.hulk_.033108.jpg", data.get("avatar").getAsString());
		assertEquals("Test", data.get("alias").getAsString());
		assertEquals("http://localhost:9000/", data.get("server").getAsString());
		long room_id = data.get("room_id").getAsLong();
		assertTrue(room_id > 0);
		
		// now talk to it
		params = new HashMap<String, String>();
	    params.put("user_id", pmo_db_id + "");
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
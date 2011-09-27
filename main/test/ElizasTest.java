
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
	    params.put("channel", "15");	    
	    params.put("qry", "whats up eliza");
		postAndValidateResponse("/eliza", params);   
	} 
		
}
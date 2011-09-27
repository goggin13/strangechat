import java.util.HashMap;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class UsersTest extends MyFunctionalTest {
		
    @org.junit.Before
    public void setUp() {
        GET("/mock/init");
    }
    
	@Test
	public void testSigninResponse () {
		
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_2 + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);		
	    
		assertEquals(1, jsonObj.entrySet().size());
		
		JsonObject caller = jsonObj.get(fb_id_2 + "").getAsJsonObject();
        caller.get("session_id").getAsString();
		
		// I have an ice breaker, so that should show up
        JsonObject powers = caller.get("superPowerDetails").getAsJsonObject();
        JsonObject power = powers.get("Ice Breaker").getAsJsonObject();
        assertEquals("Ice Breaker", power.get("name").getAsString());
		assertEquals("true", power.get("infinite").getAsString());	
		
		JsonArray myPowers = caller.get("superPowers").getAsJsonArray();
        JsonObject myPower = myPowers.get(0).getAsJsonObject();
        assertEquals("ICE_BREAKER", myPower.get("power").getAsString());
        myPower.get("id").getAsLong();
        
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
	public void testPostToConsume () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_2 + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
	    JsonObject caller = jsonObj.get(fb_id_2 + "").getAsJsonObject();
        String session = caller.get("session_id").getAsString();
        String id = caller.get("id").getAsString();
        
        params = new HashMap<String, String>();
	    params.put("sign_in_id", pmo_id + "");
	    params.put("alias", "Patrick Moberg");
		postAndValidateResponse("/signin", params);
        
        params = new HashMap<String, String>();
	    params.put("consume_user_id", pmo_id + "");
        params.put("user_id", id + "");
        params.put("session", session);
        
		postAndAssertOkay("/users/consume", params);		
	}

    @Test
    public void testGoodLogout () {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_2 + "");
	    params.put("alias", "Matthew Goggin");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
	    JsonObject caller = jsonObj.get(fb_id_2 + "").getAsJsonObject();
        String session = caller.get("session_id").getAsString();
        String id = caller.get("id").getAsString();
        
        params = new HashMap<String, String>();
        params.put("user_id", id);
        params.put("session", session);
        postAndAssertOkay("/signout", params);
    }  
    
}
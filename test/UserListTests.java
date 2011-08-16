import java.util.HashMap;

import org.junit.Test;

import com.google.gson.JsonObject;

public class UserListTests extends MyFunctionalTest {
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/initblacklist");
	}
	 	        
 	@Test
	public void testBlackList () {
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("sign_in_id", fb_id_1 + "");
	    params.put("name", "Matthew Goggin");
	    params.put("access_token", facebook_token_2);	    
	    params.put("updatefriends", "true");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
		System.out.println(jsonObj);
		assertEquals("error", jsonObj.get("status").getAsString());
		assertEquals("You have been blacklisted", jsonObj.get("message").getAsString());
	}  
	
}
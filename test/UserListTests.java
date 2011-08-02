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

public class UserListTests extends MyFunctionalTest {
	private static Long blacklisted_id = 15L;
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	 	        
 	@Test
	public void testBlackList () {
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("facebook_id", blacklisted_id.toString());
	    params.put("name", "Matthew Goggin");
	    params.put("access_token", facebook_token_2);	    
	    params.put("updatefriends", "true");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
		assertEquals("error", jsonObj.get("status").getAsString());
		assertEquals("You have been blacklisted", jsonObj.get("message").getAsString());
	}  
	
}
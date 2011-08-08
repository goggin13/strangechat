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
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/initblacklist");
	}
	 	        
 	@Test
	public void testBlackList () {
		// first id 2 logs in
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", fb_id_1 + "");
	    params.put("name", "Matthew Goggin");
	    params.put("access_token", facebook_token_2);	    
	    params.put("updatefriends", "true");
		JsonObject jsonObj = postAndValidateResponse("/signin", params);
		System.out.println(jsonObj);
		assertEquals("error", jsonObj.get("status").getAsString());
		assertEquals("You have been blacklisted", jsonObj.get("message").getAsString());
	}  
	
}
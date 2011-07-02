import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class UsersTest extends FunctionalTest {
	
	// These tokens were obtained using offline_access permissions from the facebook API, which are NOT like
	// the tokens this app will usually be receiving.  But they will hopefully last for testing.  
	private static Long fb_id_1 = 100002292928724L;
	private static String facebook_token_1 = "126880997393817|1a234213f96da63a39f46e84.1-100002292928724|uhaRQIintrlca7mqrVmb6HAMxK0";
	private static Long fb_id_2 = 32701378L;
	private static String facebook_token_2 = "126880997393817|a22af9b8426a1465b605d78f.1-32701378|LQO-IKrb-KEjBrlmcp-bscL2OMA";
	private static Long pmo_id = 24403414L;
	private static Long k_id = 411183L;
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}

	@Test
	public void testLoginResponse () {
		
		// first id 1 logs in
		String url = "/login?facebook_id=" + fb_id_2 + "&access_token=" + facebook_token_2;
		System.out.println(url);
	    JsonObject jsonObj = getAndValidateResponse(url);
	
		assertEquals(3, jsonObj.entrySet().size());
		assertEquals("Patrick Moberg", jsonObj.get(pmo_id.toString()).getAsString());
		assertEquals("Kristen Diver", jsonObj.get(k_id.toString()).getAsString());
		assertEquals("chat1.com", jsonObj.get("-1").getAsString());
		
		// and now id 2 logs in
		url = "/login?facebook_id=" + fb_id_1+ "&access_token=" + facebook_token_1;
	    jsonObj = getAndValidateResponse(url);
		
		// should include my fake account
		assertEquals(2, jsonObj.entrySet().size());
		assertEquals("Matthew Goggin", jsonObj.get(fb_id_2.toString()).getAsString());
		assertEquals("chat1.com", jsonObj.get("-1").getAsString());
		
	}

	@Test
	public void testBadTokenResponse () {		
		String url = "/login?facebook_id=" + fb_id_1 + "&access_token=ANINVALIDTOKENASdfasdf";
	    JsonObject jsonObj = getAndValidateResponse(url);
		JsonObject errObj = jsonObj.get("error").getAsJsonObject();
		assertEquals("OAuthException", errObj.get("type").getAsString());
	}
	
	@Test
	public void testGoodLogout () {
		String url = "/logout?facebook_id=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}
	
	@Test
	public void testBadLogout () {
		String bad_id = "23423424312";
		String url = "/logout?facebook_id=" + bad_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("user " + bad_id + " not found", jsonObj.get("message").getAsString());
	}
	
	private JsonObject getAndValidateResponse (String url) {
		Response response = GET(url);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertCharset("utf-8", response);	
		String jsonStr = response.out.toString();
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}
    
}
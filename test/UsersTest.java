import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class UsersTest extends MyFunctionalTest {
	
	// These tokens were obtained using offline_access permissions from the facebook API, which are NOT like
	// the tokens this app will usually be receiving.  But they will hopefully last for testing.  
	private static Long fb_id_1 = 100002292928724L;
	private static String facebook_token_1 = "126880997393817|1a234213f96da63a39f46e84.1-100002292928724|uhaRQIintrlca7mqrVmb6HAMxK0";
	private static Long fb_id_2 = 32701378L;
	private static String facebook_token_2 = "126880997393817|a22af9b8426a1465b605d78f.1-32701378|LQO-IKrb-KEjBrlmcp-bscL2OMA";
	private static Long pmo_id = 24403414L;
	private static Long k_id = 411183L;
	private static String masterURI = "";
	private static String chatURI = "http://localhost:9000/";
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
		// masterURI = Server.getMasterServer().uri;
		// chatURI = Server.getAChatServer().uri;
	}

/*	@Test
	public void testLoginResponse () {
		
		// first id 2 logs in
		String url = "/login?facebook_id=" + fb_id_2 + "&access_token=" + facebook_token_2 + "&name=Matthew Goggin";
	    JsonObject jsonObj = getAndValidateResponse(url);
	
		assertEquals(4, jsonObj.entrySet().size());
		
		JsonObject pmo = jsonObj.get(pmo_id.toString()).getAsJsonObject();
		assertEquals("Patrick Moberg", pmo.get("name").getAsString());
		assertEquals(chatURI, pmo.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());

		JsonObject kk = jsonObj.get(k_id.toString()).getAsJsonObject();
		assertEquals("Kristen Diver", kk.get("name").getAsString());
		assertEquals(chatURI, kk.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
				
		JsonObject caller = jsonObj.get(fb_id_2.toString()).getAsJsonObject();
		assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
		
		// and now id 1 logs in
		url = "/login?facebook_id=" + fb_id_1 + "&access_token=" + facebook_token_1 + "&name=Matt Goggin";;
	    jsonObj = getAndValidateResponse(url);
		
		// should include my fake account
		assertEquals(2, jsonObj.entrySet().size());
		JsonObject goggin = jsonObj.get(fb_id_2.toString()).getAsJsonObject();
		
		assertEquals("Matthew Goggin", goggin.get("name").getAsString());
		assertEquals(chatURI, goggin.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());

		caller = jsonObj.get(fb_id_1.toString()).getAsJsonObject();
		assertEquals(chatURI, caller.get("heartbeatServer").getAsJsonObject().get("uri").getAsString());
		
		// there should be an event waiting for user 2 telling them that user 1
		// logged in
		JsonObject data = getListenResponse(fb_id_2, 0);
		assertEquals("userlogon", data.get("type").getAsString());
		assertEquals(fb_id_1.toString(), data.get("new_user").getAsString());
		assertEquals(fb_id_2.toString(), data.get("user_id").getAsString());		
	}

	@Test
	public void testBadTokenResponse () {		
		String url =  "/login?facebook_id=" + fb_id_1 + "&access_token=ANINVALIDTOKENASdfasdf";
	    JsonObject jsonObj = getAndValidateResponse(url);
		JsonObject errObj = jsonObj.get("error").getAsJsonObject();
		assertEquals("OAuthException", errObj.get("type").getAsString());
	} 
	
	@Test
	public void testBadLogout () {
		String bad_id = "23423424312";
		String url = "/logout?facebook_id=" + bad_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("user " + bad_id + " not found", jsonObj.get("message").getAsString());
	} 
	
	@Test
	public void testGoodLogout () {
		String url =  "/logout?facebook_id=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		// PMO should have an event waiting notifying him k logged out
		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("left_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());		
	} 
	
	@Test
	public void testGetRandom () {
		String url =  "/random?user_id=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertNotSame(k_id.toString(), jsonObj.get("user_id").getAsString());
		assertTrue(jsonObj.get("online").getAsBoolean());
		assertTrue(jsonObj.has("name"));
	} */
	
	@Test
	public void testMeetUpFunction () {
		// pmo registers to get paired
		String url =  "/requestrandomroom?user_id=" + pmo_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		// // should be no issue if he registers twice (shouldnt be but it is)
		// url =  "/requestrandomroom?user_id=" + pmo_id;
		// 	    jsonObj = getAndValidateResponse(url);
		// assertEquals("okay", jsonObj.get("status").getAsString());
		
		// kk registers to get paired
		url =  "/requestrandomroom?user_id=" + k_id;
	    jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
		// now they should both have events waiting for them about the other joining
		
		// PMO should have an event waiting notifying him kk joined
		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("new_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	   
		// kk should have an event waiting notifying her pmo joined
		data = getListenResponse(k_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(pmo_id.toString(), data.get("new_user").getAsString());		
		assertEquals(k_id.toString(), data.get("user_id").getAsString());
		
		// if another person looks, they should get pmo back
		// url =  "/requestrandomroom?user_id=" + fb_id_1;
		// 	    jsonObj = getAndValidateResponse(url);
		// assertEquals("okay", jsonObj.get("status").getAsString());
		// 
		// data = getListenResponse(fb_id_1, 0);
		// assertEquals("join", data.get("type").getAsString());
		// assertEquals(pmo_id.toString(), data.get("new_user").getAsString());		
		// assertEquals(fb_id_1.toString(), data.get("user_id").getAsString());
		
		GET("/mock/reseteventqueue");
		
		// now if kk logs out, pmo should get a notification telling him she left
		url =  "/logout?facebook_id=" + k_id;
	    jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
		// PMO should have an event waiting notifying him kk peaced
		data = getListenResponse(pmo_id, 0);
		System.out.println(data);
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("left_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}
		    
}
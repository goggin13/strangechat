import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import org.junit.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import models.*;

public class MyFunctionalTest extends FunctionalTest {
	
	// These tokens were obtained using offline_access permissions from the facebook API, which are NOT like
	// the tokens this app will usually be receiving.  But they will hopefully last for testing.  
	protected static Long next_id = 6L;
	
	protected static Long fb_id_1 = 100002292928724L;
	protected static Long fb_1_db_id = 3L;
	protected static String facebook_token_1 = "126880997393817|1a234213f96da63a39f46e84.1-100002292928724|uhaRQIintrlca7mqrVmb6HAMxK0";
	
	protected static Long fb_id_2 = 32701378L;
	protected static Long fb_2_db_id = next_id;
	protected static String facebook_token_2 = "126880997393817|a22af9b8426a1465b605d78f.1-32701378|LQO-IKrb-KEjBrlmcp-bscL2OMA";
	
	protected static Long pmo_id = 24403414L;
	protected static Long pmo_db_id = 1L;
	
	protected static Long k_id = 411183L;
	protected static Long k_db_id = 2L;
	
	protected static String masterURI = "";
	protected static String chatURI = "http://localhost:9000/";
	protected static Long rando_1 = 11L;
	protected static Long rando_1_db = 4L;	
	protected static Long rando_2 = 12L;
	protected static Long rando_2_db = 5L;
		
	@Test
	public void stub () {
		// do nothing
	}
	
	protected JsonArray getAdminListenResponse (int lastReceived) {
		String url = "/adminlisten?lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}
	
	protected JsonObject getListenResponse (Long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived;
	    JsonArray jsonArr = getAndValidateAsArray(url);
	    System.out.println("listening response = " + jsonArr.toString());
		JsonObject jsonObj = jsonArr.get(jsonArr.size() - 1).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		return data;
	}

	protected JsonArray getWholeListenResponse (Long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}
	
	protected void heartbeatFor (Long user_id) {
	    String url = "/heartbeat?for_user=" + user_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}
	
	protected void heartbeatForRoom (Long user_id, Long room_id) {
	    String url = "/heartbeat?for_user=" + user_id + "&room_ids=" + room_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}	
	
	protected JsonArray getAndValidateAsArray (String url) {
		String jsonStr = getAndValidateInner(url);
		JsonArray jsonArr = new JsonParser().parse(jsonStr).getAsJsonArray();
		return jsonArr;
	}

	protected String getAndValidateInner (String url) {
		System.out.println("GET " + url);
		Response response = GET(url);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertCharset("utf-8", response);	
		String jsonStr = response.out.toString();
		return jsonStr;
	}

	protected JsonObject getAndValidateResponse (String url) {
		String jsonStr = getAndValidateInner(url);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}
	
	protected void goToSleep (int seconds) {
	    try {
			System.out.println("sleeping");
			Thread.sleep(seconds * 1000);
			System.out.println("waking up");			
		} catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
	
}
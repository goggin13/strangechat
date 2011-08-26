import java.util.HashMap;

import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyFunctionalTest extends FunctionalTest {
	
	// These tokens were obtained using offline_access permissions from the facebook API, which are NOT like
	// the tokens this app will usually be receiving.  But they will hopefully last for testing.  
	protected static long next_id = 8L;
	protected static long fb_id_1 = 100002292928724L;
	protected static long fb_1_db_id = 3L;
	protected static String facebook_token_1 = "126880997393817|1a234213f96da63a39f46e84.1-100002292928724|uhaRQIintrlca7mqrVmb6HAMxK0";
	
	protected static long fb_id_2 = 32701378L;
	protected static long fb_2_db_id = next_id;
	protected static String facebook_token_2 = "126880997393817|a22af9b8426a1465b605d78f.1-32701378|LQO-IKrb-KEjBrlmcp-bscL2OMA";
	
	protected static long pmo_id = 24403414L;
	protected static long pmo_db_id = 1L;
	
	protected static long k_id = 411183L;
	protected static long k_db_id = 2L;
	
	protected static String masterURI = "";
	protected static String chatURI = "http://localhost:9000/";
	protected static long rando_1 = 11L;
	protected static long rando_1_db = 4L;	
	protected static long rando_2 = 12L;
	protected static long rando_2_db = 5L;

	protected static long power_id = 0L;	
	
	protected static String pmo_session_id = "123";
	protected static String k_session_id = "321";
	protected static String fb_1_session_id = "324rwef675ds";
	protected static String fb_2_session_id = "324rwe65fdasdfs";	
	protected static String rando_1_session_id = "1324refd";
	protected static String rando_2_session_id = "ASfdgdfsefsd";	
		
	protected static String pmo_session = "pmo_session";
	protected static String k_session = "k_session";
	protected static String fb_1_session = "goggin_session";
	protected static String rando_1_session = "rando_1_session";
	protected static String rando_2_session = "rando_2_session";				
	
	protected static HashMap<Long, String> sessionMap = new HashMap<Long, String>();
	
	static {
		sessionMap.put(pmo_db_id, pmo_session);
		sessionMap.put(k_db_id, k_session);
		sessionMap.put(fb_1_db_id, fb_1_session);
		sessionMap.put(rando_1_db, rando_1_session);
		sessionMap.put(rando_2_db, rando_2_session);
	}
	
	@Test
	public void stub () {
		// do nothing
	}
	
	protected JsonArray getAdminListenResponse (int lastReceived) {
		String url = "/adminlisten?lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}
	
	protected JsonObject getListenResponse (long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived + "&session=" + sessionMap.get(id);
	    JsonArray jsonArr = getAndValidateAsArray(url);
		JsonObject jsonObj = jsonArr.get(jsonArr.size() - 1).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		return data;
	}

	protected JsonArray getWholeListenResponse (long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived + "&session=" + sessionMap.get(id);
	    return getAndValidateAsArray(url);
	}

    protected JsonObject getListenItem (String type, long user_id, int lastReceived) {
        JsonArray arr =  getWholeListenResponse(user_id, lastReceived);
        JsonObject returnData = null;
        for (JsonElement e : arr) {
            JsonObject data = e.getAsJsonObject().get("data").getAsJsonObject();
            if (data.get("type").getAsString().equals(type)) {
                returnData = data;
            }
        }
        assertTrue(returnData != null);
        return returnData;
    }	
	
	protected void heartbeatFor (long user_id) {
	    heartbeatFor(user_id, sessionMap.get(user_id));
	}
	
	protected void heartbeatFor (long user_id, String sess) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", user_id + "");
	    params.put("session", sess);
	    JsonObject jsonObj = postAndValidateResponse("/heartbeat", params);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}
	
	protected void heartbeatForRoom (long user_id, String sess, long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", user_id + "");
	    params.put("room_ids", room_id + "");	 
	    params.put("session", sess);
	    JsonObject jsonObj = postAndValidateResponse("/heartbeat", params);
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

	protected String postAndValidateInner (String url, HashMap<String, String> params) {
		System.out.println("POST " + url);
		Response response = POST(url, params);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertCharset("utf-8", response);
		return response.out.toString();
	}

    protected JsonObject getJsonObj (String url, HashMap<String, String> params) {
        String jsonStr = postAndValidateInner(url, params);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
    }

    protected void postAndAssertOkay (String url, HashMap<String, String> params) {
        JsonObject jsonObj = getJsonObj(url, params);
		assertEquals("okay", jsonObj.get("status").getAsString());
    }

    protected void getAndAssertOkay (String url, HashMap<String, String> params) {
        url += "?";
        for (String k : params.keySet()) {
            String v = params.get(k);
            url += k + "=" + v + "&";
        }
        String jsonStr = getAndValidateInner(url);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		assertEquals("okay", jsonObj.get("status").getAsString());
    }

	protected JsonObject postAndValidateResponse (String url, HashMap<String, String> params) {
		String jsonStr = postAndValidateInner(url, params);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}

	protected JsonObject getAndValidateResponse (String url) {
		String jsonStr = getAndValidateInner(url);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}
	
	protected JsonObject usePower (long power_id, long user_id, String sess, long other_id, String other_sess, long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("power_id", power_id + "");
	    params.put("user_id", user_id + "");
	    if (other_id > 0) {
	        params.put("for_user", other_id + "");
	    }
	    if (!other_sess.equals("")) {
	        params.put("for_session", other_sess);
	    }
	    params.put("session", sess);
	    params.put("room_id", room_id + "");
	    return postAndValidateResponse("/usepower", params);
	}

    protected void signoutUser (long user_id, String session) {
        HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", user_id + "");
	    params.put("session", session);	    
		postAndAssertOkay("/signout", params);
    }
	
	protected void goToSleep (int seconds) {
	    try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
	}

}

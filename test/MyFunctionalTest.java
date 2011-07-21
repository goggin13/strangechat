import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import org.junit.*;
import java.util.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import models.*;
import java.lang.reflect .*;
import controllers.Notify;

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
        // System.out.println("listening response = " + jsonArr.toString());
		JsonObject jsonObj = jsonArr.get(jsonArr.size() - 1).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		return data;
	}

	protected JsonArray getWholeListenResponse (Long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}
	
	protected void heartbeatFor (Long user_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("for_user", user_id.toString());
	    JsonObject jsonObj = postAndValidateResponse("/heartbeat", params);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}
	
	protected void heartbeatForRoom (Long user_id, Long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("for_user", user_id.toString());
	    params.put("room_ids", room_id.toString());	    
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

    protected void postAndAssertOkay (String url, HashMap<String, String> params) {
        String jsonStr = postAndValidateInner(url, params);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		assertEquals("okay", jsonObj.get("status").getAsString());
    }

    protected void getAndAssertOkay (String url, HashMap<String, String> params) {
        url += "?";
        for (String k : params.keySet()) {
            String v = params.get(k);
            url += k + "=" + k + "&";
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
	
	protected void requestRoomFor (Long user_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", user_id.toString());
		postAndAssertOkay("/requestrandomroom", params);
	}
	
	protected void notifyLogout (Long for_user, Long left_user) {
	    HashMap<String, String> params = Notify.getNotifyLogoutParams(for_user, left_user);
		postAndAssertOkay("/notify/logout", params);
	}
	
	protected void notifyLogin (Long for_user, Long new_user) {
	    HashMap<String, String> params = Notify.getNotifyLoginParams(for_user, new_user, "name", "server");
		postAndAssertOkay("/notify/login", params);
	}	

    protected void notifyMessage (Long for_user, Long from_user, String msg) {
        HashMap<String, String> params = Notify.getNotifyMessageParams(from_user, for_user, msg);
		postAndAssertOkay("/notify/message", params);
    }

    protected void notifyChatMessage (Long from_user, Long for_user, String msg, Long room_id) {
		HashMap<String, String> params = Notify.getNotifyChatMessageParams(from_user, for_user, msg, room_id);
		postAndAssertOkay("/notify/roommessage", params);        
    }

    protected void notifyJoined (Long for_user, Long new_user, String avatar, String name, String server, Long room_id, String session_id) {
        HashMap<String, String> params = Notify.getNotifyJoinedParams(for_user, new_user, avatar, name, server, room_id, session_id);
    	postAndAssertOkay("/notify/joined", params);        
    }
     
    protected void notifyLeft (Long for_user, Long left_user, Long room_id) {
        HashMap<String, String> params = Notify.getNotifyLeftParams(for_user, left_user, room_id);
		postAndAssertOkay("/notify/left", params);
    }

    protected void notifyNewPower(Long for_user, StoredPower sp, String session_id) {
        HashMap<String, String> params = Notify.getNotifyNewPowerParams(for_user, sp.getSuperPower(), sp.id, sp.level, session_id);
        postAndAssertOkay("/notify/newpower", params);
    }

    protected void notifyUsedPower (Long for_user, Long by_user, Long room_id, SuperPower power, int level, String result, String session_id) {
        HashMap<String, String> params = Notify.getNotifyUsedPowerParams(for_user, by_user, room_id, power, level, result, session_id);
        postAndAssertOkay("/notify/usedpower", params);
    }

    protected void notifyTyping (Long for_user, Long user_id, Long room_id, String txt) {
		HashMap<String, String> params = Notify.getNotifyTypingParams(for_user, user_id, room_id, txt);
		postAndAssertOkay("/notify/useristyping", params);        
    }

	protected JsonObject usePower (Long power_id, Long user_id, Long other_id, Long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("power_id", power_id.toString());
	    params.put("user_id", user_id.toString());
	    params.put("other_id", other_id.toString());
	    params.put("room_id", room_id.toString());
	    return postAndValidateResponse("/usepower", params);
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

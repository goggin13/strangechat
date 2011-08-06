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
import play.libs.*;
import play.libs.F.*;
import jobs.*;
import models.powers.*;

public class MyFunctionalTest extends FunctionalTest {
	
	// These tokens were obtained using offline_access permissions from the facebook API, which are NOT like
	// the tokens this app will usually be receiving.  But they will hopefully last for testing.  
	protected static long next_id = 7L;
	
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
	
	@Test
	public void stub () {
		// do nothing
	}
	
	protected JsonArray getAdminListenResponse (int lastReceived) {
		String url = "/adminlisten?lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}
	
	protected JsonObject getListenResponse (long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived;
	    JsonArray jsonArr = getAndValidateAsArray(url);
        // System.out.println("listening response = " + jsonArr.toString());
		JsonObject jsonObj = jsonArr.get(jsonArr.size() - 1).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		return data;
	}

	protected JsonArray getWholeListenResponse (long id, int lastReceived) {
		String url = "/listen?user_id=" + id + "&lastReceived=" + lastReceived;
	    return getAndValidateAsArray(url);
	}

    protected JsonObject getListenItem(String type, long user_id, int lastReceived) {
        JsonArray arr =  getWholeListenResponse(user_id, lastReceived);
        for (JsonElement e : arr) {
            JsonObject data = e.getAsJsonObject().get("data").getAsJsonObject();
            if (data.get("type").getAsString().equals(type)) {
                return data;
            }
        }
        assertTrue(false);
        return null;
    }	
	
	protected void heartbeatFor (long user_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("for_user", user_id + "");
	    JsonObject jsonObj = postAndValidateResponse("/heartbeat", params);
		assertEquals("okay", jsonObj.get("status").getAsString());
	}
	
	protected void heartbeatForRoom (long user_id, long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("for_user", user_id + "");
	    params.put("room_ids", room_id + "");	    
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
	
	protected void requestRoomFor (long user_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("user_id", user_id + "");
		postAndAssertOkay("/requestrandomroom", params);
	}
	
	protected void notifyLogout (long for_user, long left_user) {
	    HashMap<String, String> params = Notify.getNotifyLogoutParams(for_user, left_user);
		postAndAssertOkay("/notify/logout", params);
	}
	
	protected void notifyLogin (long for_user, long new_user) {
	    HashMap<String, String> params = Notify.getNotifyLoginParams(for_user, new_user, "name", "server");
		postAndAssertOkay("/notify/login", params);
	}	

    protected void notifyMessage (long for_user, long from_user, String msg) {
        HashMap<String, String> params = Notify.getNotifyMessageParams(from_user, for_user, msg);
		postAndAssertOkay("/notify/message", params);
    }

    protected void notifyChatMessage (long from_user, long for_user, String msg, long room_id) {
		HashMap<String, String> params = Notify.getNotifyChatMessageParams(from_user, for_user, msg, room_id);
		postAndAssertOkay("/notify/roommessage", params);        
    }

    protected void notifyJoined (long for_user, long new_user, String avatar, String name, String server, long room_id, String session_id) {
        HashMap<String, String> params = Notify.getNotifyJoinedParams(for_user, new_user, avatar, name, server, room_id, session_id);
    	postAndAssertOkay("/notify/joined", params);        
    }
     
    protected void notifyLeft (long for_user, long left_user, long room_id) {
        HashMap<String, String> params = Notify.getNotifyLeftParams(for_user, left_user, room_id);
		postAndAssertOkay("/notify/left", params);
    }

    protected void notifyNewPower(long for_user, StoredPower sp, String session_id) {
        HashMap<String, String> params = Notify.getNotifyNewPowerParams(for_user, sp.getSuperPower(), sp.id, sp.level, session_id);
        postAndAssertOkay("/notify/newpower", params);
    }

    protected void notifyUsedPower (long for_user, long by_user, long room_id, SuperPower power, int level, String result, String session_id) {
        HashMap<String, String> params = Notify.getNotifyUsedPowerParams(for_user, by_user, room_id, power, level, result, session_id);
        postAndAssertOkay("/notify/usedpower", params);
    }

    protected void notifyTyping (long for_user, long user_id, long room_id, String txt) {
		HashMap<String, String> params = Notify.getNotifyTypingParams(for_user, user_id, room_id, txt);
		postAndAssertOkay("/notify/useristyping", params);        
    }

	protected JsonObject usePower (long power_id, long user_id, long other_id, long room_id) {
	    HashMap<String, String> params = new HashMap<String, String>();
	    params.put("power_id", power_id + "");
	    params.put("user_id", user_id + "");
	    params.put("other_id", other_id + "");
	    params.put("room_id", room_id + "");
	    return postAndValidateResponse("/usepower", params);
	}
	
	protected void goToSleep (int seconds) {
	    try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
	}
	
	protected int assertResponseContains (long user_id, String power_name, int level, int lastReceived) {
	    Promise<String> p = new CheckPowers().now();
        goToSleep(1);
        
        JsonArray arr = getWholeListenResponse(user_id, lastReceived);
		int received = 0;
		int lastLevel = 0;
        // int power_id = -1;
		
		for (JsonElement event : arr) {
		    JsonObject data = event.getAsJsonObject().get("data").getAsJsonObject();

		    if (data.get("type").getAsString().equals("newpower")) {
                JsonObject newPower = data.get("superPower").getAsJsonObject();
    		    System.out.println(newPower);
		        if (newPower.get("name").getAsString().equals(power_name)) {
		            received++;
		            lastLevel = Math.max(lastLevel, data.get("level").getAsInt());
		            power_id = data.get("power_id").getAsLong();
		        }
		    }
		    lastReceived = event.getAsJsonObject().get("id").getAsInt();
		}
		
		System.out.println("testing level " + level + " - " + power_name + ", received = " + received);
    	assertTrue(1 <= received);
    	assertEquals(level, lastLevel);
		return lastReceived;            

	}	
	
    protected int assertResponseHasIceBreaker (long user_id, int level, int lastReceived) {
        return assertResponseContains(user_id, "Ice Breaker", level, lastReceived);
    }
    
    protected int earnIceBreakers (long user1, long user2, int count, int lastReceived) {
	    for (int i = 0; i < count; i++) {
	        double time = new IceBreaker().award_interval;
	        double iters = Math.ceil(time / 5);
	        for (int j = 0; j < iters; j++) {
    		    heartbeatForRoom(user1, 15L);
    		    heartbeatForRoom(user1, 15L);    		    
    		}
    		// and now after we wait, PMO should have a superpower notifications
    		lastReceived = assertResponseHasIceBreaker(user1, 1, lastReceived);
	    }
	    return lastReceived;
    }
    
    protected void earnAndUseIceBreakers (long user1, long user2, int count) {
        // first lets earn some Ice Breakers
        earnIceBreakers(user1, user2, count, 0);

        // now use them!
        for (int i = 0; i < count; i++) {
            JsonObject json = usePower (power_id, user1, user2, 15L);
            assertEquals("okay", json.get("message").getAsString());
            assertEquals("okay", json.get("status").getAsString());
        }
        
        // again wont work though
        // JsonObject json = usePower (power_id, user1, user2, 15L);
        // assertEquals("You don't have any of that power remaining!", json.get("message").getAsString());
        // assertEquals("error", json.get("status").getAsString());        
	}	
	
}

import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import org.junit.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import models.*;

public class MyFunctionalTest extends FunctionalTest {
	
	@Test
	public void stub () {
		// do nothing
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
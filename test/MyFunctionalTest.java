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
	System.out.println(jsonArr);
		JsonObject jsonObj = jsonArr.get(0).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		return data;
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
		System.out.println("JSON = " + jsonStr);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}
	
}
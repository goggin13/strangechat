import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class ServersTest extends FunctionalTest {
	private static Long goggin13 = 100002292928724L;
	private static Long pmo_id = 24403414L;
	private static Long k_id = 411183L;
	private static String bad_id = "1234554312";	
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/initfull");
	}

	@Test
	public void testRoomRequest () {
		String url = "/getroomfor?user_1=" + goggin13 + "&user_2=" + pmo_id;
		System.out.println(url);
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("1", jsonObj.get("room_id").getAsString());
		assertEquals("chat1.com", jsonObj.get("server").getAsString());
		
		// now if we listen for pmo_id, he should get back an event telling him about the new room
		url = "/listen?user_id=" + pmo_id + "&lastReceived=0";
		System.out.println(url);
	    JsonArray jsonArr = getAndValidateAsArray(url);
		jsonObj = jsonArr.get(0).getAsJsonObject();
		JsonObject data = jsonObj.get("data").getAsJsonObject();
		assertEquals("listento", data.get("type").getAsString());
		assertEquals("1", data.get("room_id").getAsString());
		assertEquals("chat1.com", data.get("server").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());		
	}

	@Test
	public void testBadRoomRequest () {
		String url = "/getroomfor?user_1=" + goggin13 + "&user_2=" + bad_id;
		System.out.println(url);
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals(bad_id + " is no longer logged in", jsonObj.get("message").getAsString());
	}

	private JsonArray getAndValidateAsArray (String url) {
		String jsonStr = getAndValidateInner(url);
		System.out.println(jsonStr);
		JsonArray jsonArr = new JsonParser().parse(jsonStr).getAsJsonArray();
		return jsonArr;
	}

	private String getAndValidateInner (String url) {
		Response response = GET(url);
	    assertIsOk(response);
	    assertContentType("application/json", response);
	    assertCharset("utf-8", response);	
		String jsonStr = response.out.toString();
		return jsonStr;
	}

	private JsonObject getAndValidateResponse (String url) {
		String jsonStr = getAndValidateInner(url);
		System.out.println(jsonStr);
		JsonObject jsonObj = new JsonParser().parse(jsonStr).getAsJsonObject();
		return jsonObj;
	}
    
}
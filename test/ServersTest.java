import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class ServersTest extends MyFunctionalTest {
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
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("1", jsonObj.get("room_id").getAsString());
		assertEquals("chat1.com", jsonObj.get("server").getAsString());
		
		// now if we listen for pmo_id, he should get back an event telling him about the new room
		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("listento", data.get("type").getAsString());
		assertEquals("1", data.get("room_id").getAsString());
		assertEquals("chat1.com", data.get("server").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());	
		
		// now leave the room and chec tha goes okay
		url = "/leaveroom?server=chat1.com&room_id=1&user_id=" + pmo_id;
	    jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());	
	}

	@Test
	public void testBadRoomRequest () {
		String url = "/getroomfor?user_1=" + goggin13 + "&user_2=" + bad_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals(bad_id + " is no longer logged in", jsonObj.get("message").getAsString());
	}
    
}
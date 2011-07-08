import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class NotificationTests extends MyFunctionalTest {
	private static Long fb_id_1 = 100002292928724L;
	private static Long fb_id_2 = 32701378L;
	private static Long pmo_id = 24403414L;
	private static Long k_id = 411183L;
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	
	@Test
	public void testHeartbeatFadeout () {
		// send one heartbeat to register
		String url = "/heartbeat?for_user=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());
		
		try {
			System.out.println("sleeping");
			Thread.sleep(12000);
			System.out.println("waking up");			
		} catch (InterruptedException e){
			System.out.println(e.getMessage());
		}
		
		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("left_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}
	
	@Test
	public void testNotifyLogout () {
		String url = "/notify/logout?for_user=" + pmo_id + "&left_user=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("userlogout", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("left_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}

	@Test
	public void testNotifyLogin () {
		String url = "/notify/login?for_user=" + pmo_id + "&new_user=" + k_id;
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("userlogon", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("new_user").getAsString());
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}

	@Test
	public void testNotifyMessage () {
		String url = "/notify/message?for_user=" + pmo_id + "&from_user=" + k_id + "&msg=helloworld&";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("directmessage", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	} 

	@Test
	public void testNotifyChatMessage () {
		String url = "/notify/roommessage?for_user=" + pmo_id + "&from_user=" + k_id + "&msg=helloworld&room_id=45";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("roommessage", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("from").getAsString());
		assertEquals("helloworld", data.get("text").getAsString());	
		assertEquals("45", data.get("room_id").getAsString());			
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}

	@Test
	public void testNotifyJoined () {
		String url = "/notify/joined?for_user=" + pmo_id + "&new_user=" + k_id + "&avatar=www.avatar.com&room_id=14&name=kristen&server=chat1.com";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("join", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("new_user").getAsString());
		assertEquals("www.avatar.com", data.get("avatar").getAsString());
		assertEquals("14", data.get("room_id").getAsString());
		assertEquals("kristen", data.get("alias").getAsString());
		assertEquals("chat1.com", data.get("server").getAsString());		
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}

	@Test
	public void testNotifyLeft () {
		String url = "/notify/left?for_user=" + pmo_id + "&left_user=" + k_id + "&room_id=15";
	    JsonObject jsonObj = getAndValidateResponse(url);
		assertEquals("okay", jsonObj.get("status").getAsString());

		JsonObject data = getListenResponse(pmo_id, 0);
		assertEquals("leave", data.get("type").getAsString());
		assertEquals(k_id.toString(), data.get("left_user").getAsString());
		assertEquals("15", data.get("room_id").getAsString());		
		assertEquals(pmo_id.toString(), data.get("user_id").getAsString());
	}
	
}
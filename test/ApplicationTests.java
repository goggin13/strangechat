import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import com.google.gson.*;

public class ApplicationTests extends MyFunctionalTest {

	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}

	@Test
	public void testSelfRequest () {
		// String url = "/application/testselfrequest";
		// 	    JsonObject jsonObj = getAndValidateResponse(url);
		// assertEquals("okay", jsonObj.get("status").getAsString());
	} 
    
}
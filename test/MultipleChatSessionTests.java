import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;
import models.powers.*;
import jobs.*;
import com.google.gson.*;
import java.lang.reflect .*;
import com.google.gson.reflect.*;
import controllers.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;
import play.libs.*;
import play.libs.F.*;

public class MultipleChatSessionTests extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testJoining () {   
        // join a one on one chat room
        
        // join a group chat room
        
        // send a one on one message
            // make sure only you two get it
            
        // send a group message
            // make sure only the people in the group get it
        
        // use a power one on one
            // make sure only those two get it
        
        // use a power in the group room
            // make sure only those people see it
    }
}        
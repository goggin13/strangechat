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

public class IntervalRewardsTest extends MyFunctionalTest {
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	 	        
 	@Test
	public void testIceBreaker () {
	    int lastReceived = 0;
	    for (int i = 0; i < 4; i++) {
	        double time = IceBreaker.interval;
	        double iters = Math.ceil(time / 5);
	        
	        for (int j = 0; j < iters; j++) {
    		    heartbeatForRoom(pmo_db_id, 15L);
    		}

    		// and now after we wait, PMO should have a superpower notifications
    		lastReceived = assertResponseHasIceBreaker(pmo_db_id, 1, lastReceived);
	    }
	}  

 	@Test
	public void testBrainMash () {
        // int lastReceived = 0;
        // for (int i = 0; i < 4; i++) {
        //     double time = BrainMash.interval;
        //     double iters = Math.ceil(time / 5);
        //     
        //     for (int j = 0; j < iters; j++) {
        //              heartbeatForRoom(pmo_db_id, 15L);
        //          }
        // 
        //          // and now after we wait, PMO should have a superpower notifications
        //          lastReceived = assertResponseContains(pmo_db_id, "Brain Mash", 1, lastReceived);  
        // }
	}

}
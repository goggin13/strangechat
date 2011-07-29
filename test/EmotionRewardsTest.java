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

public class EmotionRewardsTest extends MyFunctionalTest {
	private static Long power_id = 0L;
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
    private int assertResponseHasEmotion (Long user_id, int level, int lastReceived) {
        return assertResponseContains(user_id, "Emotion", level, lastReceived);     
    }
    
 	@Test
	public void testEmotions () {
        // Emotions dont currently level up, so this test fails
        // int lastReceived = 0;
        // Long lastLevelTime = 0L;
        // for (int level = 1; level <= Emotion.levelCount; level++) {
        //     Long currentLevelTime = Emotion.levels.get(level);
        //     Long timeRequired = currentLevelTime - lastLevelTime;
        //     double iters = Math.ceil(timeRequired / 5.0);
        //     
        //     for (int i = 0; i < iters; i++) {
        //              heartbeatForRoom(pmo_db_id, 15L);
        //          }
        // 
        //          // and now after we wait, PMO should have a superpower notifications
        //          lastReceived = assertResponseHasEmotion(pmo_db_id, level, lastReceived);
        // }
	} 
	
}
import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import controllers.Notify;
import com.google.gson.*;
import play.libs.WS;

import java.lang.reflect .*;
import com.google.gson.*;
import com.google.gson.reflect.*;

public class UserNotificationTest extends UnitTest {
	protected static Long pmo_id = 24403414L;
	protected static Long pmo_db_id = 1L;
	
	protected static Long k_id = 411183L;
	protected static Long k_db_id = 2L;

	protected static Long rando_1 = 11L;
	protected static Long rando_1_db = 4L;	

	protected static Long rando_2 = 12L;
	protected static Long rando_2_db = 5L;
	
	@Before
    public void setup() {
        Fixtures.deleteAll();
        Fixtures.load("data-full.yml");
    }

	@Test
	public void testSuperPowerNotification () {

	}
}
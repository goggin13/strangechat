import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class UserTest extends UnitTest {

	@Before
    public void setup() {
        Fixtures.deleteAll();
		// Fixtures.load("data.yml");
    }

	@Test
	public void testBasicUserFunctions () {
		// first no users
		assertEquals(0, User.count());
		
		User u1 = User.getOrCreate(100L);
		User u2 = User.getOrCreate(101L);		
		assertEquals(2, User.count());
		
		User u3 = User.getOrCreate(100L);		
		assertEquals(2, User.count());
	}

}

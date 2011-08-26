import models.User;
import models.UserExclusion;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserTest extends UnitTest {
	protected static long pmo_id = 24403414L;
	protected static long pmo_db_id = 1L;
	protected static long k_id = 411183L;
	protected static long k_db_id = 2L;

	protected static long rando_1 = 11L;
	protected static long rando_1_db = 4L;	

	protected static long rando_2 = 12L;
	protected static long rando_2_db = 5L;
	protected static String pmo_session_id = "123";
	protected static String k_session_id = "321";
	protected static String fb_1_session_id = "324rwef675ds";
	protected static String fb_2_session_id = "324rwe65fdasdfs";	
	protected static String rando_1_session_id = "1324refd";
	protected static String rando_2_session_id = "ASfdgdfsefsd";
	
	@Before
    public void setup() {
        Fixtures.deleteAll();
        Fixtures.load("data-full.yml");
    }

    @Test
    public void testBasicUserFunctions () {
        // first no users
        assertEquals(7, User.count());
        
        User.getOrCreate(100);
        User.getOrCreate(101);       
        assertEquals(9, User.count());
        
        User.getOrCreate(100);       
        assertEquals(9, User.count());
    }
    
    @Test
    public void testUsersCanSpeak () {
        User u = User.getOrCreate(645);
        User u2 = User.getOrCreate(646);
        User u3 = User.getOrCreate(647);
        
        assertTrue(UserExclusion.canSpeak(u.id, u2.id));
        
        new UserExclusion(u, 1);
        new UserExclusion(u2, 1);     
        
        assertFalse(UserExclusion.canSpeak(u.id, u2.id));       
        
        assertTrue(UserExclusion.canSpeak(u.id, u3.id));        
    } 

}

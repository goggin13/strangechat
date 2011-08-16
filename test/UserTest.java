import models.Room;
import models.User;
import models.UserExclusion;
import models.UserSession;

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
        
        User u1 = User.getOrCreate(100);
        User u2 = User.getOrCreate(101);       
        assertEquals(9, User.count());
        
        User u3 = User.getOrCreate(100);       
        assertEquals(9, User.count());
    }
    
	@Test
	public void testRoomMeetupFunctions () {
		// start a room
		Room r = new Room(1);
		
		User pmo = User.findById(pmo_db_id);
		UserSession pmo_sess = new UserSession(pmo, pmo_session_id);
		User k = User.findById(k_db_id);
		UserSession k_sess = new UserSession(k, k_session_id);
		User rando_1 = User.findById(rando_1_db);
		UserSession rando_1_sess = new UserSession(rando_1, rando_1_session_id);
		User rando_2 = User.findById(rando_2_db);
		UserSession rando_2_sess = new UserSession(rando_2, rando_2_session_id);
		
        r.addUsers(pmo_sess, k_sess);
        assertEquals(2, r.participants.size());
        
        // should be first meeting
        assertEquals(0, Room.lastMeetingBetween(pmo_db_id, k_db_id));
        assertEquals(0, Room.lastMeetingBetween(k_db_id, pmo_db_id));
        
        // should never have met
        assertEquals(-1, Room.lastMeetingBetween(k_db_id, rando_1_db));
        
        // start another room
        r = new Room(2);
        r.addUsers(rando_1_sess, k_sess);
        
        // should be first meeting
        assertEquals(0, Room.lastMeetingBetween(pmo_db_id, k_db_id));
        assertEquals(0, Room.lastMeetingBetween(rando_1_db, k_db_id));
        
        assertEquals(0, Room.lastMeetingBetween(k_db_id, rando_1_db));
        assertEquals(1, Room.lastMeetingBetween(k_db_id, pmo_db_id));
                
        // and one more for good measure
        r = new Room(6);
        r.addUsers(k_sess, rando_2_sess);
        
        // should be first meeting
        assertEquals(0, Room.lastMeetingBetween(k_db_id, rando_2_db));
        assertEquals(1, Room.lastMeetingBetween(k_db_id, rando_1_db));   
        assertEquals(2, Room.lastMeetingBetween(k_db_id, pmo_db_id));
                             
        assertEquals(-1, Room.lastMeetingBetween(rando_1_db, rando_2_db));   
        
        assertTrue(Room.hasMetRecently(k_db_id, rando_1_db, 1)); 
        assertFalse(Room.hasMetRecently(rando_1_db, rando_2_db, 1)); 
        
        assertTrue(Room.hasMetRecently(k_db_id, pmo_db_id, 1));
        
        // get PMO some distance and see if they qualify now
        r = new Room(7);
        r.addUsers(pmo_sess, rando_1_sess);
        r = new Room(8);
        r.addUsers(pmo_sess, rando_2_sess);   
		assertFalse(Room.hasMetRecently(k_db_id, pmo_db_id, 1));				
	}
	
    // @Test
    // public void testAssigningAndUsingPowers () {
    //     User u = User.getOrCreate(645);
    //     User u2 = User.getOrCreate(646);
    //     int countPowers = 1;
    //     
    //     assertEquals(countPowers, u.superPowers.size());
    //     StoredPower sp = StoredPower.getOrCreate(Power.ICE_BREAKER, u);
    //     for (int i = 1; i <= MindReader.ICE_BREAKERS_LEVEL_2; i++) {
    //         sp.increment(1);
    //         assertEquals(countPowers, u.superPowers.size());
    //         assertEquals(i, u.countPowers(Power.ICE_BREAKER));
    //         assertEquals(i, u.countAvailablePowers(Power.ICE_BREAKER));
    //         assertEquals(0, u.countUsedPowers(Power.ICE_BREAKER));
    //     }
    // 
    //     for (int i = 1; i <= MindReader.ICE_BREAKERS_LEVEL_2; i++) {
    //         sp.use(u2);
    //         assertEquals(countPowers, u.superPowers.size());
    //         assertEquals(MindReader.ICE_BREAKERS_LEVEL_2, u.countPowers(Power.ICE_BREAKER));
    //         assertEquals(MindReader.ICE_BREAKERS_LEVEL_2 - i, u.countAvailablePowers(Power.ICE_BREAKER));           
    //         assertEquals(i, u.countUsedPowers(Power.ICE_BREAKER));
    //     }
    // 
    //     // test is qualified
    //     assertTrue(new MindReader().isQualified(u) > 0);
    // }
    
    @Test
    public void testUsersCanSpeak () {
        User u = User.getOrCreate(645);
        User u2 = User.getOrCreate(646);
        User u3 = User.getOrCreate(647);
        
        assertTrue(UserExclusion.canSpeak(u.id, u2.id));
        
        UserExclusion ue = new UserExclusion(u, 1);
        ue = new UserExclusion(u2, 1);     
        
        assertFalse(UserExclusion.canSpeak(u.id, u2.id));       
        
        assertTrue(UserExclusion.canSpeak(u.id, u3.id));        
    } 

}

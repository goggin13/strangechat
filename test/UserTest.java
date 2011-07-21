import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import java.util.concurrent.ConcurrentHashMap;
import enums.*;
import models.powers.*;

public class UserTest extends UnitTest {
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
	public void testBasicUserFunctions () {
		// first no users
		assertEquals(5, User.count());
		
		User u1 = User.getOrCreate(100L);
		User u2 = User.getOrCreate(101L);		
		assertEquals(7, User.count());
		
		User u3 = User.getOrCreate(100L);		
		assertEquals(7, User.count());
	}
    
	@Test
	public void testRoomMeetupFunctions () {
	    // reset meetings first
	    Room.recentMeetings = new ConcurrentHashMap<Long, List<Long>>();
		
		// start a room
		Room r = new Room(1L);
		r.addUsers(pmo_db_id, k_db_id);
		assertEquals(2, r.participants.size());
		
		// should be first meeting
		assertEquals(0, Room.lastMeetingBetween(pmo_db_id, k_db_id));
		assertEquals(0, Room.lastMeetingBetween(k_db_id, pmo_db_id));
		
		// should never have met
        assertEquals(-1, Room.lastMeetingBetween(k_db_id, rando_1_db));
        
        // start another room
		r = new Room(2L);
		r.addUsers(rando_1_db, k_db_id);
		
		// should be first meeting
		assertEquals(0, Room.lastMeetingBetween(pmo_db_id, k_db_id));
		assertEquals(0, Room.lastMeetingBetween(rando_1_db, k_db_id));
		
		assertEquals(0, Room.lastMeetingBetween(k_db_id, rando_1_db));
		assertEquals(1, Room.lastMeetingBetween(k_db_id, pmo_db_id));
				
        // and one more for good measure
		r = new Room(6L);
		r.addUsers(k_db_id, rando_2_db);
		
		// should be first meeting
        assertEquals(0, Room.lastMeetingBetween(k_db_id, rando_2_db));
		assertEquals(1, Room.lastMeetingBetween(k_db_id, rando_1_db));	
		assertEquals(2, Room.lastMeetingBetween(k_db_id, pmo_db_id));
        				
		assertEquals(-1, Room.lastMeetingBetween(rando_1_db, rando_2_db));	
		
		assertTrue(Room.hasMetRecently(k_db_id, rando_1_db, 1));	
		assertFalse(Room.hasMetRecently(rando_1_db, rando_2_db, 1));	
		
		assertTrue(Room.hasMetRecently(k_db_id, pmo_db_id, 1));
		
		// get PMO some distance and see if they qualify now
		r = new Room(7L);
		r.addUsers(pmo_db_id, rando_1_db);
		r = new Room(8L);
		r.addUsers(pmo_db_id, rando_2_db);	
		assertFalse(Room.hasMetRecently(k_db_id, pmo_db_id, 1));				
	}
	
	@Test
	public void testAssigningAndUsingPowers () {
	    User u = User.getOrCreate(645L);
	    
	    assertEquals(1, u.superPowers.size());
	    StoredPower.incrementPowerForUser(Power.ICE_BREAKER, u, 1);
	    
	    assertEquals(1, u.superPowers.size());
	    assertEquals(2, u.countPowers(Power.ICE_BREAKER, 0));
	    assertEquals(0, u.countPowers(Power.ICE_BREAKER, 2));
	    
	    StoredPower.incrementPowerForUser(Power.ICE_BREAKER, u, 1);
	    assertEquals(1, u.superPowers.size());
	    assertEquals(3, u.countPowers(Power.ICE_BREAKER, 0));
	    assertEquals(3, u.countPowers(Power.ICE_BREAKER, 1));
	    assertEquals(0, u.countPowers(Power.ICE_BREAKER, 2));
	    
	    StoredPower.usePowerForUser(Power.ICE_BREAKER, u);
	    assertEquals(1, u.superPowers.size());
	    assertEquals(3, u.countPowers(Power.ICE_BREAKER, 0));
	    assertEquals(2, u.countPowers(Power.ICE_BREAKER, 1));
	    assertEquals(1, u.countPowers(Power.ICE_BREAKER, 2));
	    
	    // test is qualified
	    assertFalse(new IceBreaker().isQualified(u) > 0);
	    assertFalse(new XRayLevelOne().isQualified(u) > 0);
	    assertFalse(new MindReader().isQualified(u) > 0);
	}

}

import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserTest extends UnitTest {
	private static Long rando_1 = 11L;
	private static Long rando_2 = 12L;
	private static Long pmo_id = 24403414L;
	private static Long k_id = 411183L;	
	
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
		r.addUsers(pmo_id, k_id);
		assertEquals(2, r.participants.size());
		
		// should be first meeting
		assertEquals(0, Room.lastMeetingBetween(pmo_id, k_id));
		assertEquals(0, Room.lastMeetingBetween(k_id, pmo_id));
		
		// should never have met
        System.out.println(Room.recentMeetings);
        assertEquals(-1, Room.lastMeetingBetween(k_id, rando_1));
        
        // start another room
		r = new Room(2L);
		r.addUsers(rando_1, k_id);
		
		// should be first meeting
		assertEquals(0, Room.lastMeetingBetween(pmo_id, k_id));
		assertEquals(0, Room.lastMeetingBetween(rando_1, k_id));
		
		assertEquals(0, Room.lastMeetingBetween(k_id, rando_1));
		assertEquals(1, Room.lastMeetingBetween(k_id, pmo_id));
				
        // and one more for good measure
		r = new Room(6L);
		r.addUsers(k_id, rando_2);
		
		// should be first meeting
        assertEquals(0, Room.lastMeetingBetween(k_id, rando_2));
		assertEquals(1, Room.lastMeetingBetween(k_id, rando_1));	
		assertEquals(2, Room.lastMeetingBetween(k_id, pmo_id));
        				
		assertEquals(-1, Room.lastMeetingBetween(rando_1, rando_2));	
		
		assertTrue(Room.hasMetRecently(k_id, rando_1, 1));	
		assertFalse(Room.hasMetRecently(rando_1, rando_2, 1));	
		
		assertTrue(Room.hasMetRecently(k_id, pmo_id, 1));
		
		// get PMO some distance and see if they qualify now
		r = new Room(7L);
		r.addUsers(pmo_id, rando_1);
		r = new Room(8L);
		r.addUsers(pmo_id, rando_2);	
		assertFalse(Room.hasMetRecently(k_id, pmo_id, 1));				
	}

}

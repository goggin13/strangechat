import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import java.util.concurrent.ConcurrentHashMap;
import enums.*;
import models.powers.*;

public class IceBreakersTest extends UnitTest {
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
	public void testIceBreakerHasSeenAssignment () {
	    // set up the users we need
	    int total = IceBreaker.iceBreakersCount();
	    Set<Integer> all = new TreeSet<Integer>();
	    Set<Integer> some1 = new TreeSet<Integer>();
	    Set<Integer> some2 = new TreeSet<Integer>();
	    Set<Integer> allbut1 = new TreeSet<Integer>();
	    Random r = new Random();
	    for (int i = 0; i < total; i++) {
	        all.add(i);
	        if (r.nextInt(100) % 2 == 0) {
	            some1.add(i);
	        }
	        if (r.nextInt(100) % 2 == 0) {
	            some2.add(i);
	        }	        
	        if (i != total - 1) {
	            allbut1.add(i);
	        }
	    }
        User seenAll1 = User.findById(pmo_db_id);
        seenAll1.icebreakers_seen = all;
        seenAll1.save();

        User seenAll2 = User.findById(rando_2_db);
        seenAll2.icebreakers_seen = all;
        seenAll2.save();
        
        User seenSome1 = User.findById(k_db_id);
        seenSome1.icebreakers_seen = some1;
        seenSome1.save();

        User seenSome2 = User.findById(rando_1_db);                
        seenSome2.icebreakers_seen = some2;
        seenSome2.save(); 
        
        //phew.  Okay now check we get appropriately seen and unseen icebreakers.
        // since there is some randomness we will iterate the tests so hopefully we
        // aren't just getting lucky
        IceBreaker sp = new IceBreaker();
        int index;
        for (int k = 0; k < 50; k++) {
            index = sp.chooseIndex(seenAll1, seenSome1);            
            assertTrue(seenAll1.seenIceBreaker(index));
            assertFalse(seenSome1.seenIceBreaker(index));  
        }     
        
        for (int k = 0; k < 50; k++) {
            index = sp.chooseIndex(seenSome1, seenSome2);     
            assertFalse(seenSome2.seenIceBreaker(index));
            assertFalse(seenSome1.seenIceBreaker(index));  
        }    
        
        for (int k = 0; k < 50; k++) {
            index = sp.chooseIndex(seenAll1, seenAll2);     
            assertTrue(seenAll1.seenIceBreaker(index));
            assertTrue(seenAll2.seenIceBreaker(index)); 
        }            
        
        // try it with the all but 1 case
        seenSome1.icebreakers_seen = allbut1;
        seenSome1.save();
        seenSome2.icebreakers_seen = allbut1;
        seenSome2.save();
        
        index = sp.chooseIndex(seenSome1, seenSome2);     
        assertFalse(seenSome1.seenIceBreaker(index));
        assertFalse(seenSome2.seenIceBreaker(index)); 
        
        // should mark it seen
        sp.use(seenSome1, seenSome2);
        index = sp.chooseIndex(seenSome1, seenSome2);     
        assertTrue(seenSome1.seenIceBreaker(index));
        assertTrue(seenSome2.seenIceBreaker(index));
        
        seenSome1.icebreakers_seen = new TreeSet<Integer>();
        seenSome1.save();
        seenSome2.icebreakers_seen = new TreeSet<Integer>();
        seenSome2.save();
        index = sp.chooseIndex(seenSome1, seenSome2);     
        assertFalse(seenSome1.seenIceBreaker(index));
        assertFalse(seenSome2.seenIceBreaker(index)); 
    }

    @Test
    public void testRandomIceBreakers () {
        int total = IceBreaker.iceBreakersCount();
        IceBreaker ice = new IceBreaker();
        
        User u1 = User.getOrCreate(1L);
        User u2 = User.getOrCreate(1L);        
        List<String> seen = new LinkedList<String>();
        
        for (int i = 0; i < total; i++) {
            String msg = ice.use(u1, u2);
            assertFalse(seen.contains(msg));
            seen.add(msg);
        }
        
        String msg = ice.use(u1, u2);
        assertTrue(seen.contains(msg));
    }

}

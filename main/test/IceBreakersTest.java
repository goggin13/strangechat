import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import models.User;
import models.powers.IceBreaker;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

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
		Fixtures.deleteDatabase();
		Fixtures.loadModels("data-full.yml");
	}

    @Test
    public void testIceBreakerHasSeenAssignment() {
        // set up the users we need
        int total = IceBreaker.iceBreakersCount();
        
        User seenAll1 = User.getOrCreate(pmo_db_id);
        User seenAll2 = User.getOrCreate(rando_2_db);
        User seenSome1 = User.getOrCreate(k_db_id);
        User seenSome2 = User.getOrCreate(rando_1_db);
        User seenAllButOne_1 = User.getOrCreate(457L);
        User seenAllButOne_2 = User.getOrCreate(458L);
        
        Random r = new Random();
        for (int i = 0; i < total; i++) {
            seenAll1.addSeenIceBreaker(i);
            seenAll2.addSeenIceBreaker(i);
            if (r.nextInt(100) % 2 == 0) {
                seenSome1.addSeenIceBreaker(i);
            }
            if (r.nextInt(100) % 2 == 0) {
                seenSome2.addSeenIceBreaker(i);
            }
            if (i != total - 1) {
                seenAllButOne_1.addSeenIceBreaker(i);
                seenAllButOne_2.addSeenIceBreaker(i);
            }
        }

        // phew. Okay now check we get appropriately seen and unseen
        // icebreakers.
        // since there is some randomness we will iterate the tests so hopefully
        // we
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

        index = sp.chooseIndex(seenAllButOne_1, seenAllButOne_1);
        assertFalse(seenAllButOne_1.seenIceBreaker(index));
        assertFalse(seenAllButOne_1.seenIceBreaker(index));

        // should mark it seen
        sp.use(seenAllButOne_1, seenAllButOne_1);
        index = sp.chooseIndex(seenAllButOne_1, seenAllButOne_1);
        assertTrue(seenAllButOne_1.seenIceBreaker(index));
        assertTrue(seenAllButOne_1.seenIceBreaker(index));

    }

    @Test
    public void testRandomIceBreakers() {
        int total = IceBreaker.iceBreakersCount();
        IceBreaker ice = new IceBreaker();

        User u1 = User.getOrCreate(1);
        User u2 = User.getOrCreate(1);
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

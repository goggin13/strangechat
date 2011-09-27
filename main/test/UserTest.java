import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import models.User;
import models.UserExclusion;
import models.powers.StoredPower;
import models.karma.*;
import enums.*;

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
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data-full.yml");
    }


    public void testWinningCountAmount (double pct, int expectedMin, int expectedMax, User user) {
        for (int i = 0; i < 50; i++) {
    	    int winnings = user.awardTriviaCoins(pct);  
            assertTrue(winnings >= expectedMin);
            assertTrue(winnings <= expectedMax);    
    	}
    }
    
    @Test
    public void testAwardRandomCoins () {
    	User user = User.getOrCreate(5000);
        testWinningCountAmount(0.0, 1, 1, user);
        testWinningCountAmount(0.26, 5, 8, user);
    	testWinningCountAmount(0.51, 10, 15, user);    	
        testWinningCountAmount(0.76, 15, 25, user);              
    }
    
    @Test
    public void testUserExclusions () {
    	User user1 = User.getOrCreate(340L);
    	User user2 = User.getOrCreate(341L);
    	User user3 = User.getOrCreate(342L);
    	new UserExclusion(user1, 1);
    	new UserExclusion(user2, 1);
    	new UserExclusion(user3, 1);
    	
    	List<Long> test = new LinkedList<Long>();
    	test.add(user2.id);
    	test.add(user3.id);
    	user1.login();
    	assertEquals(test, user1.excludedUsers);
    } 

    public void givePowersToUser (User user, Power power, int countAvailable, int countUsed, int level) {        
        StoredPower sp = new StoredPower(power, user);
        sp.level = level;
        sp.available = countAvailable;
        sp.used = countUsed;        
        sp.save();
    }
    
    public void initRandomFields (User user) {
        Random r = new Random();
        user.coinCount = Math.abs(r.nextInt() % 10 + 1);
        user.coinsEarned = Math.abs(r.nextInt() % 10 + 1);
        user.chatTime = Math.abs(r.nextInt() % 10 + 1);
        user.messageCount = Math.abs(r.nextInt() % 10 + 1);
        user.gotMessageCount = Math.abs(r.nextInt() % 10 + 1);
        user.joinCount = Math.abs(r.nextInt() % 10 + 1);
        user.offersMadeCount = Math.abs(r.nextInt() % 10 + 1);
        user.offersReceivedCount = Math.abs(r.nextInt() % 10 + 1);
        user.revealCount = Math.abs(r.nextInt() % 10 + 1);
        user.save();
        
        for (int i = 0; i < 10; i++) {
            new KarmaKube(User.getOrCreate(r.nextInt()), user, r.nextInt() % 2 == 0).save();
        }
        
        for (int i = 0; i < 10; i++) {
            new UserExclusion(user.id, r.nextInt());
        }
        
    }
    
    @Test
    public void testConsumeUser () {
        User user1 = User.find("byAlias", "Patrick Moberg").first();
        User user2 = User.find("byAlias", "Matthew Goggin").first();
        initRandomFields(user1);
        initRandomFields(user2);
        
        Random r = new Random();
        for (int i = 0; i < 30; i++) {
            if (r.nextInt() % 2 == 0) {
                user1.addSeenIceBreaker(i);
            } else {
                user2.addSeenIceBreaker(i);        
            }
        }
        
        // same level
        givePowersToUser(user1, Power.ICE_BREAKER, 3, 0, 1);
        givePowersToUser(user2, Power.ICE_BREAKER, 5, 4, 1);
        
        // user 1 higher
        givePowersToUser(user1, Power.MIND_READER, 5, 2, 2);
        givePowersToUser(user2, Power.MIND_READER, 3, 1, 1);
        
        // user 2 lower
        givePowersToUser(user1, Power.EMOTION, 5, 2, 1);
        givePowersToUser(user2, Power.EMOTION, 3, 7, 2);

        givePowersToUser(user1, Power.KARMA, 7, 1, 1);
        givePowersToUser(user2, Power.KARMA, 5, 4, 1);
        
        int expected_kubeCount = user1.getKubes().size() + user2.getKubes().size();
                                 
        int expected_exclusionCount = UserExclusion.userGroups(user1.id).size() + 
                                        UserExclusion.userGroups(user2.id).size();
                                        
        int expected_iceBreakerCount = user1.getSeenIceBreakers().size() 
                                        + user2.getSeenIceBreakers().size();
                                        
        
        int expected_coinCount = user1.coinCount + user2.coinCount;
        int expected_coinsEarned = user1.coinsEarned + user2.coinsEarned;
        long expected_chatTime = user1.chatTime + user2.chatTime;
        int expected_messageCount = user1.messageCount + user2.messageCount;
        int expected_gotMessageCount = user1.gotMessageCount + user2.gotMessageCount;
        int expected_joinCount = user1.joinCount + user2.joinCount;
        int expected_offersMadeCount = user1.offersMadeCount + user2.offersMadeCount;
        int expected_offersReceivedCount = user1.offersReceivedCount + user2.offersReceivedCount;
        int expected_revealCount = user1.revealCount + user2.revealCount;
        
        user1.consume(user2);
        
        User deleted = User.find("byAlias", "Matthew Goggin").first();
        assertNull(deleted);
    
        assertEquals(4, user1.countUsedPowers(Power.ICE_BREAKER));
        assertEquals(8 - User.INITIAL_ICE_BREAKERS, user1.countAvailablePowers(Power.ICE_BREAKER));
        assertEquals(1, user1.currentLevel(Power.ICE_BREAKER));

        assertEquals(5, user1.countUsedPowers(Power.KARMA));
        assertEquals(12 - User.INITIAL_KARMA, user1.countAvailablePowers(Power.KARMA));
        assertEquals(1, user1.currentLevel(Power.KARMA));

        assertEquals(3, user1.countUsedPowers(Power.MIND_READER));
        assertEquals(8, user1.countAvailablePowers(Power.MIND_READER));
        assertEquals(2, user1.currentLevel(Power.MIND_READER));

        assertEquals(9, user1.countUsedPowers(Power.EMOTION));
        assertEquals(8, user1.countAvailablePowers(Power.EMOTION));
        assertEquals(2, user1.currentLevel(Power.EMOTION));
            
        assertEquals(expected_iceBreakerCount, user1.getSeenIceBreakers().size());
        assertEquals(expected_kubeCount, user1.getKubes().size());
        assertEquals(expected_exclusionCount, UserExclusion.userGroups(user1.id).size());
                
        assertTrue(expected_coinCount > 0);
        assertEquals(user1.coinCount, expected_coinCount);
        assertEquals(user1.coinsEarned, expected_coinsEarned);
        assertEquals(user1.chatTime, expected_chatTime);
        assertEquals(user1.messageCount, expected_messageCount);
        assertEquals(user1.gotMessageCount, expected_gotMessageCount);
        assertEquals(user1.joinCount, expected_joinCount);
        assertEquals(user1.offersMadeCount, expected_offersMadeCount);
        assertEquals(user1.offersReceivedCount, expected_offersReceivedCount);
        assertEquals(user1.revealCount, expected_revealCount);
    }
}

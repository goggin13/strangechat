import jobs.CheckPowers;
import models.powers.Omniscience;
import models.powers.XRayVision;

import org.junit.Test;

public class IntervalRewardsTest extends MyFunctionalTest {
	
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	 	        
    // @Test
    // public void testIceBreaker () {
    //     int lastReceived = 0;
    //     for (int i = 0; i < 4; i++) {
    //         double time = new IceBreaker().award_interval;
    //         double iters = Math.ceil(time / 5);
    //         
    //         for (int j = 0; j < iters; j++) {
    //              heartbeatForRoom(pmo_db_id, pmo_session, 15L);
    //          }
    // 
    //          // and now after we wait, PMO should have a superpower notifications
    //          lastReceived = assertResponseHasIceBreaker(pmo_db_id, 1, lastReceived);
    //     }
    // }  

    //      @Test
    // public void testBrainMash () {
        // int lastReceived = 0;
        // for (int i = 0; i < 4; i++) {
        //     double time = BrainMash.interval;
        //     double iters = Math.ceil(time / 5);
        //     
        //     for (int j = 0; j < iters; j++) {
        //              heartbeatForRoom(pmo_db_id, pmo_session, 15L);
        //          }
        // 
        //          // and now after we wait, PMO should have a superpower notifications
        //          lastReceived = assertResponseContains(pmo_db_id, "Brain Mash", 1, lastReceived);  
        // }
    // }
	
	@Test
	public void testOmniscience () {
        
        double time = Math.ceil(new Omniscience().award_interval) + 1;
        // keep them alive and wait for checkpowers to run
		for (int i = 0; i < time; i++) {
		    heartbeatForRoom(pmo_db_id, pmo_session, 15L);
		}
        
		// and now after we wait, PMO should have a superpower notifications
        assertResponseContains(pmo_db_id, "Omniscience", 1, 0);  		
	}
	
    @Test
	public void testXRayLevel1 () {
        double reveals = new XRayVision().award_interval + 1;
		for (int i = 0; i < reveals; i++) {
            notifyChatMessage(pmo_db_id, k_db_id, CheckPowers.REVEAL_CODE, 15L);
		}
		assertResponseContains(pmo_db_id, "X Ray Vision", 1, 0);  	
	}	

}
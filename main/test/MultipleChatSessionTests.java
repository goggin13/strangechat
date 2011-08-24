import org.junit.Test;

public class MultipleChatSessionTests extends MyFunctionalTest {
		
	@org.junit.Before
	public void setUp() {
	    GET("/mock/init");
	}
	       
	@Test
	public void testJoining () {   
        // join a one on one chat room
        
        // join a group chat room
        
        // send a one on one message
            // make sure only you two get it
            
        // send a group message
            // make sure only the people in the group get it
        
        // use a power one on one
            // make sure only those two get it
        
        // use a power in the group room
            // make sure only those people see it
    }
}        
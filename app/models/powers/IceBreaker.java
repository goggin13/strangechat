package models.powers;
import java.util.*;
import enums.Power;
import play.Logger;
import models.*;

public class IceBreaker extends SuperPower {
	public static int CHAT_SECONDS_REQUIRED = 10;
	private IceBreakers breakers = new IceBreakers();
	
	public IceBreaker() {
		super(
		    "Ice Breaker",
			"http://lh3.ggpht.com/HygyyzxG6-EgjNjQvlfszfqXOEx4144eSqUCEKAEay7Fp_vYIIsAk0N1lp18Hdbh-aopXHo84oBPxiqK",
			"Display an ice breaker message",
			false
		);
	}
		
	public boolean isQualified (User user) {
	    Power p = this.getPower();
		long secsUsed = CHAT_SECONDS_REQUIRED * user.countPowers(p, 0);
		return (user.chatTime == null ? 0 : user.chatTime) - secsUsed > CHAT_SECONDS_REQUIRED;
	}
	
	public String use () {
	    return breakers.getRandom();
	}
	
	private static class IceBreakers {
	    private static List<String> messages = new ArrayList<String>();
	    private static Random r = new Random();
	    
	    private IceBreakers () {
	        messages.add("How much does a polar bear weigh?");
	        messages.add("Where were you born");
	        messages.add("a/s/l?");
	    }
	    
	    public static String getRandom () {
	        int size = messages.size();
	        int rand = r.nextInt(size);
	        return messages.get(rand);
	    }
	}
	
}
	

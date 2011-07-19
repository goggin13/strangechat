package models;
import java.util.*;
import enums.Power;
import play.Logger;

public class IceBreaker extends SuperPower {
	private static final int CHAT_SECONDS_REQUIRED = 10;
	
	public IceBreaker() {
		super(
		    "Ice Breaker",
			"http://lh3.ggpht.com/HygyyzxG6-EgjNjQvlfszfqXOEx4144eSqUCEKAEay7Fp_vYIIsAk0N1lp18Hdbh-aopXHo84oBPxiqK",
			"Display an ice breaker message",
			false
		);
	}
	
	public Power getPower () {
		return Power.ICE_BREAKER;
	}
		
	public boolean isQualified (User user) {
		long secsUsed = CHAT_SECONDS_REQUIRED * user.countPowers(this.getPower(), true);
		return (user.chatTime == null ? 0 : user.chatTime) - secsUsed > CHAT_SECONDS_REQUIRED;
	}
}
	

package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class XRayLevelOne extends SuperPower {
	private static final int CHAT_MESSAGES_REQUIRED = 40;
	
	public XRayLevelOne() {
		super(
		    "X Ray Level 1",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"See if the other user has revealed themselves",
			false
		);
	}
		
	public boolean isQualified (User user) {
	    Power p = this.getPower();
		long msgsUsed = CHAT_MESSAGES_REQUIRED * user.countPowers(p, 0);
		return user.messageCount - msgsUsed > CHAT_MESSAGES_REQUIRED;
	}
	
	public String use () {
	    return "X-Ray";
	}
}

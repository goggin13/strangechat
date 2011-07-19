package models;
import java.util.*;
import enums.Power;

public class XRayLevelOne extends SuperPower {
	private static final int CHAT_MESSAGES_REQUIRED = 40;
	
	public XRayLevelOne() {
		super(
		    "X Ray Level 1",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"See when the other user is typing",
			false
		);
	}
	
	public Power getPower () {
		return Power.X_RAY_LEVEL_1;
	}	
	
	public boolean isQualified (User user) {
		long msgsUsed = CHAT_MESSAGES_REQUIRED * user.countPowers(this.getPower(), true);
		return user.messageCount - msgsUsed > CHAT_MESSAGES_REQUIRED;
	}
	
}

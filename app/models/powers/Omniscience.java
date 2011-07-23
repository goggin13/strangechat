package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class Omniscience extends SuperPower {
	public static final int CHAT_TIME_REQUIRED = 30;
	
	public Omniscience () {
		super(
		    "Omniscience",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"Receive notifications whenever the other user uses super powers",
			false,
			true
		);
	}
	
	public int isQualified (User user) {
	    Power p = this.getPower();
		long secsUsed = CHAT_TIME_REQUIRED * user.countPowers(p);
		if (user.chatTime == null) {
		    return 0;
		}
		return user.chatTime - secsUsed > CHAT_TIME_REQUIRED ? 1 : 0;
	}
	
	public String use (User caller, User subject) {
	    return "omniscient";
	}
	
}

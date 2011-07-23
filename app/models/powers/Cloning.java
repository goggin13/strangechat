package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class Cloning extends SuperPower {
	public static final int CHAT_MSGS_REQUIRED = 20;
	
	public Cloning () {
		super(
		    "Cloning",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"Hold multiple chats",
			true,
			false
		);
	}
	
	public int isQualified (User user) {
	    Power p = this.getPower();
        int currentLevel = user.currentLevel(p);
	    if (currentLevel > 0) {
	        return 0;
	    }	    
		long msgsUsed = CHAT_MSGS_REQUIRED * user.countPowers(p);
		return user.messageCount - msgsUsed > CHAT_MSGS_REQUIRED ? 1 : 0;
	}
	
	public String use (User caller, User subject) {
	    return "cloning";
	}
	
}

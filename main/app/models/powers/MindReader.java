package models.powers;
import models.User;
import enums.Power;

public class MindReader extends SuperPower {
	public static final int ICE_BREAKERS_REQUIRED = 3;
	public static final int ICE_BREAKERS_LEVEL_2 = 6;
	
	public MindReader () {
		super(
		    "Mind Reader",
			"http://lh5.ggpht.com/-9NUoHdm3HKbEaU4zmOwJe4xD26Jf4YJFzsKAxnS72kQ30-sOiRYMG9hAIMats73tx64m4B27ibHR_w",
			"See when the other user is typing",
			true,
			true
		);
	    this.multiRoom = true;		
	}
		
	public int isQualified (User user) {
	    Power p = this.getPower();
	    int currentLevel = user.currentLevel(p);
	    int usedIceBreakers = user.countUsedPowers(Power.ICE_BREAKER);
	    
	    if (currentLevel >= 2) {
	        return 0;
	    }
	    if (usedIceBreakers >= ICE_BREAKERS_LEVEL_2  && currentLevel < 2) {
	        return 2;
	    } else if (usedIceBreakers >= ICE_BREAKERS_REQUIRED && currentLevel < 1) {
            return 1;
        } else {
            return 0;
        }
	}
	
}

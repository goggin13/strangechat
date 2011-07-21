package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class MindReader extends SuperPower {
	private static int ICE_BREAKERS_REQUIRED = 3;
	private static int ICE_BREAKERS_LEVEL_2 = 6;
	
	public MindReader () {
		super(
		    "Mind Reader",
			"http://lh5.ggpht.com/-9NUoHdm3HKbEaU4zmOwJe4xD26Jf4YJFzsKAxnS72kQ30-sOiRYMG9hAIMats73tx64m4B27ibHR_w",
			"See when the other user is typing",
			true
		);
	}
		
	public int isQualified (User user) {
	    Power p = this.getPower();
	    int currentLevel = user.currentLevel(p);
	    int usedIceBreakers = user.countPowers(new IceBreaker().getPower(), 2);
	    
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

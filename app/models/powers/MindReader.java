package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class MindReader extends SuperPower {
	private static int ICE_BREAKERS_REQUIRED = 3;
	
	public MindReader () {
		super(
		    "Mind Reader",
			"http://lh5.ggpht.com/-9NUoHdm3HKbEaU4zmOwJe4xD26Jf4YJFzsKAxnS72kQ30-sOiRYMG9hAIMats73tx64m4B27ibHR_w",
			"See when the other user is typing",
			true
		);
	}
		
	public boolean isQualified (User user) {
	    Power p = this.getPower();
	    int mindReaderCount = user.countPowers(p, 0);
        if (mindReaderCount > 0) {
            return false; // can only have one
        }
        int iceCount = user.countPowers(new IceBreaker().getPower(), 2);
        System.out.println(iceCount >= ICE_BREAKERS_REQUIRED);
		return iceCount >= ICE_BREAKERS_REQUIRED;
	}
	
}

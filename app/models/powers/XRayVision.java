package models.powers;
import java.util.*;
import enums.Power;
import models.*;

public class XRayVision extends SuperPower {
	public static final int REVEALS_REQUIRED = 3;
	
	public XRayVision() {
		super(
		    "X Ray Vision",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"See if the other user has revealed themselves",
			false
		);
	}
		
	public int isQualified (User user) {
	    Power p = this.getPower();
		long revealsUsed = REVEALS_REQUIRED * user.countPowers(p, 0);
		return user.offersMadeCount - revealsUsed > REVEALS_REQUIRED ? 1 : 0;
	}
	
	public String use (User caller, User subject) {
	    return "X-Ray";
	}
}

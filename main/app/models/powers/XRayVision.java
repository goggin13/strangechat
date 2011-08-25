package models.powers;
import models.User;

public class XRayVision extends IntervalPower {
	
	public XRayVision() {
		super(
		    "X Ray Vision",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"See if the other user has revealed themselves",
			false,
			true
		);
		this.award_interval = 3;
	}
		
	public Long getFieldValue (User user) {	
	    return Long.valueOf(user.offersMadeCount);
	}
		
	public String use (User caller, User subject) {
	    return "X-Ray";
	}
}

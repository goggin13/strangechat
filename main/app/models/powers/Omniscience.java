package models.powers;
import models.User;

public class Omniscience extends IntervalPower {
	
	public Omniscience () {
		super(
		    "Omniscience",
			"http://lh6.ggpht.com/yj-E7lODrXPhoguX0ojc91zR90_BE7Bcv2slyH-df57i2VIcSkzuAsNzNTPWdC2yz6J-SzTLM8heA7c",
			"Receive notifications whenever the other user uses super powers",
			false,
			true
		);
		this.award_interval = 100;
	}
		
	@Override
	public Long getFieldValue (User user) {	    
	    return user.chatTime;
	}
	
	@Override
	public String use (User caller, User subject, String[] params) {
	    return "omniscient";
	}
	
}

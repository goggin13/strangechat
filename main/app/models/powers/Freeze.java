package models.powers;
import models.IntervalPower;
import models.User;

public class Freeze extends IntervalPower {
	
	public Freeze () {
		super(
		    "Freeze",
			"",
			"Disable the other users typing",
			false,
			false
		);
		this.award_interval = 120;
	}

	public Long getFieldValue (User user) {	    
	    return user.chatTime;
	}
	
}

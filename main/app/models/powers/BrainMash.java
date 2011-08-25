package models.powers;
import models.User;

public class BrainMash extends IntervalPower {
	
	public BrainMash () {
		super(
		    "Brain Mash",
			"",
			"Render the other user senseless",
			false,
			false
		);
		this.award_interval = 120;
	}
		
	public Long getFieldValue (User user) {	    
	    return user.chatTime;
	}
	
}

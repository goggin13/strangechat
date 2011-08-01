package models.powers;
import java.util.*;
import enums.Power;
import models.*;

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

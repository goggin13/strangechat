package models.powers;
import models.User;

public class GoldCoin extends IntervalPower {
	
	public GoldCoin () {
		super(
		    "Gold Coin",
			"",
			"Accumulate wealth to spread to your fellow chatters",
			false,
			true
		);
		this.award_interval = 100;
	}
		
	public Long getFieldValue (User user) {	    
	    return user.chatTime;
	}
	
	public String use (User caller, User subject) {
	    return "goldcoin";
	}
	
}

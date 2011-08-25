package models.powers;

import models.User;

public abstract class IntervalPower extends SuperPower {
    public int award_interval = 120;
	    
	public IntervalPower (String n, String im, String d, boolean i, boolean o) {
	    super(n, im, d, i, o);
	}
	
	public abstract Long getFieldValue (User user);
	    
	public int isQualified (User user) {	    
        if (countAvailablePowers(user) >= 100) {
            return 0;
        }
        int count = countPowers(user);
        long current = getFieldValue(user);
        long used = count * this.award_interval;
        long availableSeconds = current - used;
        return (availableSeconds >= this.award_interval) ? 1 : 0;
	}
	
}
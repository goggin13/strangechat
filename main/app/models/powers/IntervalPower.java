package models.powers;

import models.User;

public abstract class IntervalPower extends SuperPower {
    public int award_interval = 120;
	    
	public IntervalPower (String n, String im, String d, boolean i, boolean o) {
	    super(n, im, d, i, o);
	}
	
	public abstract Long getFieldValue (User user);
	    
	@Override
	public int isQualified (User user) {	    
        if (countAvailablePowers(user) >= 100) {
            return 0;
        }
        int count = countPowers(user);
        long current = getFieldValue(user);
        long used = count * this.award_interval;
        long availableSeconds = current - used;
        // System.out.println("count = " + count);
        // System.out.println("current = " + current);
        // System.out.println("used = " + used);
        // System.out.println("availableSeconds = " + availableSeconds);
        return (availableSeconds >= this.award_interval) ? 1 : 0;
	}
	
}
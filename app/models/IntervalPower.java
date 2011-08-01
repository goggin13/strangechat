package models;
import java.util.*;
import enums.Power;
import play.Logger;
import models.*;

public abstract class IntervalPower extends SuperPower {
    public int award_interval = 120;
	    
	public IntervalPower (String n, String im, String d, boolean i, boolean o) {
	    super(n, im, d, i, o);
	}
	
	public abstract Long getFieldValue (User user);
	    
	public int isQualified (User user) {	    
	    
	    Power p = this.getPower();
        int count = user.countPowers(p);
        long current = getFieldValue(user);
        long used = count * this.award_interval;
        long available = current - used;
        // System.out.println("--------------");
        // System.out.println(this.name);
        // System.out.println("current : " + current);
        // System.out.println("count : " + count);
        // System.out.println("interval : " + this.award_interval);        
        // System.out.println("used :" + used);
        // System.out.println("available : " + available);
        return (count < 100 && available >= this.award_interval) ? 1 : 0;
	}
	
}
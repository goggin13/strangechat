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
        int count = countPowers(user);
        long current = getFieldValue(user);
        long used = count * this.award_interval;
        long available = current - used;
        return (count < 100 && available >= this.award_interval) ? 1 : 0;
	}
	
}
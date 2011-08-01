package models;
import java.util.*;
import enums.Power;
import play.Logger;
import models.*;

public abstract class IntervalPower extends SuperPower {
    public static int interval = 120;
	    
	public IntervalPower (String n, String im, String d, boolean i, boolean o) {
	    super(n, im, d, i, o);
	}
	
	public abstract Long getFieldValue (User user);
	    
	public int isQualified (User user) {	    
	    Power p = this.getPower();
        int count = user.countPowers(p);
        long available = getFieldValue(user) - (count * interval);
        // System.out.println("--------------");
        // System.out.println(this.name);
        // System.out.println("val : " + getFieldValue(user));
        // System.out.println("count : " + count);
        // System.out.println("available : " + available);
        return count < 100 && available >= interval ? 1 : 0;
	}
	
}
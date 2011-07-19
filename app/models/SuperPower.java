package models;
import java.util.*;
import enums.Power;
import play.test.*;
import play.data.validation.*;

public abstract class SuperPower {
	
	/** Icon for this super power */
	public final String image;
	/** Short description to display to the user */
	public final String description;
	/** True if this power never wears out */
	public final boolean infinite;
	/** Name of this superpower */
	public final String name;	
	
	public SuperPower (String n, String im, String d, boolean i) {
        this.name = n;
		this.image = im;
		this.description = d;
		this.infinite = i;
	}
	
	/**
	* Test whether <code>userStat</code> is qualified to receive 
	* another {@link SuperPower} instance of this type
	* @param user 
	* @return <code>true</code> if userStat has earned another
	*/
	public abstract boolean isQualified (User user);
	
	/**
	* Return the enumerated Power associated with this
	* SuperPower  
	* @return
	**/
	public abstract Power getPower();
	
    /**
    * Test whether <code>userStat</code> has earned another instance
    * of this super power. If they have, add an instance to their 
    * collection of powers;
    * @param userStat 
    * @return <code>true</code> if this user passes the requirements for
    *		  another instance of this power
    */
    public boolean awardIfQualified (User user) {
    	boolean qualified = isQualified(user);
    	if (qualified) {
    	    System.out.println("award to " + user.id);
    		grantTo(user);
    	}
    	return qualified;
    }


    /**
    * Create a {@link SuperPower} instance of this type, and grant it
    * to <code>userStat</code>
    * @param userStat
    */
    public void grantTo (User user) {
    	StoredPower power = new StoredPower(this.getPower(), user);
    	power.save();
    	user.superpowers.add(power);
    	user.save();
    	user.notifyNewPower(power);
    } 
}
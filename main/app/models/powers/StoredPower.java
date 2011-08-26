
package models.powers;
 
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Required;
import play.db.jpa.Model;
import enums.Power;

@Entity
public class StoredPower extends Model {
	
	/**
	 * The type of power this is */
	@Required
	@Enumerated(EnumType.STRING)
	public Power power;	
	
	/** How many have been used */
	public int used;
	
	/** How many are available to use */
	public int available;
	
	/** the level of the current power */
	public int level;

	/* true if this power has been used at this level before */
	public boolean newToLevel;
	
	/** User who has accrued this power */
	@Required
	@ManyToOne
    public User owner;
	
	public StoredPower (Power p, User u) {
		this.power = p;
		this.owner = u;
		this.available = 0;
		this.level = 1;
		this.used = 0;
		this.newToLevel = true;	
		this.save();
	    u.superPowers.add(this);	    
	    u.save();	
	}
	
	public String toString () {
		return power.toString() 
		       + "(" + this.level + ")"
               + " " + this.available + " / " + (this.used + this.available) 
               + " --> " + owner.toString();
	}
	
	public SuperPower getSuperPower () {
	    return this.power.getSuperPower();
	}
	
	/**
	 * Return a new or existing row that matches 
     * @param p the power to match against
     * @param u the user to match against
	 * @return a new record if none matches, or the existing row */
	public static StoredPower getOrCreate (Power p, User u) {
	    StoredPower storedPower = StoredPower.find("byPowerAndOwner", p, u).first();
    	if (storedPower == null) {
    	  storedPower = new StoredPower(p, u);  
    	  storedPower.save(); 
    	}
    	return storedPower;
	}
	
	/**
	 * Incrememnt the count available for this power, and set
	 * the level
	 * @param level */
	public void increment (int level) {
	    this.level = level;
  	    if (this.getSuperPower().infinite) {
  	        this.available = 1;
  	    } else if (this.available < Integer.MAX_VALUE) {
      	    this.available++;
      	}
      	this.save();
	}
	
    public void setLevel (int l) {
        if (l != this.level) {
            this.level = l;
            this.newToLevel = true;
        } 
    }
    
    /**
     * Return true if the Owner of this stored power has some available
     * @return */
    public boolean canUse () {
        return this.available > 0;
    }
    
    /**
     * Use this power, return the string from the super powers use() method
     * @param subject the {@link User} the power is being used on     
     * @return the result of this.getSuperPower().use() */
    public String use (User subject, String[] params) {
        SuperPower superPower = this.getSuperPower();
        if (!superPower.infinite) {
            this.available--;
        }
        this.used++;
        this.newToLevel = false;
        this.save();
        return superPower.use(this.owner, subject, params);
    }
 	
}



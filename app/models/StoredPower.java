
package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import enums.Power;
import play.db.jpa.*;
 
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
	
	/** User who has accrued this power */
	@Required
	@ManyToOne
    public User owner;
	
	public StoredPower (Power p, User u) {
		this.power = p;
		this.owner = u;
		this.available = 1;
		this.used = 0;
	}
	
	public String toString () {
		return power.toString();
	}
	
	public SuperPower getSuperPower () {
	    return this.power.getSuperPower();
	}
	
	/**
	 * increment the available powers of type <code>power</code> for the given user 
	 * @param power
	 * @param user 
	 * @return the storedpower, after its been incremented */
	public static StoredPower incrementPowerForUser(Power power, User user) {
    	StoredPower storedPower = StoredPower.find("byPowerAndOwner", power, user).first();
    	if (storedPower == null) {
    	  storedPower = new StoredPower(power, user);  
    	  user.superPowers.add(storedPower);
    	  storedPower.save();  
    	  user.save();
    	} else {
    	  storedPower.available++; 
    	  storedPower.save();  
    	}
    	return storedPower;
    }
    
    /**
     * Return true if the Owner of this stored power has some available
     * @return */
    public boolean canUse () {
        return this.owner.countPowers(this.power, 1) > 0;
    }
    
    /**
     * Use this power, return the string from the super powers use() method
     * @return the result of this.getSuperPower().use() */
    public String use () {
        SuperPower superPower = this.getSuperPower();
        if (!superPower.infinite) {
            this.available--;
        }
        this.used++;
        this.save();
        return superPower.use();
    }
    
	/**
	 * decrement the available powers of type <code>power</code> for the given user 
	 * @param power
	 * @param user */
	public static void usePowerForUser(Power power, User user) {
    	StoredPower storedPower = StoredPower.find("byPowerAndOwner", power, user).first();
    	if (storedPower != null) {
    	  storedPower.available--;
    	  storedPower.used++;
    	  storedPower.save();            	  
    	}
    }    
	
}



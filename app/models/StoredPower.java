
package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import enums.Power;
import play.db.jpa.*;
import play.Logger;

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
	
	/** User who has accrued this power */
	@Required
	@ManyToOne
    public User owner;
	
	public StoredPower (Power p, User u) {
		this.power = p;
		this.owner = u;
		this.available = 1;
		this.level = 1;
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
	 * @param level the level of the power being awarded
	 * @return the storedpower, after its been incremented */
	public static StoredPower incrementPowerForUser (Power power, User user, int level) {
    	StoredPower storedPower = StoredPower.find("byPowerAndOwner", power, user).first();
    	if (storedPower == null) {
    	  storedPower = new StoredPower(power, user);  
    	  storedPower.level = level;
    	  storedPower.save(); 
    	  user.superPowers.add(storedPower);
    	  user.save();
    	} else {
    	  storedPower.level = level;
    	  if (storedPower.getSuperPower().infinite) {
    	      storedPower.available = 1;
    	  } else {
    	      if (storedPower.available < Integer.MAX_VALUE) {
    	          storedPower.available++;
    	      } 
    	  }    	    
    	  storedPower.save();  
    	}
    	return storedPower;
    }
    
    /**
     * Return true if the Owner of this stored power has some available
     * @return */
    public boolean canUse () {
        return this.available > 0;
    }
    
    /**
     * Use this power, return the string from the super powers use() method
     * @param caller the {@link User} using the power
     * @param subject the {@link User} the power is being used on     
     * @return the result of this.getSuperPower().use() */
    public String use (User caller, User subject) {
        SuperPower superPower = this.getSuperPower();
        if (!superPower.infinite) {
            this.available--;
        }
        this.used++;
        this.save();
        return superPower.use(caller, subject);
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



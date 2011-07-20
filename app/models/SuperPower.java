package models;
import java.util.*;
import enums.Power;
import play.test.*;
import play.data.validation.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.lang.reflect .*;

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
	* @return **/
	public Power getPower () {
	    return Power.valueOf(this.name.replace(' ', '_').toUpperCase());
	}
	
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
        Power p = this.getPower();
        StoredPower sp = StoredPower.incrementPowerForUser(p, user);
    	user.notifyNewPower(sp);
    } 
    
    /**
     * This function returns a string after the power is used;
     * this string will be sent back to the chat clients, so it can
     * be anything, so long as it coordinates with what clients expect.
     * E.G. "used", "fail", etc.  Subclasses superpowers may return any
     * string they wish 
     * @return */
    public String use () {
        return "used";
    }
    
    /** @return JSON representing this object */
    public String toJSON () {
        Gson gson = new GsonBuilder()
    	            .setExclusionStrategies(new User.ChatExclusionStrategy())
    	            .create();		    
        return gson.toJson(this, new TypeToken<SuperPower>() {}.getType());
    }
    
    /** @return deserialized object from string */
    public static SuperPower fromJSON (String superPowerJSON) {
        Gson gson = new GsonBuilder()
                     .setExclusionStrategies(new User.ChatExclusionStrategy())
                     .registerTypeAdapter(SuperPower.class, new UserEvent.SuperPowerDeserializer())
                     .create();
         Type powerType = new TypeToken<SuperPower>() {}.getType();
         SuperPower superPower = gson.fromJson(superPowerJSON, powerType);
         return superPower;
    }
}
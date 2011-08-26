package models.powers;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import models.User;
import models.UserEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import enums.Power;

public abstract class SuperPower {
	
	/** Icon for this super power */
	public final String image;
	/** Short description to display to the user */
	public final String description;
	/** True if this power never wears out */
	public final boolean infinite;
	/** Name of this superpower */
	public final String name;	
	/** true if this power can only be used once per chat */
	public final boolean oncePerChat;
	/** true if this power should be automatically turned on whenever its received */
	public boolean autoOn;
	/** true if this power is applied to multiple rooms at once*/
	public boolean multiRoom;	
	
	public SuperPower (String n, String im, String d, boolean i, boolean o) {
        this.name = n;
		this.image = im;
		this.description = d;
		this.infinite = i;
		this.oncePerChat = o;
		this.autoOn = false;
		this.multiRoom = false;
	}
	
	/**
	* Test whether <code>userStat</code> is qualified to receive 
	* another {@link SuperPower} instance of this type
	* @param user 
	* @return the level of the power they are qualified for, or 0 if unqualified
	*/
	public abstract int isQualified (User user);
	
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
    	int level = isQualified(user);
    	if (level > 0) {
    		grantTo(user, level);
    	}
    	return level > 0;
    }

    /**
    * Create a {@link SuperPower} instance of this type, and grant it
    * to <code>user</code>
    * @param user the user to grant to 
    * @param level the level of the power to award    
    */
    public StoredPower grantTo (User user, int level) {
        Power p = this.getPower();
        StoredPower sp = StoredPower.getOrCreate(p, user);
        sp.increment(level);
        user.notifyNewPower(sp);
        System.out.println("AWARD " + sp.getSuperPower().name + " to " + user.id);
    	return sp;
    } 
    
    /**
     * This function returns a string after the power is used;
     * this string will be sent back to the chat clients, so it can
     * be anything, so long as it coordinates with what clients expect.
     * E.G. "used", "fail", etc.  Subclasses superpowers may return any
     * string they wish 
     * @param caller the {@link User} using the power
     * @param subject the {@link User} the power is being used on
     * @param params optional params to pass to use 
     * @return */
    public String use (User caller, User subject, List<String> params) {
        return "used";
    }
    
    public String use (User caller, User subject) {
    	return use(caller, subject, new LinkedList<String>());
    }
    
    /** count how many powers the given user has of this type */
    public int countPowers (User u) {
        Power p = this.getPower();
        int count = u.countPowers(p);
        return count;
    }

    /** count how many available powers the given user has of this type */
    public int countAvailablePowers (User u) {
        Power p = this.getPower();
        int count = u.countAvailablePowers(p);
        return count;
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
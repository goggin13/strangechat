package models.karma;

import models.Utility;

import com.google.gson.reflect.TypeToken;

public abstract class Reward {
	
	/** name of the reward */
	final public String name;
	
	/** true if this is a good reward */
	final public boolean isGood;
	
	public Reward(String n, boolean g) {
		this.name = n;
		this.isGood = g;
	}
	
	public String toString () {
	    return this.name;
	}
	
	public String toJson () {
		return Utility.toJson(this, new TypeToken<Reward>(){});
	}
	
}

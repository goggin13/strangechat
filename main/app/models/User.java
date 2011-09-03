package models;
 
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import models.karma.KarmaKube;
import models.powers.StoredPower;
import models.powers.SuperPower;
import models.pusher.Pusher;
import play.Play;
import play.db.jpa.Model;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import enums.Power;

/**
 * A chat user in the system, who can be online or off, and the maintained meta
 * data relevant to them
 */

@Entity
public class User extends Model {
    @Transient
	public static long admin_id = -3L;    
	
	/** The user_id, in this case will be the facebook_id */
    public long user_id;
    
	/** the avatar to display for this user */
	public String avatar;

    /** String id of the most recent session we have for this user */ 
    public String session_id;
    
	/** this users alias */
	public String alias;
	
	/** maps to a Pandora bot */
	public String botid;
	
    /** List of other users this user has met with recently */
    // @ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    // @JoinTable(name = "UserToMetWith")
    // public List<User> recentMeetings;    

    /** Collection of the superpowers this user has, including
     *  ones that have already been used */ 
    @OneToMany(cascade=CascadeType.REMOVE)
    public List<StoredPower> superPowers;

    /** list of the SuperPower objects owned by this user
     *  Just for login to tell them what they have */
    @Transient
    public HashMap<Power, SuperPower> superPowerDetails;

    @Transient
    public List<KarmaKube> karmaKubes;
    
	/** set of icebreaker indices seen */
	@ElementCollection  
	public Set<Integer> icebreakers_seen;

    /** number of gold coins this user has available to spend **/
    public int coinCount;

    /** number of coins this user has accrued all time */
    public int coinsEarned;
    
    /** True if this user is currently online */
    public long lastLogin;
	
	/** True if this user is currently online */
	public boolean online;

    /** total seconds chatting */
    public long chatTime;  		

    /** total messages sent */
    public int messageCount;	

    /** total messages received */
    public int gotMessageCount;

    /** total joins by this user */
    public int joinCount;
     
    /** offers to reveal made */
    public int offersMadeCount; 		 

    /** offers to reveal received */
    public int offersReceivedCount;  	 

    /** mutual offers to receive */
    public int revealCount; 			

	public User (long u) {
		this.user_id = u;
		this.online = false;
		this.session_id = "";
		this.chatTime = 0L;
		this.messageCount = 0;
		this.joinCount = 0;
		this.offersMadeCount = 0;
		this.offersReceivedCount = 0;
		this.revealCount = 0;
        coinCount = 0;
        coinsEarned = 0;		
	    this.superPowers = new LinkedList<StoredPower>();
        // this.recentMeetings = new LinkedList<User>();
        this.icebreakers_seen = new CopyOnWriteArraySet<Integer>();
        // this.icebreakers_seen = new TreeSet<Integer>();      
	    this.save();
        addStartUpPowers();
	}
	
	private void addStartUpPowers () {
	    StoredPower sp = new StoredPower(Power.ICE_BREAKER, this);
        sp.level = 1;
        sp.available = 2;
        sp.save();
        sp = new StoredPower(Power.KARMA, this);
        sp.level = 1;
        sp.available = 10;
        sp.save();       
	}
		
	/**
	 * log this user in, and notify any of their friends that
	 * are online that they are available */	
	public UserSession login () {
		this.online = true;
		Random r = new Random();
		this.session_id = Utility.md5(this.avatar + this.alias + System.currentTimeMillis() + r.nextInt());
		this.karmaKubes = KarmaKube.find("byRecipientAndOpenedAndRejected", this, false, false).fetch(1000);
		this.lastLogin = Utility.time();
		this.populateSuperPowerDetails();
		return new UserSession(this, this.session_id);
	}
	
	/**
	 * Iterate stored powers and save all the superpower objects for
	 * sending back to the user on login */
	private void populateSuperPowerDetails () {
	    if (this.superPowerDetails != null) {
	        this.superPowerDetails.clear();
        }
	    this.superPowerDetails = new HashMap<Power, SuperPower>();
	    for (StoredPower sp : this.superPowers) {
	        this.superPowerDetails.put(sp.power, sp.getSuperPower());
	    }
	}
		
	/**
	 * @return string representation of this user
	 */	
	public String toString () {
	    String str;
	    if (!this.alias.equals("")) {
	        str = this.alias 
	              + " (" + this.user_id + ")";
	    } else {
	        str = this.user_id + "";
	    }
		return str;
	}
		
	/**
	 * A random user who's user_id does not match this object
	 * @return a random user */
	public User getRandom () {
		return User.find(
		    "user_id != ? and online = ? order by rand()", this.user_id, true
		).first();
	}
					
    /**
     * Return a list of all the ice breakers this user has seen 
     * @return list of indices of icebreakers */
    public Set<Integer> getSeenIceBreakers () {
        return icebreakers_seen;
    }
    
    /**
     * Mark the given index as seen by this user 
     * @param i the index of the icebreaker to mark as seen */
    public void addSeenIceBreaker (int i) {
        this.icebreakers_seen.add(i);
        // this.save();
    }

    /**
     * @param i
     * @return <code>true</code> if this user has seen the icebreaker at index i */
    public boolean seenIceBreaker (int i) {
        return this.icebreakers_seen.contains(i);
    }

	/**
	* Count how many {@link StoredPower} instances 
	* @param p the {@link Power} to count
	* @param countUsed flag to indicate whether to include used powers;
	*                  0 => include used and unused
	*                  1 => unused only
	*                  2 => used only
	* @return number of powers matching <code>p</code>
	*/
	public int countPowers (Power p, int countUsed) {
	    StoredPower sp = StoredPower.find("byOwnerAndPower", this, p).first();
	    if (sp == null) {
	        return 0;
	    } else if (countUsed == 0) {
			return sp.used + sp.available;
		} else if (countUsed == 1) {
			return sp.available;
		} else {
		    return sp.used;
		}
	}
	
	/**
	 * count how many powers of type p this user has available 
	 * @return */
    public int countAvailablePowers (Power p) {
        return countPowers(p, 1);
    }
    
	/**
	 * count how many powers of type p this user has used 
	 * @return */
    public int countUsedPowers (Power p) {
        return countPowers(p, 2);
    }    

	/**
	 * count how many powers of type p this user has
	 * @return */
    public int countPowers (Power p) {
        return countPowers(p, 0);
    }
    
    /**
     * Add money to this users coin count 
     * @param n number of coins to add */
    public void addCoins (int n) {
        this.coinCount += n;
        this.coinsEarned += n;
        this.save();
    }

    /**
     * Subtract money from this users coin count, not below zero though
     * @param n number of coins to subtract */
    public void subtractCoins (int n) {
        this.coinCount = Math.max(0, this.coinCount - n);
        this.save();
    }
    
    /**
     * This users current level for <code>p</code>
     * @param p the power to check for
     * @return the level of that power for this user, or 0 if they don't have it */
    public int currentLevel (Power p) {
        StoredPower sp = StoredPower.find("byOwnerAndPower", this, p).first();
	    if (sp == null) {
	        return 0;
	    } 
	    return sp.level;
    }

	/**
	 * Send this user a notification that they have recieved a new power
	 * @param power the new power they have received */
	public void notifyNewPower (StoredPower power) {
        UserEvent.NewPower message = new UserEvent.NewPower(this.id, power, "");
        notifyMe("newpower", message.toJson());
	}

	public void notifyMe (String event, String json) {
		new Pusher().trigger(this.getChannelName(), event, json);
	}
	
    /**
     * Check all of the super powers and see if I am eligible for any
     * new ones; if so, assign them to me, and put notification events in
     * the stream */
    public void checkForNewPowers () {
        Power[] powers = Power.values(); 
		for (Power power : powers) {
		    SuperPower superPower = power.getSuperPower();
		    superPower.awardIfQualified(this);
		}
    }
	
	public List<UserSession> getSessions () {
	    return UserSession.find("byUser", this).fetch();
	}
			
	public boolean equals (Object obj) {
	    if (obj == null ||
            !(obj instanceof User)) {
            return false;
        }
        User other = (User)obj;
        return other.id == this.id;
	}	

    public String getChannelName () {
        return this.id + "_channel" + (Play.mode == Play.Mode.DEV ? "-local" : "");
    }
			
	/**
	 * Return a user with id <code>user_id</code>, either an 
	 * existing one, or a newly created user with this id
	 * @param user_id
	 * @return user object of matched or created user
	 */
	public static User getOrCreate (long user_id) {
		User user = User.find("byUser_id", user_id).first();
		if (user == null) {
			user = new User(user_id);
		}
		user.populateSuperPowerDetails();
		return user;
	}
		    
	/** 
	 * This class is used when serializing and deserializing JSON.  Its only 
	 * purpose is to inform the GsonBuilder objects that they should exclude 
	 * the friends field (since it likely will contain a circular reference) */
	public static class ChatExclusionStrategy implements ExclusionStrategy {

		/** 
		 * Required to implement this, but we dont want to skip
		 * any classes */
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		/**
		 * Indicate which fieds to skip; for now, just friends */
		public boolean shouldSkipField(FieldAttributes f) {
		  return f.getName().equals("user_id")
                 || f.getName().equals("owner")
                 || f.getName().equals("recipient")
                 || f.getName().equals("isGood")
                 || f.getName().equals("reward")
                 || f.getName().equals("recentMeetings");
		}
 	}
			
}
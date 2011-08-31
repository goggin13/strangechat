package models.karma;
 
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import models.User;
import models.Utility;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.F.None;
import play.libs.F.Option;
import play.libs.F.Some;

import com.google.gson.reflect.TypeToken;

import enums.KarmaReward;

/**
 * A chat user in the system, who can be online or off, and the maintained meta
 * data relevant to them
 */

@Entity
public class KarmaKube extends Model {
	
	/** the person who originally received this karma kube */
	@Required
	@ManyToOne
	public User sender;			

	/** The person who the cube has been given to */
    @ManyToOne
    public User recipient;

    /** if this is good or bad karma kube */
    public boolean isGood;
    
    /** True if this kube has been opened by the recipient */
    public boolean opened;
    
    /** True if this kube has been rejected by the recipient */
    public boolean rejected;    

    /** The contents of this KarmaKube */
    @Required
    @Enumerated(EnumType.STRING)    
    public KarmaReward reward;
    
	public KarmaKube (User o, User r, boolean isGood) {
        this.sender = o;
        this.opened = false;
        this.recipient = r;
        this.reward = KarmaReward.getRandom(isGood);
        this.isGood = isGood;
        this.rejected = false;
        this.save();
	}
		
	public Reward open () {
		this.opened = true;
		this.save();
		return this.reward.getReward();
	}
	
	public boolean hasBeenSent () {
		return this.getRecipient().isDefined();
	}
	
	public Option<User> getRecipient() {
		if (recipient == null) {
			return new None();
		} else {
			return new Some(recipient);
		}
	}
			
	public String toJson () {
	    return Utility.toJson(this, new TypeToken<KarmaKube>(){}, new User.ChatExclusionStrategy());
	}
	
	/**
	 * Award this cube to the given user; they will receive whatever
	 * reward it contains
	 * @param recipient the user receiving this karma 
	 * @return the reward, assuming it exists */
	public void giftTo (User recipient) {
		this.recipient = recipient;
		this.save();
	}
}
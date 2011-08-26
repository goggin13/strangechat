package models.karma;
 
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import models.User;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.F.None;
import play.libs.F.Option;
import play.libs.F.Some;
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
	public User owner;			

	/** The person who the cube has been given to */
    @ManyToOne
    public User recipient;

    /** True if this kube has been opened by the recipient */
    public boolean opened;

    /** The contents of this KarmaKube */
    @Required
    @Enumerated(EnumType.STRING)    
    public KarmaReward reward;
    
	public KarmaKube (User o) {
        this.owner = o;
        this.opened = false;
        this.recipient = null;
        this.reward = null;
	}
	
	public boolean full () {
		return this.getReward().isDefined();
	}

	public boolean awarded () {
		return this.getRecipient().isDefined();
	}
	
	public Reward open () {
		this.opened = true;
		this.save();
		return this.getReward().get().getReward();
	}
	
	public Option<User> getRecipient() {
		if (recipient == null) {
			return new None();
		} else {
			return new Some(recipient);
		}
	}
	
	public Option<KarmaReward> getReward() {
		if (reward == null) {
			return new None();
		} else {
			return new Some(reward);
		}
	}
	
	/** 
	 * @param isGood dictates whether this is a good or bad karma box
	 * @return */
	public Reward setReward (boolean isGood) {
		this.reward = KarmaReward.getRandom(isGood);
		this.save();
		return this.reward.getReward();
	}
	
	/**
	 * Award this cube to the given user; they will receive whatever
	 * reward it contains
	 * @param recipient the user receiving this karma 
	 * @return the reward, assuming it exists */
	public Option<Reward> giftTo (User recipient) {
		this.recipient = recipient;
		this.save();
		if (this.getReward().isDefined()) {
			Reward r = this.getReward().get().getReward();
			return new Some(r);
		}
		return new None();
	}
	
}
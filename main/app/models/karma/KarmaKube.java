package models.karma;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import models.User;
import models.Utility;

import org.hibernate.exception.LockAcquisitionException;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.F.None;
import play.libs.F.Option;
import play.libs.F.Some;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;

import enums.KarmaReward;

/**
 * A chat user in the system, who can be online or off, and the maintained meta
 * data relevant to them
 */

@Entity
public class KarmaKube extends Model {

	/** the person who originally received this karma kube */
//	@Required
//	@ManyToOne
//	public User sender;

	/** The person who the cube has been given to */
//	@ManyToOne
//	public User recipient;

	public long sender_id;
	public long recipient_id;
	
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
		this.sender_id = o.id;
		this.opened = false;
		this.recipient_id = r.id;
		this.reward = KarmaReward.getRandom(isGood);
		this.isGood = isGood;
		this.rejected = false;
	}

	public User getSender () {
		return User.findById(this.sender_id);
	}
		
	public Reward open() {
		if (!this.getRecipient().isDefined()) {
			return null;
		}
		this.opened = true;
		Reward r = this.reward.getReward();
		User recipient = this.getRecipient().get();
		if (r instanceof GoldCoin) {
			GoldCoin coin = new GoldCoin(); // make a new one to get a new
											// random amount
			int amount = coin.coins;
			recipient.addCoins(amount);
			this.save();
			return coin;
		} else { // if (r instanceof Fart) {
			Fart fart = new Fart();
			int amount = fart.coins;
			recipient.subtractCoins(amount);
			this.save();
			return fart;
		}
	}

    public void reject () {
        this.rejected = true;
        this.save();
    }

	public boolean hasBeenSent() {
		return this.getRecipient().isDefined();
	}

	public Option<User> getRecipient() {
		User user = User.findById(this.recipient_id);
		if (user == null) {
			return new None();
		} else {
			return new Some(user);
		}
	}

	private static class KubeExclusionStrategy implements ExclusionStrategy {

		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		public boolean shouldSkipField(FieldAttributes f) {
			return f.getName().equals("karmaKubes")
					|| f.getName().equals("recentMeetings")
					|| f.getName().equals("owner");
		}
	}

	public String toJson() {
		return Utility.toJson(this, new TypeToken<KarmaKube>() {
		}, new KubeExclusionStrategy());
	}

	/**
	 * Award this cube to the given user; they will receive whatever reward it
	 * contains
	 * 
	 * @param recipient
	 *            the user receiving this karma
	 * @return the reward, assuming it exists
	 */
	public void giftTo(User recipient) {
		this.recipient_id = recipient.id;
		this.save();
	}

}
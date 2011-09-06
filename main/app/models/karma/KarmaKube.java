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

	public KarmaKube(User o, User r, boolean isGood) {
		this.sender = o;
		this.opened = false;
		this.recipient = r;
		this.reward = KarmaReward.getRandom(isGood);
		this.isGood = isGood;
		this.rejected = false;
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

	public boolean hasBeenSent() {
		return this.getRecipient().isDefined();
	}

	public Option<User> getRecipient() {
		if (recipient == null) {
			return new None();
		} else {
			return new Some(recipient);
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
		this.recipient = recipient;
		this.save();
	}

	@Override
	public KarmaKube save() {
		try {
			super.save();
		} catch (LockAcquisitionException e) {
			Logger.error("DEADLOCK1 (%s) ", e.getMessage());
			throw(e);
		} catch (play.exceptions.JavaExecutionException e) {
			Logger.error("DEADLOCK2 (%s, %s, %s) ",
					e.getMessage(), e.getErrorDescription(),
					e.getErrorTitle());
			throw(e);
		} catch (javax.persistence.PersistenceException e) {
			Logger.error("DEADLOCK3 (%s) ", e.getMessage());
			throw (e);
		}

		return this;
	}
}
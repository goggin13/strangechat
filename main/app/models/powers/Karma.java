package models.powers;

import models.User;
import models.karma.KarmaKube;
import play.Logger;

public class Karma extends IntervalPower {

	public Karma() {
		super("Karma", "", "Accrue karma to reward or punish others", false, false);
		this.award_interval = 120;
	}

	@Override
	public String use (User caller, User subject, String[] params) {
		if (caller == null || subject == null || params.length == 0) {
			Logger.error("Karma requires both a caller and subject and params[0]");
			return "";
		}
		boolean isGood = params[0].equals("1");
		KarmaKube kube = KarmaKube.find("byOwner", caller).first();
		kube.setReward(isGood);
		kube.giftTo(subject);
		return "KarmaKube-" + kube.id;
	}
	
    @Override
    public StoredPower grantTo (User user, int level) {
    	new KarmaKube(user).save();
    	return super.grantTo(user, level);
    } 
	
	@Override
	public Long getFieldValue(User user) {
		return user.chatTime;
	}

}

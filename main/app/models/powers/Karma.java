package models.powers;

import java.util.List;

import models.User;
import models.karma.KarmaKube;
import play.Logger;
 
public class Karma extends IntervalPower {

	public Karma() {
		super("Karma", "", "Accrue karma to reward or punish others", false, false);
		this.award_interval = 400;
	}

	@Override
	public String use (User caller, User subject, List<String> params) {
		if (caller == null || subject == null || params == null || params.size() == 0) {
			Logger.error("Karma requires both a caller and subject and params[0]");
			return "";
		}
		boolean isGood = params.get(0).equals("1");
		KarmaKube kube = new KarmaKube(caller, subject, isGood);
		subject.notifyMe("karma", kube.toJson());
		return "KarmaKube-" + kube.id;
	}
	
	@Override
	public Long getFieldValue(User user) {
		return user.chatTime;
	}

}

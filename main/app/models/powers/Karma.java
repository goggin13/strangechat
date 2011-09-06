package models.powers;

import java.util.List;

import models.User;
import models.karma.KarmaKube;

import org.hibernate.exception.LockAcquisitionException;

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
		
		boolean success = false;
		int tries = 0;
		KarmaKube kube = null;
		while (!success && tries++ < 5) {
			try {
				kube = new KarmaKube(caller, subject, isGood);
				kube.save();
				success = true;
			} catch (LockAcquisitionException e) {
				Logger.error("CAUGHT 1 (%s)", tries);
				pause(500);
			} catch (play.exceptions.JavaExecutionException e) {
				Logger.error("CAUGHT2 (%s)", tries);
				pause(500);
			} catch (javax.persistence.PersistenceException e) {
				Logger.error("CAUGHT3 (%s)", tries);
				pause(500);
			}
		}
		
		if (kube != null) {
			subject.notifyMe("karma", kube.toJson());
			return "KarmaKube-" + kube.id;
		} else {
			return "failed";
		}

	}
	
	private void pause (long secs) {
		try {
			Thread.sleep(secs);
		} catch (InterruptedException e) {
			
		}
	}
	
	@Override
	public Long getFieldValue(User user) {
		return user.chatTime;
	}

}

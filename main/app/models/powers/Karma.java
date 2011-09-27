package models.powers;

import java.util.List;

import models.User;
import models.Utility;
import models.karma.KarmaKube;

import org.hibernate.exception.LockAcquisitionException;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
 
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
		
		KarmaKube kube = null;
		
		boolean success = false;
		int tries = 0;
		while (!success && tries++ < 5) {
			try {
				kube = new KarmaKube(caller, subject, isGood);
				kube.save();
				success = true;
			} catch (javax.persistence.PersistenceException e) {
				JPA.em().clear();
				Logger.warn("Karma.java Caught PersistenceException : %s", e.getMessage());
				Utility.pause(250);
			}
		}
        if (success) {
        	if (tries > 1) {
        		Logger.error("Karma Kube saved in %s tries", tries);
        	}
        } else {
        	Logger.error("Failed to save in %s tries", tries);
        }
        
		if (kube != null) {
			subject.notifyMe("karma", kube.toJson());
			return "KarmaKube-" + kube.id;
		} else {
			return "";
		}
		
		
	}
	
	@Override
	public Long getFieldValue(User user) {
		return user.chatTime;
	}

}

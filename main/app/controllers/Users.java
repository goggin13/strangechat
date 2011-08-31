package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.BlackList;
import models.User;
import models.UserEvent;
import models.UserSession;
import models.karma.KarmaKube;
import models.karma.Reward;
import models.powers.StoredPower;
import models.powers.SuperPower;
import play.data.validation.Required;

import com.google.gson.reflect.TypeToken;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Users extends Index {
	    				
	/**
	 * This function signs in a user who has not linked their facebook account.  
	 * @param user_id the user_id field of the user signing in
	 * @param avatar optional, url of an avatar to display for this user
	 * @param alias optional, an alias for this user */
	public static void signin (long sign_in_id, String avatar, String alias) {
	    User user = User.getOrCreate(sign_in_id);
        if (BlackList.isBlacklisted(user)) {
		    returnFailed("You have been blacklisted; <br/> If you think this is an error, please contact info@bnter.com");
		}
		System.out.println("Logging in " + user.id);
        Users.renderJSONP(
            getYourData(user, avatar, alias), 
            new TypeToken<HashMap<String, User>>() {}.getType()
        );   
	}
	
	private static HashMap<String, User> getYourData (
	            User user,
	            String avatar,
	            String alias) 
	{	    
		user.avatar = avatar == null ? "" : avatar;
		user.alias = alias == null ? "" : alias;
		user.login();
		user.save();		
		
		HashMap<String, User> data = new HashMap<String, User>();
		data.put(user.user_id + "", user);
		
		return data;	    
	}
	
	/**
	 * Mark this user as offline */
	public static void signout () {
	    UserSession sess = currentSession();
		if (sess != null) {
		    sess.logout();
		    sess.delete();
			returnOkay();
		} else {
		    returnFailed("No valid user, session data passed (user_id, session)");
		}
	}
	
	public static void openKube (@Required long kube_id) {
	   KarmaKube kube = KarmaKube.findById(kube_id);
	   UserSession sess = currentSession();
       if (validation.hasErrors()) {
           returnFailed(validation.errors());
       } else if (sess == null) {
           returnFailed("valid user and session required to use a karma kube");
       } else if (kube == null) {
	       returnFailed(kube_id + " does not reference a valid kube");
	   } else if (kube.opened) {
		   returnFailed("This kube's already been opened!");
	   } else if (!kube.hasBeenSent() || !kube.getRecipient().get().equals(sess.user)) {
	       returnFailed("This kube does not belong to you");
	   }
       
       Reward reward = kube.open();
	   HashMap<String, String> msg = new HashMap<String, String>();
	   msg.put("reward", reward.toJson());
	   returnOkay(msg);
	}

	public static void rejectKube (@Required long kube_id) {
	   KarmaKube kube = KarmaKube.findById(kube_id);
	   UserSession sess = currentSession();
       if (validation.hasErrors()) {
           returnFailed(validation.errors());
       } else if (sess == null) {
           returnFailed("valid user and session required to use a karma kube");
		   returnFailed("This kube's already been opened!");
	   } else if (!kube.hasBeenSent() || !kube.getRecipient().get().equals(sess.user)) {
	       returnFailed("This kube does not belong to you");
	   }
       kube.rejected = true;
       kube.save();
	   returnOkay();
	}
	
	/**
	 * Use the given power, and notify the relevant users.
	 * @param power_id the id of a {@link StoredPower} to use
	 * @param channel, the channel this power is being used in 
	 * @param params optional, params to pass to SuperPower.use */
	public static void usePower (@Required long power_id, @Required String channel, List<String> params) { 
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    UserSession user = currentSession();
	    UserSession other = currentForSession();
	    
	    if (user == null) {
	        returnFailed("valid session is required");
	    }
	    
        StoredPower storedPower = StoredPower.findById(power_id);
        if (storedPower == null) {
            returnFailed("no power by that ID exists");
        } else if (!storedPower.canUse()) {
            returnFailed("Use Power : You don't have any of that power remaining!");
        }

        SuperPower sp = storedPower.getSuperPower();
        if (params == null) {
            params = new LinkedList<String>();
        }
        String result = storedPower.use(other != null ? other.user : null, params);
        
        UserEvent.UsedPower message;
        if (other == null) {
            message = new UserEvent.UsedPower(user.user.id, 0L, sp, storedPower.level, result);
        } else {
            message = new UserEvent.UsedPower(user.user.id, other.user.id, sp, storedPower.level, result);
        }
        
	    Notify.push(channel, "usedpower", message.toJson(), null, callback());
	}
}
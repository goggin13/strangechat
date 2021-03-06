package controllers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import models.BlackList;
import models.User;
import models.UserEvent;
import models.UserSession;
import models.Utility;
import models.karma.KarmaKube;
import models.karma.Reward;
import models.powers.StoredPower;
import models.powers.SuperPower;
import models.pusher.Pusher;
import play.data.validation.Required;

import com.google.gson.reflect.TypeToken;

import play.Logger;

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
		commitCurrentTx();
		
		HashMap<String, User> data = new HashMap<String, User>();
		data.put(user.user_id + "", user);
		
		return data;	    
	}
	
	public static void met_with (@Required long other_id) {
	    Long user_id = currentUser_id();
	    User user = null;
	    if (user_id != null) {
	        user = User.findById(user_id);
	    }		
        if (validation.hasErrors()) {
           returnFailed(validation.errors());
        } else if (user == null) {
           returnFailed("valid user_id and user required to set met with");
        } 
        user.addToRecentMeetings(other_id);
        returnOkay();
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
	
	/** report scores from a trivia round and reward them */
	public static void reportRound (@Required long total, @Required long correct) {
	    Long user_id = currentUser_id();
	    User user = null;
	    if (user_id != null) {
	        user = User.findById(user_id);
	    }
        if (validation.hasErrors()) {
           returnFailed(validation.errors());
        } else if (user == null) {
           returnFailed("user_id is required and must map to a valid user");
        }
        double pct = correct / (float)total;
        user.awardTriviaCoins(pct);
        commitCurrentTx();
        returnOkay();
	}
	
	public static void spendCoins (@Required long amount) {
	    UserSession sess = currentSession();
        if (validation.hasErrors()) {
           returnFailed(validation.errors());
        } else if (sess == null) {
           returnFailed("valid user and session required to use a karma kube");
        } else if (sess.user.coinCount < amount) {
           returnFailed("You have less than " + amount + " available");
        }
        
        sess.user.coinCount -= amount;
        sess.user.save();
        commitCurrentTx();
        returnOkay();
	}
	
	public static void openKube (@Required long kube_id, @Required String channel) {
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
       commitCurrentTx();
	   HashMap<String, String> msg = new HashMap<String, String>();
	   msg.put("reward", reward.toJson());
	   msg.put("kube", kube.toJson());
	   String json = Utility.toJson(msg, new TypeToken<HashMap<String, String>>(){});
       new Pusher().trigger(channel, "openedkube", json);
	   returnOkay();
	}

	public static void rejectKube (@Required long kube_id, @Required String channel) {
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
       kube.reject();
       commitCurrentTx();
       new Pusher().trigger(channel, "rejectedkube", kube.toJson());
	   returnOkay();
	}
	
	/**
	 * consume and delete user identified by consume_user_id, giving all their
	 * powers and stats to current user 
	 * @param consume_id */
	public static void consume (@Required Long consume_user_id) {
	    UserSession sess = currentSession();
	    User consume = User.find("byUser_id", consume_user_id).first();
	    
	    System.out.println("search for user with user_id = " + consume_user_id);
	    
	    if (validation.hasErrors()) {
            returnFailed(validation.errors());
        } else if (sess == null) {
            returnFailed("valid user and session required to use a karma kube");
        } else if (consume == null) {
            returnFailed("consume id does not map to a valid user");
        }
        sess.user.consume(consume);
        Logger.info("Consumed " + consume.id);
        returnOkay();
	}
	
	/**
	 * Use the given power, and notify the relevant users.
	 * @param power_id the id of a {@link StoredPower} to use
	 * @param channel, the channel this power is being used in 
	 * @param params optional, params to pass to SuperPower.use */
	public static synchronized void usePower (@Required long power_id, @Required String channel, List<String> params) { 
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
            returnFailed("Use Power (" + storedPower.power.toString() + "): You don't have any of that power remaining!");
        }

        SuperPower sp = storedPower.getSuperPower();
        if (params == null) {
            params = new LinkedList<String>();
        }
        String result;
        if (other == null) {
            result = storedPower.use(null, params);
        } else {
            result = storedPower.use(other.user, params);
            // other.user.save();
        }
        // user.user.save();
        commitCurrentTx();
        
        UserEvent.UsedPower message;
        if (other == null) {
            message = new UserEvent.UsedPower(user.user.id, 0L, sp, storedPower.level, result);
        } else {
            message = new UserEvent.UsedPower(user.user.id, other.user.id, sp, storedPower.level, result);
        }
        
	    new Pusher().trigger(channel, "usedpower", message.toJson());
	    if (result.length() == 0) {
	       returnFailed("No result from use power (" + storedPower.power +")"); 
	    } else {
	       returnOkay();
	    }
	    
	}
}
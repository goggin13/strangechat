package models;
 
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.Logger;
import play.db.jpa.Model;

/**
 * A user session is an abstraction for a browser window that the user has open; 
 * Every time a user logs in (refreshes or lands on a chat page), they begin a new
 * UserSession.
 */
@Entity
public class UserSession extends Model {
    
	/** The user_id, in this case will be the facebook_id */
	@ManyToOne
    public User user;
    
    /** Session key for this session */
    public String session;
    
    /** socket_id for pusher */
    public String socket;
    
    public UserSession (User u, String s) {
        this.user = u;
        this.session = s;
        this.socket = "";
        this.save();
    }
    
    public boolean equals (Object obj) {
        if (obj == null || 
            !(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession)obj;
        return other.user.equals(user) && other.session.equals(session);
    }

	/**
	 * log this user out, and notify any of their friends that
	 * are online that they are no longer available */	
	public void logout () {
	    Logger.info(this.user.id + " logging out");
		this.save();
	}
			 
   public String toString () {
       return this.user.id + " (" + this.session + ")";
   }	
      
   public UserSession.Faux toFaux () {
	   return new Faux(this.user.id, this.session);
   }
    
   public static UserSession getFor (long user_id, String session) {
       return UserSession.find("byUser_idAndSession", user_id, session).first();
   }
   
   public static class Faux {
       final public long user_id;
       final public String session;
       public Faux (long u, String s) {
           this.user_id = u;
           this.session = s;
       }
       
       public UserSession toReal () {
           UserSession sess = UserSession.find("byUser_idAndSession", user_id, session).first();
           if (sess == null) {
               Logger.error("Called toReal() on UserSession.Faux which doesnt map to real object");
           }
    	   return sess;
       }
       
       public String toString () {
           return "(" + user_id + ", " + session + ")";
       }
   }
}
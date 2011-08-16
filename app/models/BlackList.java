package models;
 
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

/**
 * List of users who are not allowed back in the system
 */
@Entity
public class BlackList extends Model {
    
	@OneToOne	 
    public User user;
    
    public BlackList (User u) {
        this.user = u;
        List<UserSession> sessions = user.getSessions();
        for (UserSession session : sessions) {
            session.logout();
        }
        u.save();
    }
    
    public static List<BlackList> getBlackList () {
        List<BlackList> users = BlackList.all().fetch(1000);
        return users;
    }
    
    public static boolean isBlacklisted (User u) {
        List<BlackList> users = getBlackList();
        for (BlackList b : users) {        
            if (b.user.user_id == u.user_id) {
                return true;
            }
        }
        return false;
    }
    
    public String toString () {
        return this.user.toString();
    }
}
    
    
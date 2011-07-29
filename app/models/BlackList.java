package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import play.libs.F.*;
import play.db.jpa.*;
import com.google.gson.*;
import play.libs.WS;
import play.*;
import play.mvc.*;
import java.lang.reflect.Modifier;
import java.lang.reflect .*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import controllers.*;
import enums.Power;
import models.powers.*;

/**
 * List of users who are not allowed back in the system
 */
@Entity
public class BlackList extends Model {
    
	@OneToOne	 
    public User user;
    
    public BlackList (User u) {
        this.user = u;
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
    
    
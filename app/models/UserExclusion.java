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
 * These objects represent groups of users who are not allowed to chat with one another.
 * Any users who share >= 1 group_id in UserExclusions should never be allowed to meet
 */
@Entity
public class UserExclusion extends Model {
    
	@OneToOne	 
    public User user;
    
    public long group_id;
    
    public UserExclusion (User u, long group) {
        this.user = u;
        this.group_id = group;
        this.save();
    }
    
    public static Set<Long> userGroups (long user_id) {
        List<UserExclusion> userExclusions = UserExclusion.find("byUser_id", user_id).fetch(1000);
        Set<Long> groups = new HashSet<Long>();
        for (UserExclusion u : userExclusions) {
            groups.add(u.group_id);
        }
        return groups;
    }
    
    public static boolean canSpeak (long u1, long u2) {
        Set<Long> g1 = userGroups(u1);
        Set<Long> g2 = userGroups(u2);
        g1.retainAll(g2);
        return g1.size() == 0;
    }
    
    public String toString () {
        return this.user.toString();
    }
}
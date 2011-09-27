package models;
 
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Query;

import play.db.jpa.JPA;
import play.db.jpa.Model;

/**
 * These objects represent groups of users who are not allowed to chat with one another.
 * Any users who share >= 1 group_id in UserExclusions should never be allowed to meet */
@Entity
public class UserExclusion extends Model {
    
    public long user_id;
    
    public long group_id;
    
    public UserExclusion (long user_id, long group) {
    	this.user_id = user_id;
        this.group_id = group;
        this.save();
    }
    
    public UserExclusion (User u, long group) {
        new UserExclusion(u.id, group);
    }
    
    public static Set<Long> userGroups (long user_id) {
        List<UserExclusion> userExclusions = UserExclusion.find("byUser_id", user_id).fetch(1000);
        Set<Long> groups = new HashSet<Long>();
        for (UserExclusion u : userExclusions) {
            groups.add(u.group_id);
        }
        return groups;
    }
    
    public static List<Long> excludedList (long user_id) {
    	String sql = "select distinct user_id " +
        		     "from UserExclusion " +
        		     "where group_id in (select group_id " +
        		     					"from UserExclusion " +
        		     					"where user_id = " + user_id +") " +
        		     "and user_id != " + user_id;
        return JPA.em().createQuery(sql).getResultList();
    }
    
    public String toString () {
        return this.group_id + " : " + this.user_id;
    }
}
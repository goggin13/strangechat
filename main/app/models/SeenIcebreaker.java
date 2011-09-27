package models;

import java.util.List;

import javax.persistence.Entity;

import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public class SeenIcebreaker extends Model {
	
	/** user_id of the user that has seen this */
	public long user_id;
	
	/** index of the seen ice breaker */
	public int ice_index;
	
	public SeenIcebreaker (long user_id, int index) {
		this.user_id = user_id;
		this.ice_index = index;
		this.save();
	}
	
	public static List<Integer> getMySeen (long user_id) {
    	String sql = "select distinct ice_index " +
   		     		 "from SeenIcebreaker " +
   		     		 "where user_id = " + user_id;
    	return JPA.em().createQuery(sql).getResultList();		
	}
	
}

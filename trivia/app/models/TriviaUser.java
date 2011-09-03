package models;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class TriviaUser extends Model{
	
	public long user_id;
	
	public TriviaUser (long u) {
		user_id = u;
		save();
	}

	public Batch getBatch (Category c, int size) {
	    List<Result> results = Result.find("byUser", this).fetch();
		return new Batch(this, c, size, results);
	}
	
	public static TriviaUser getOrCreate(long user_id) {
		TriviaUser u = TriviaUser.find("byUser_id", user_id).first();
		if (u == null) {
			u = new TriviaUser(user_id);
 		}
		return u;
	}

}
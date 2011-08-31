package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.Logger;
import play.db.jpa.Model;

/** 
 * The answer to a trivia question */
@Entity
public class Result extends Model {
    
    /** User this result is pertinent to */
	@ManyToOne
    public TriviaUser user;
    
    /** the trivia question this is relevant to */
    @ManyToOne
    public Question question;
    
    /** number of attempts */
    public int attempts;
    
    /** number of successful attempts*/
    public int successful;
    
    public Result (TriviaUser u, Question q) {
        user = u;
        question = q;
        attempts = 0;
        successful = 0;
        save();
    }
    
    public static void markAttemptForUser (TriviaUser user, Question q, boolean success) {
    	Result r = getOrCreate(user, q);
    	r.attempts++;
    	if (success) {
    		r.successful++;
    	}
    	r.save();
    }
    
    public static Result getOrCreate (long user_id, long qid) {
    	Question q = Question.findById(qid);
    	if (q == null) {
    		Logger.error("requested qid = ?, but no question exists", qid);
    		return null;
    	}
    	TriviaUser u = TriviaUser.getOrCreate(user_id);
    	return getOrCreate(u, q);
    }
    
    public static Result getOrCreate(TriviaUser u, Question q) {
    	Result r = Result.find("byUserAndQuestion", u, q).first();
    	if (r == null) {
    		r = new Result(u, q);
    	}
    	return r;
    }
    
}
package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import play.Logger;
import play.db.DB;
import play.db.jpa.Model;

/** 
 * The answer to a trivia question */
@Entity
public class Category extends Model {
    
    /** The text of the question */
    public String name;
    
    @Transient
    private static String baseSql = "select count(*) as count " +
            "from Result r " +
            "inner join Question q on " +
              "q.id = r.question_id " +
            "inner join Category c on " +
              "c.id = q.category_id " +
            "where c.id = ";
    
    private Category (String n) {
        name = n;
        this.save();
    }
    
    public static Category getOrCreate (String n) {
    	Category c = Category.find("byName", n).first();
    	if (c == null) {
    		c = new Category(n);
    	} else {
    	}
    	return c;
    }
    
    private long longFromSql (String sql) {
		ResultSet results = DB.executeQuery(sql);
		try {
		    results.next();
			return results.getLong("count");
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			return 0;
		}
    }
    
    public long totalQuestions () {
    	return Question.count("byCategory", this);
    }
    
    public long attemptsByMe (TriviaUser user) {
    	return longFromSql(baseSql + this.id + " " +
                " and r.user_id = " + user.id);
    }
    
    public long answeredByMe (TriviaUser user) {
    	return longFromSql(baseSql + this.id + " " +
                " and r.user_id = " + user.id + " " +
                " and r.successful = " + 1);        
    }    

    public String toString () {
        return this.name;
    }

    public static void clearQuestionsAndResponses (Category c) {
        List<Question> questions = Question.find("byCategory", c).fetch();
        for (Question q : questions) {
            q.delete();
        }
        List<TriviaResponse> responses = TriviaResponse.find("byCategory", c).fetch();
        for (TriviaResponse r : responses) {
            r.delete();
        }        
    }
    
}
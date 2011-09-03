package models;

import java.sql.ResultSet;
import java.sql.SQLException;

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
              "q.id = R.question_id " +
            "inner join Category c on " +
              "c.id = Q.category_id " +
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
    	System.out.println(sql);
		ResultSet results = DB.executeQuery(sql);
		try {
		    results.next();
			return results.getLong("count");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Logger.error(e.getMessage());
			return 0;
		}
    }
    
    public long totalQuestions () {
    	return Question.count("byCategory", this);
    }
    
    public long attemptsByMe (TriviaUser user) {
        System.out.println("attempts by me");
    	return longFromSql(baseSql + this.id + " " +
                " and R.user_id = " + user.id);
    }
    
    public long answeredByMe (TriviaUser user) {
        System.out.println("answered by me");        
    	return longFromSql(baseSql + this.id + " " +
                " and R.user_id = " + user.id + " " +
                " and R.successful = " + 1);        
    }    
}
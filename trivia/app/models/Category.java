package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** 
 * The answer to a trivia question */
@Entity
public class Category extends Model {
    
    /** The text of the question */
    public String name;
    
    private Category (String n) {
        name = n;
        save();
    }
    
    public static Category getOrCreate (String n) {
    	Category c = Category.find("byName", n).first();
    	if (c == null) {
    		c = new Category(n);
    	}
    	return c;
    }

}
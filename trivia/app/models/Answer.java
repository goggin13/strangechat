package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

/** 
 * The answer to a trivia question */
@Entity
public class Answer extends Model {
    
    /** The text of the question */
    public String text;
    
    /** true if this is a correct answer */
    public boolean isCorrect;
    
    public Answer (String t, boolean c) {
        text = t;
        isCorrect = c;
        save();
    }
    
    public Answer (String t) {
        new Answer(t, false);
    }

}
package models;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

/** 
 * A trivia question, and answers */
@Entity
public class Question extends Model {
    
    /** The text of the question */
    public String text;
    
    /** the category the question belongs to, e.g. music */
    @ManyToOne
    public Category category;
    
    /** list of answers */
    @OneToMany
    public List<Answer> answers;
    
    public Question (String q, List<Answer> a, Category c) {
    	text = q;
        answers = a;
        category = c;
        save();
    }
    
    public boolean isCorrect (int i) {
        return answers.get(i).isCorrect;
    }

    public boolean isCorrect (char letter) {
    	return isCorrect(letter - 97); // adjust from ascii value for 'a'
    }

    public Answer getAnswer (long answer_id) {
    	for (Answer a : answers) {
    		if (a.id == answer_id) {
    			return a;
    		}
    	}    	
    	return null;
    }
       
    public void shuffleAnswers () {
    	Collections.shuffle(this.answers);
    }
    
    @Override
    public Question delete () {
        List<Result> results = Result.find("byQuestion", this).fetch();
        for (Result r : results) {
            r.delete();
        }
        return super.delete();
    }
    
    public String toString () {
        return this.category.name + " : " + this.text;
    }
    
    public static List<Question> getByCategory (String n) {
    	Category c = Category.getOrCreate(n);
    	return Question.find("byCategory", c).fetch();
    }
    
    public static List<Question> getRandomFrom (Category c, int s, List<Long> seen) {
    	if (seen.size() == 0) {
    		seen.add(-1L); // just to format the query okay, should have no affect
    	}
    	return Question.find(
    				    "select q " +
    				    "from Question q " +
    				    "where category = ? " +
    				    " and q.id not in " + seen.toString().replace('[', '(').replace(']', ')') +
    				    " order by rand()", c
    				)
    			.fetch(s);

    }
    
}
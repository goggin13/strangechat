package models;

import java.util.LinkedList;
import java.util.List;

import play.db.jpa.Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.ResponseType;

public class Batch extends Model{

	public List<Question> questions;
	
	public List<TriviaResponse> repeatResponses;
	public List<TriviaResponse> incorrectResponses;
	public List<TriviaResponse> correctResponses;
	public List<TriviaResponse> salutationResponses;
	
	public final long totalInCategory;
	public final long totalAttempted;
	public final long totalCorrect;
			
	public Category category;
	
	public Batch (TriviaUser user, Category c, int size, List<Result> seen) {
		this.category = c;
		List<Long> seenIDs = new LinkedList<Long>();
		for (Result r : seen) {
			seenIDs.add(r.question.id);
		}
		
		this.totalInCategory = Question.count("byCategory", c);
		this.totalAttempted = c.attemptsByMe(user);
		this.totalCorrect = c.answeredByMe(user);
		
		this.questions = Question.getRandomFrom(c, size, seenIDs);
		this.repeatResponses = TriviaResponse.getBy(c, ResponseType.REPEAT);
		this.incorrectResponses = TriviaResponse.getBy(c, ResponseType.INCORRECT);
		this.correctResponses = TriviaResponse.getBy(c, ResponseType.CORRECT);
		this.salutationResponses = TriviaResponse.getBy(c, ResponseType.SALUTATION);
		for (Question q : this.questions) {
			q.shuffleAnswers();
		}
	}
	
	public String toJson () {
	    return new Gson().toJson(this, new TypeToken<Batch>() {}.getType());
	}
	
}

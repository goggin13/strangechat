package controllers;

import models.Answer;
import models.Batch;
import models.Category;
import models.Question;
import models.Result;
import models.TriviaUser;
import play.data.validation.Required;

public class Trivia extends TriviaIndex{
	
	public static void getBatch (@Required long user_id, @Required String name) {
		if (validation.hasErrors()) {
			returnFailed(validation.errors());
		}
		Category c = Category.find("byName", name).first();
		if (c == null) {
			returnFailed("Category " + name + " does not exist");
		}
		TriviaUser u = TriviaUser.getOrCreate(user_id);
		Batch b = u.getBatch(c, 3);
		returnJson(b.toJson());
	}
	
	public static void answerQuestion (
							@Required long qid, 
							@Required long user_id,
							@Required long aid) 
	{
		if (validation.hasErrors()) {
			returnFailed(validation.errors());
		}
		Question q = Question.findById(qid);
		TriviaUser u = TriviaUser.find("byUser_id", user_id).first();
		Answer answer = q.getAnswer(aid);
		if (q == null || u == null || answer == null) {
			returnFailed("qid and user_id must map to an existing question and user, and aid must be a valid answer");
		}		
		Result.markAttemptForUser(u, q, answer.isCorrect);
		returnOkay();
	}
	
}

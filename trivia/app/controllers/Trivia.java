package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import models.Answer;
import models.Batch;
import models.Category;
import models.Question;
import models.Result;
import models.TriviaUser;
import play.Logger;
import play.data.validation.Required;
import play.db.DB;

public class Trivia extends TriviaIndex{
	private static final int BATCH_SIZE = 10;
	
	public static void getEligibleTrivia (@Required long user_id) {
		if (validation.hasErrors()) {
			returnFailed(validation.errors());
		}		
		TriviaUser tUser = TriviaUser.find("byUser_id", user_id).first();
	    long tUser_id;
		if (tUser == null) {
		    tUser_id = -1;
		} else {
		    tUser_id = tUser.id;
		}
		String sql = "select distinct C.name as name from Category C " +
					 "inner join Question Q on " +
						"Q.category_id = C.id " +
					 "left join Result R on " +
						"R.question_id = Q.id " +
                        "and R.user_id = " + tUser_id + " " + 						
				     "where R.id is null " + 
				     "group by C.name " +
				     "having count(*) >= " + BATCH_SIZE;
		
		ResultSet results = DB.executeQuery(sql);
		try {
			HashMap<String, String> categories = new HashMap<String, String>();
			while (results.next()) {
				String name = results.getString("name");
				categories.put(name, name); 
			}
			returnOkay(categories);
		} catch (SQLException e) {
			Logger.error(e.getMessage());
			returnFailed(e.getMessage());
		} 

	}
	
	public static void getBatch (@Required long user_id, @Required String name) {
		if (validation.hasErrors()) {
			returnFailed(validation.errors());
		}
		Category c = Category.find("byName", name).first();
		if (c == null) {
			returnFailed("Category " + name + " does not exist");
		}
		TriviaUser u = TriviaUser.getOrCreate(user_id);
		Batch b = u.getBatch(c, BATCH_SIZE);
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

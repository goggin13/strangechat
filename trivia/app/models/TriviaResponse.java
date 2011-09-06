package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import enums.ResponseType;

@Entity
public class TriviaResponse extends Model {
	
	/** The category this response belongs to */
	@Required
	@ManyToOne
	public Category category;
	
	/** the text of the response */
	@Required
	public String text;
	
	@Required
	@Enumerated(EnumType.STRING)
	public ResponseType type;

	public TriviaResponse (Category c, String t, ResponseType r) {
		category = c;
		text = t;
		type = r;
		save();
	}
	
	public String toString () {
	    return this.category.name + "(" + this.type + ") : " + this.text;
	}
	
	public static TriviaResponse getRandomRepeatResponse (Category c) {
		return getRandomResponse(c, ResponseType.REPEAT);
	}
	
	public static TriviaResponse getRandomIncorrectResponse (Category c) {
		return getRandomResponse(c, ResponseType.INCORRECT);
	}
	
	public static TriviaResponse getRandomCorrectResponse (Category c) {
		return getRandomResponse(c, ResponseType.CORRECT);
	}
	
	public static TriviaResponse getRandomResponse (Category c, ResponseType t) {
    	return TriviaResponse.find("byCategoryAndType", c, t).first();
	}
	
	public static List<TriviaResponse> getBy (Category c, ResponseType t) {
		return TriviaResponse.find("byCategoryAndType", c, t).fetch();
	}
	
	public String toJson () {
	    return new Gson().toJson(this, new TypeToken<TriviaResponse>() {}.getType());
	}	
}

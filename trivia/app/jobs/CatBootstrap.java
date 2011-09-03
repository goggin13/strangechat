
package jobs;

import java.util.LinkedList;
import java.util.List;

import models.Answer;
import models.Category;
import models.Question;
import models.TriviaResponse;
import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import enums.ResponseType;

@OnApplicationStart
public class CatBootstrap extends Job {

    public void doJob() {
        Category c = Category.getOrCreate("Cats");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;					
        a1 = new Answer("Five days", true);	a2 = new Answer("One month", false);	a3 = new Answer("One week", false);	a4 = new Answer("One dat", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How long does a cat typically stay in heat?", a, c);
        a1 = new Answer("Phyllis", true);	a2 = new Answer("Tara", false);	a3 = new Answer("Bette", false);	a4 = new Answer("Doris", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Felix the cat's girlfriend's name?", a, c);
        a1 = new Answer("Sweet", true);	a2 = new Answer("Savory", false);	a3 = new Answer("Spicy", false);	a4 = new Answer("Salty", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Cats are unable to detect what taste?", a, c);
        a1 = new Answer("4", true);	a2 = new Answer("2", false);	a3 = new Answer("0, they're cats", false);	a4 = new Answer("1", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Of an adult domestic cats 30 teeth, how many are canines?", a, c);
        a1 = new Answer("Bela Lugosi", true);	a2 = new Answer("Lon Chaney, Jr.", false);	a3 = new Answer("Gene Autry", false);	a4 = new Answer("Martin Landau", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What actor starred in The Black Camel, Black Dragons, Black Friday, The Black Sheep, and two movies called The Black Cat?", a, c);
        a1 = new Answer("Si and Am", true);	a2 = new Answer("Please and Yes", false);	a3 = new Answer("You and Me", false);	a4 = new Answer("I and Her", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What are the names of the two Siamese cats in the 1955 Disney movie Lady and the Tramp?", a, c);
        a1 = new Answer("That's all folks!", true);	a2 = new Answer("Thanks for watching!", false);	a3 = new Answer("Here to entertain.", false);	a4 = new Answer("It was a good show.", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What phrase is inscribed on the tombstone of Mel Blanc, the Man of 1,000 voices including Tweetee pie, Sylvester the Cat, Porky Pig, and Bugs Bunny?", a, c);
        a1 = new Answer("Yellow with black markings", true);	a2 = new Answer("Black with yellow markings.", false);	a3 = new Answer("Brown and white markings.", false);	a4 = new Answer("White and black stripes", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What color is an ocelot?", a, c);
        a1 = new Answer("Manx", true);	a2 = new Answer("Calico", false);	a3 = new Answer("Siamese", false);	a4 = new Answer("Persian", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What cat does not have a tail?", a, c);
        a1 = new Answer("18", true);	a2 = new Answer("12", false);	a3 = new Answer("10", false);	a4 = new Answer("20", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("House cats have how many claws in total?", a, c);
        
        new TriviaResponse(c, "Welcome to Cat Trivia.", ResponseType.SALUTATION);

        new TriviaResponse(c, "I don’t understand what you’re trying to say. Type that again?", ResponseType.REPEAT);
        new TriviaResponse(c, "You’re not making sense, try typing your response out again.", ResponseType.REPEAT);
        new TriviaResponse(c, "No time for gibberish, try responding again.", ResponseType.REPEAT);
        new TriviaResponse(c, "It’s so uninteresting to be indecipherable. Try that again?", ResponseType.REPEAT);
        new TriviaResponse(c, "I don’t understand your answer and I won’t respond to it", ResponseType.REPEAT);
        new TriviaResponse(c, "I suppose you think that was terribly clever. However, I don’t understand it. Type that again?", ResponseType.REPEAT);

        new TriviaResponse(c, "You’re rocking it but how long can you keep this up?", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes.", ResponseType.CORRECT);
        new TriviaResponse(c, "Sure.", ResponseType.CORRECT);
        new TriviaResponse(c, "You got it. ", ResponseType.CORRECT);
        new TriviaResponse(c, "One can’t expect you to get everything wrong, I guess.", ResponseType.CORRECT);
        new TriviaResponse(c, "Now you’re picking up what I’m putting down!", ResponseType.CORRECT);
        new TriviaResponse(c, "You’re doing well. I’m surprised.", ResponseType.CORRECT);
        new TriviaResponse(c, "You just might make it through this thing. ", ResponseType.CORRECT);
        new TriviaResponse(c, "I daresay you might be right.", ResponseType.CORRECT);
        new TriviaResponse(c, "I’ll give that one to you. It was an easy one, though.", ResponseType.CORRECT);

        new TriviaResponse(c, "You’re wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Nah.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Nope.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Wrong!", ResponseType.INCORRECT);
        new TriviaResponse(c, "No way, Jose.", ResponseType.INCORRECT);
        new TriviaResponse(c, "I feel kinda bad about how wrong you are.", ResponseType.INCORRECT);
        new TriviaResponse(c, "That’s just wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You’re totally off.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You got that wrong. I’m not surprised.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Wrong. You’ll never make it through this.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You’re done for. Wrong!", ResponseType.INCORRECT);
        new TriviaResponse(c, "I can’t believe you got that one wrong! It was so easy.", ResponseType.INCORRECT);
        
        Logger.info("Bootstrapped cat trivia");
    }
 
}




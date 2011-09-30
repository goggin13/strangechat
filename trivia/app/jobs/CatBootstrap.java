
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

public class CatBootstrap extends Job {

    public void doJob() {
        Category c = Category.getOrCreate("Cats");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;	
        Category.clearQuestionsAndResponses(c);
                				
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
        
        
        new TriviaResponse(c, "Huh? Type that again. I only need the corresponding letter to your answer.", ResponseType.REPEAT);
        new TriviaResponse(c, "I can’t hear you! Try typing only the letter of your answer.", ResponseType.REPEAT);
        new TriviaResponse(c, "You're making no sense, try answering again with only the first letter of the answer.", ResponseType.REPEAT);
        new TriviaResponse(c, "**Yes**", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes.", ResponseType.CORRECT);
        new TriviaResponse(c, "Sure.", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes, lol.", ResponseType.CORRECT);
        new TriviaResponse(c, "FTW!", ResponseType.CORRECT);
        new TriviaResponse(c, "Very nice.", ResponseType.CORRECT);
        new TriviaResponse(c, "Kitty likes.", ResponseType.CORRECT);
        new TriviaResponse(c, "U can has correct answer.", ResponseType.CORRECT);
        new TriviaResponse(c, "Ur such a n00b", ResponseType.CORRECT);
        new TriviaResponse(c, "So right.", ResponseType.CORRECT);
        new TriviaResponse(c, "You got it, dude!", ResponseType.CORRECT);
        new TriviaResponse(c, "YAY, you're right!", ResponseType.CORRECT);
        new TriviaResponse(c, "U can has WRONG answer.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You’re wrong. Deal with it!", ResponseType.INCORRECT);
        new TriviaResponse(c, "**No.**", ResponseType.INCORRECT);
        new TriviaResponse(c, "No, lol.", ResponseType.INCORRECT);
        new TriviaResponse(c, "No.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You're so off!", ResponseType.INCORRECT);
        new TriviaResponse(c, "You stink worse than kitty litter.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Very bad.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Cool story, bro!", ResponseType.INCORRECT);
        new TriviaResponse(c, "So wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You stink.", ResponseType.INCORRECT);
        new TriviaResponse(c, "No, no, no silly!", ResponseType.INCORRECT);
        new TriviaResponse(c, "Ready to rumble?", ResponseType.SALUTATION);
        new TriviaResponse(c, "You wanna go?", ResponseType.SALUTATION);
        new TriviaResponse(c, "Purfect! You're going away now", ResponseType.CLOSING);
        new TriviaResponse(c, "TTFN, loser.", ResponseType.CLOSING);
        
        Logger.info("Bootstrapped cat trivia");
    }
 
}




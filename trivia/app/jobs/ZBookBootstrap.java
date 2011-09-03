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
public class ZBookBootstrap extends Job {

    public void doJob() {
        Category c = Category.getOrCreate("Books");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;					
        a1 = new Answer("Chitty Chitty Bang Bang", true);	a2 = new Answer("The Diamond Smugglers", false);	a3 = new Answer("For Your Eyes Only", false);	a4 = new Answer("Thrilling Cities", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which Ian Fleming novel did the dog Edison appear?", a, c);
        a1 = new Answer("P.G. Wodehouse", true);	a2 = new Answer("Tom Brown", false);	a3 = new Answer("Alfred Tennyson", false);	a4 = new Answer("Agatha Christie", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which writer did Sean O' Casey describe as English Literature's performing flea?", a, c);
        a1 = new Answer("Russian Revolution", true);	a2 = new Answer("Nuclear bombs", false);	a3 = new Answer("Invention of the printing press", false);	a4 = new Answer("World War II", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Ten Days That Shook the World is about what?", a, c);
        a1 = new Answer("Mario Puzo", true);	a2 = new Answer("Vladimir Nabokov", false);	a3 = new Answer("Ken Kesey", false);	a4 = new Answer("Dashiell Hammett", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who completed his novel Omerta shortly before his death?", a, c);
        a1 = new Answer("Casino Royale", true);	a2 = new Answer("Live and Let Die", false);	a3 = new Answer("The Man with the Golden Gun", false);	a4 = new Answer("Dr. No", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Ian Fleming's first novel?", a, c);
        a1 = new Answer("Elizabeth Taylor", true);	a2 = new Answer("Barbara Streisand", false);	a3 = new Answer("Liza Minelli", false);	a4 = new Answer("Judy Garland", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which actress wrote the children's book Nibbles & Me?", a, c);
        a1 = new Answer("e e cummings", true);	a2 = new Answer("Roald Dahl", false);	a3 = new Answer("William Gaddis", false);	a4 = new Answer("Arthur Sullivan", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose autobiographical novel was called The Enormous Room?", a, c);
        a1 = new Answer("Graham Greene", true);	a2 = new Answer("A.S. Byatt", false);	a3 = new Answer("V.S Naipaul", false);	a4 = new Answer("George ORwell", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which British novelist said, \"Fame is a powerful aphrodisiac?\"", a, c);
        a1 = new Answer("Tenderness", true);	a2 = new Answer("Love and Other Mistakes", false);	a3 = new Answer("Dark Paradise", false);	a4 = new Answer("Doom Angel", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was DH Lawrence's Lady Chatterley's Lover originally to have been called?", a, c);
        a1 = new Answer("Ezra Pound", true);	a2 = new Answer("Vladimir Nabokov", false);	a3 = new Answer("David Foster Wallace", false);	a4 = new Answer("Franz Kafka", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who left an unfinished work called Cantos?", a, c);
        a1 = new Answer("That day's train ticket", true);	a2 = new Answer("His mistress' photograph", false);	a3 = new Answer("An unfinished story", false);	a4 = new Answer("A picture of himself", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was found in Albert Camus' pocket after his fatal car crash?", a, c);
        a1 = new Answer("Truman Capote", true);	a2 = new Answer("Harper Lee", false);	a3 = new Answer("F. Scott Fitzgerald", false);	a4 = new Answer("Patrick Hamilton", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who wrote Breakfast At Tiffany's?", a, c);
        a1 = new Answer("They are the same person", true);	a2 = new Answer("They are sisters", false);	a3 = new Answer("They are cousins", false);	a4 = new Answer("They all married the same man", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the particular link between Jean Plaidy, Phillipa Carr and Victoria Holt?", a, c);
        a1 = new Answer("James Baldwin", true);	a2 = new Answer("Patrick Hamilton", false);	a3 = new Answer("Ralph Ellison", false);	a4 = new Answer("Ernest Hemingway", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose novels include Go Tell It On The Mountain?", a, c);
        a1 = new Answer("Sherwood Anderson", true);	a2 = new Answer("Sinclair Lewis", false);	a3 = new Answer("E.B. White", false);	a4 = new Answer("Donald Ray Pollock", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who wrote Winesburg, Ohio?", a, c);
        a1 = new Answer("Salman Rushdie", true);	a2 = new Answer("Jose Saramago", false);	a3 = new Answer("Alexander Pushkin", false);	a4 = new Answer("Toni Morrison", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who won a Booker Prize for Midnight's Children?", a, c);
        a1 = new Answer("Dick Francis", true);	a2 = new Answer("Felix Francis", false);	a3 = new Answer("Sue Grafton", false);	a4 = new Answer("Daniel Silva", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose sports-based novels of the 90s include Comeback and To the Hilt?", a, c);
        a1 = new Answer("50s", true);	a2 = new Answer("70s", false);	a3 = new Answer("80s", false);	a4 = new Answer("60s", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which decade was The Lord Of The Rings first published?", a, c);
        a1 = new Answer("Jack London", true);	a2 = new Answer("Lois Lowry", false);	a3 = new Answer("S.E. Hinton", false);	a4 = new Answer("Robert Louis Stevenson", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which novelist wrote White Fang?", a, c);
        a1 = new Answer("Edward Albee", true);	a2 = new Answer("Henrik Ibsen", false);	a3 = new Answer("Tennessee Williams", false);	a4 = new Answer("Arthur Millar", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who wrote Who's Afraid of Virginia Woolf?", a, c);
        a1 = new Answer("Arthur Golden", true);	a2 = new Answer("Sue Monk Kidd", false);	a3 = new Answer("Khaled Hosseini", false);	a4 = new Answer("Jhumpa Lahiri", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who wrote Memoirs of a Geisha?", a, c);
        a1 = new Answer("3", true);	a2 = new Answer("7", false);	a3 = new Answer("2", false);	a4 = new Answer("6", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many separate volumes make up the Lord of the Rings as originally published?", a, c);
        a1 = new Answer("Lucy's Holiday", true);	a2 = new Answer("Sushi for Beginner's", false);	a3 = new Answer("Watermelon", false);	a4 = new Answer("Last Chance Saloon", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which of the following is not a book by Marian Keyes?", a, c);
        a1 = new Answer("The History of House Elves", true);	a2 = new Answer("Fantastic Beasts & Where to Find Them", false);	a3 = new Answer("Quidditch through the Ages", false);	a4 = new Answer("The Tales of Beedle the Bard", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which is NOT a companion book in the \"Harry Potter\" series?", a, c);
        a1 = new Answer("Autism", true);	a2 = new Answer("Schizophrenia", false);	a3 = new Answer("Mental retardation", false);	a4 = new Answer("Paranoid personality disorder", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("\"The Curious Incident of the Dog in the Night-time\" is the first person account of a young man with what disorder?", a, c);
        a1 = new Answer("Wally Lamb", true);	a2 = new Answer("Augusten Burroughs", false);	a3 = new Answer("John Grogan", false);	a4 = new Answer("Jay McInerney", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("His debut novel \"She's Come Undone\" was published in 1992 and rose to the top of the New York Times Best Seller List. ", a, c);
        a1 = new Answer("The Road", true);	a2 = new Answer("Child of God", false);	a3 = new Answer("The End of the Road", false);	a4 = new Answer("The End God", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What post-apocalyptic novel by Cormac McCarthy was published in 2006?", a, c);
        a1 = new Answer("Sue Monk Kidd", true);	a2 = new Answer("Anita Diamant", false);	a3 = new Answer("Zora Neale Hurston", false);	a4 = new Answer("Alice Munro", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("\"The Secret Life of Bees\" was a best selling debut novel (2002) for what nurse turned novelist?", a, c);
        a1 = new Answer("A design that reads the same word when oriented in two different ways.", true);	a2 = new Answer("A word with scrambled letters", false);	a3 = new Answer("A word that reads the same forward and backwards.", false);	a4 = new Answer("A word that means the opposite of what you expect", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In \"Angels and Demons\" Brown introduces readers to the art of ambigrams. What is an ambigram?", a, c);
        a1 = new Answer("Do Androids Dream of Electric Sheep?", true);	a2 = new Answer("The Man in the High Castle", false);	a3 = new Answer("We Can Build You", false);	a4 = new Answer("Flow My Tears, the Policeman Said", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The movie Blade Runner was based on what novel by Philip K. Dick?", a, c);
        a1 = new Answer("Something Happened", true);	a2 = new Answer("The Floating Opera", false);	a3 = new Answer("Henderson the Rain King", false);	a4 = new Answer("Closing Time", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The sequel to Joseph Heller's Catch-22 was titled?", a, c);
        a1 = new Answer("White Noise", true);	a2 = new Answer("Carpenter's Gothic", false);	a3 = new Answer("Seize the Day", false);	a4 = new Answer("Going Postal", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("A professor of Hitler studies at a small liberal arts college flees a toxic outbreak with his family, shoots his wife's lover, and then takes him to the hospital in which book?", a, c);
        a1 = new Answer("JR", true);	a2 = new Answer("The Adventures of Augie March", false);	a3 = new Answer("Billy Bathgate", false);	a4 = new Answer("Omensetter's Luck", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The central figure of this novel, which is all dialogue, is an 11 year-old boy who builds a financial empire from a phone booth near his school", a, c);
        a1 = new Answer("A Confederacy of Dunces", true);	a2 = new Answer("East of Eden", false);	a3 = new Answer("Grapes of Wrath", false);	a4 = new Answer("Rama II", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("This novel is set in New Orleans and follows the various French Quarter occupations and exploits of one Ignatius J. Reilly, obese admirer of the Roman philosopher Boethius", a, c);

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
        
        Logger.info("Bootstrapped book trivia");
    }
 
}
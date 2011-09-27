
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

public class TechBootstrap extends Job {

    public void doJob() {
        Category c = Category.getOrCreate("Tech");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;			
        Category.clearQuestionsAndResponses(c);
                		
        a1 = new Answer("Wounds", true);	a2 = new Answer("Roof shingles", false);	a3 = new Answer("Tiles", false);	a4 = new Answer("Books", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Alan Roberts' special super glue was used to join what?", a, c);
        a1 = new Answer("Altair", true);	a2 = new Answer("Apple", false);	a3 = new Answer("Amstrad", false);	a4 = new Answer("Commodore PET", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of the first home computer to be manufactured?", a, c);
        a1 = new Answer("R.U.R.", true);	a2 = new Answer(" The Makropulos Affair", false);	a3 = new Answer("The Absolute at Large", false);	a4 = new Answer("Krakatit", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which play by Karel Capek introduced the word robot?", a, c);
        a1 = new Answer("Little Boy", true);	a2 = new Answer("Little One", false);	a3 = new Answer("Nice Boy", false);	a4 = new Answer("One Boy", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the codename of the first atomic bomb dropped on Hiroshima?", a, c);
        a1 = new Answer("Polio", true);	a2 = new Answer("SARS", false);	a3 = new Answer("Flu", false);	a4 = new Answer("Chicken pox", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Edward Salk developed a vaccine against what?", a, c);
        a1 = new Answer("Guy's London", true);	a2 = new Answer("Mount Sinai", false);	a3 = new Answer("Mayo Clinic", false);	a4 = new Answer("American Hospital of Paris", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which hospital performed the first heart surgery on a baby in its mother's womb?", a, c);
        a1 = new Answer("The Branch Mall", true);	a2 = new Answer("U Look Hawt", false);	a3 = new Answer("Cyber Shopping R Us", false);	a4 = new Answer("Cyber Stroll", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first shopping mall of the Internet called?", a, c);
        a1 = new Answer("Polaroid", true);	a2 = new Answer("Digital", false);	a3 = new Answer("Pinhole camera", false);	a4 = new Answer("35MM Camera", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What type of camera did Edwin Land develop?", a, c);
        a1 = new Answer("The Internet", true);	a2 = new Answer("Illegal wire tapping", false);	a3 = new Answer("Night vision goggles", false);	a4 = new Answer("Double jeopardy", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Leslie Rogge was the first person to be arrested due to what?", a, c);
        a1 = new Answer("William Shockley", true);	a2 = new Answer("Alexander Fleming", false);	a3 = new Answer("Clifford Berry", false);	a4 = new Answer("Vannevar Bush", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who led the team which invented transistors in the 1940s?", a, c);
        a1 = new Answer("French", true);	a2 = new Answer("American", false);	a3 = new Answer("English", false);	a4 = new Answer("German", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What nationality of plane first broke the 100mph sound barrier?", a, c);
        a1 = new Answer("Austria", true);	a2 = new Answer("Germany", false);	a3 = new Answer("Namibia", false);	a4 = new Answer("America", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The first air collision took place over which country?", a, c);
        a1 = new Answer("Harness maker", true);	a2 = new Answer("Dry cleaner", false);	a3 = new Answer("Dog walker", false);	a4 = new Answer("Carriage driver", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("William Henry Hoover started making vacuum cleaners because his original trade was dying out; what was it?", a, c);
        a1 = new Answer("Paper", true);	a2 = new Answer("Wheel", false);	a3 = new Answer("Pencil", false);	a4 = new Answer("Abacus", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What landmark invention did Ts'ai Lun invent from bark and hemp in the second century?", a, c);
        a1 = new Answer("Post-it notes", true);	a2 = new Answer("Bookmarks", false);	a3 = new Answer("Door stopper", false);	a4 = new Answer("Highlighter", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did Art Fry invent after scraps of paper to mark tunes in his hymnal kept falling out?", a, c);
        a1 = new Answer("Galileo", true);	a2 = new Answer("Petrus Alphonsi", false);	a3 = new Answer("William Henry Smyth", false);	a4 = new Answer("Yi Xing", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What astronomer invented the thermometer in 1592?", a, c);
        a1 = new Answer("18th", true);	a2 = new Answer("4th", false);	a3 = new Answer("8th ", false);	a4 = new Answer("20th", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What century saw the invention of the shoelace?", a, c);
        a1 = new Answer("The trampoline", true);	a2 = new Answer("The safety harness", false);	a3 = new Answer("Spandex", false);	a4 = new Answer("Juggling pins", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question(" What did George Nisser invent after observing high wire performers?", a, c);
        a1 = new Answer("Kodak", true);	a2 = new Answer("Bnter", false);	a3 = new Answer("Alpa", false);	a4 = new Answer("Lumix", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What name did George Eastman invent in 1888 because it was easy to memorize, pronounce, and spell?", a, c);
        a1 = new Answer("The geodesic dome", true);	a2 = new Answer("Monolithic dome", false);	a3 = new Answer("Radome", false);	a4 = new Answer("Thin-shell structure", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What type of structure did R. Buckminster Fuller patent in 1954?", a, c);
        a1 = new Answer("18th", true);	a2 = new Answer("19th", false);	a3 = new Answer("20th", false);	a4 = new Answer("13th", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What century saw Alexander Cummings issued the first patent for a flush toilet?", a, c);
        a1 = new Answer("The plow", true);	a2 = new Answer("The hoe", false);	a3 = new Answer("The shovel", false);	a4 = new Answer("The rake", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What landmark invention eased farming chores for Sumerians in 3500 B.C.?", a, c);
        a1 = new Answer("The vacuum cleaner", true);	a2 = new Answer("The air conditioner", false);	a3 = new Answer("The portable fan", false);	a4 = new Answer("The blow dryer", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What portable device did James Spengler invent in 1907, using a soap box, pillow case, a fan and tape?", a, c);
        a1 = new Answer("Thomas Edison", true);	a2 = new Answer("Thomas Jefferson", false);	a3 = new Answer("Alexander Graham Bell", false);	a4 = new Answer("Benjamin Franklin", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who averaged one patent for every three weeks of his life?", a, c);
        a1 = new Answer("Ovid", true);	a2 = new Answer("Socrates", false);	a3 = new Answer("Plato", false);	a4 = new Answer("Aristotle", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Necessity is the mother of invention was suggested by whom?", a, c);
        a1 = new Answer("Plywood", true);	a2 = new Answer("Insulation", false);	a3 = new Answer("Drywall", false);	a4 = new Answer("Particle board", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The inventor of dynamite and founder of the Nobel Prize Alfred Nobel also invented what building material?", a, c);

        new TriviaResponse(c, "Does not compute. Try again with only the answer's corresponding letter!", ResponseType.REPEAT);
        new TriviaResponse(c, "Unable to understand response. Answer only by entering the matching letter to the answer.", ResponseType.REPEAT);
        new TriviaResponse(c, "You do not confuse me. Answer with only the letter of the answer.", ResponseType.REPEAT);
        new TriviaResponse(c, "That is correct. ", ResponseType.CORRECT);
        new TriviaResponse(c, "You are correct.", ResponseType.CORRECT);
        new TriviaResponse(c, "I’m envious of your brains.", ResponseType.CORRECT);
        new TriviaResponse(c, "Right.", ResponseType.CORRECT);
        new TriviaResponse(c, "You must've attended an Ivy.", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes.", ResponseType.CORRECT);
        new TriviaResponse(c, "You're not awful at this game.", ResponseType.CORRECT);
        new TriviaResponse(c, "You're not bad. I've seen better, but you're not bad.", ResponseType.CORRECT);
        new TriviaResponse(c, "Hm. Correct.", ResponseType.CORRECT);
        new TriviaResponse(c, "Surprisingly right.", ResponseType.CORRECT);
        new TriviaResponse(c, "Sure.", ResponseType.CORRECT);
        new TriviaResponse(c, "That is incorrect.", ResponseType.INCORRECT);
        new TriviaResponse(c, "I’m sorry, you are not correct.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You must be a public university graduate.", ResponseType.INCORRECT);
        new TriviaResponse(c, " Wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "How embarrassing for you.", ResponseType.INCORRECT);
        new TriviaResponse(c, "No.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Not likely.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Definitely not.", ResponseType.INCORRECT);
        new TriviaResponse(c, "You should've quit while ahead.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Hmph. Wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Unsurprisingly incorrect.", ResponseType.INCORRECT);
        new TriviaResponse(c, "I guess this is when we get started. Type \"Edison\" to begin.", ResponseType.SALUTATION);
        new TriviaResponse(c, "I'm here to quiz you on technology and inventions. Type \"Apple\" to get started.", ResponseType.SALUTATION);
        new TriviaResponse(c, "You think you're up to par? Type \"Zuckerberg\" to get things rolling.", ResponseType.SALUTATION);
        new TriviaResponse(c, "Sure, you look like a semi-decent match for me. Respond \"Steve Jobs\" to get started.", ResponseType.SALUTATION);
        
        new TriviaResponse(c, "See you again soon, I'm sure.", ResponseType.CLOSING);
        new TriviaResponse(c, "Don't bother coming back.", ResponseType.CLOSING);
        new TriviaResponse(c, "Maybe we'll see each other again, maybe not.", ResponseType.CLOSING);
        new TriviaResponse(c, "See you on the other side.", ResponseType.CLOSING);
        
        Logger.info("Bootstrapped Tech trivia");
    }
 
}




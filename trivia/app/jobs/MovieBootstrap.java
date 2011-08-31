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
public class MovieBootstrap extends Job {

    public void doJob() {
        Category c = Category.getOrCreate("Movies");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;					
        a1 = new Answer("Apocalypse Now", true);	a2 = new Answer("The Godfather", false);	a3 = new Answer("Superman", false);	a4 = new Answer("The Island of Dr. Moreau", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie sees Marlon Brando blather: \"Horror has a face, and you must make a friend of horror\"?", a, c);
        a1 = new Answer("Breakfast at Tiffany's", true);	a2 = new Answer("My Fair Lady", false);	a3 = new Answer("Roman Holiday", false);	a4 = new Answer("Sabrina", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie has Audrey Hepburn note: \"Personally, I think it's a bit tacky to wear diamonds before I'm 40\"?", a, c);
        a1 = new Answer("Gary Oldman", true);	a2 = new Answer("Colin Firth", false);	a3 = new Answer("Alan Rickman", false);	a4 = new Answer("Ian Holm", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who played Sid Vicious, Lee Harvey Oswald, Count Dracula and Beethoven in movies?", a, c);
        a1 = new Answer("Terminator 2", true);	a2 = new Answer("Terminator", false);	a3 = new Answer("Stay Hungry", false);	a4 = new Answer("Red Heat", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first Arnold Schwarzenegger movie to win four Academy Awards?", a, c);
        a1 = new Answer("Apollo 13", true);	a2 = new Answer("Forrest Gump", false);	a3 = new Answer("Big", false);	a4 = new Answer("Philadelphia", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie earned Tom Hanks his third straight Oscar nomination?", a, c);
        a1 = new Answer("Lucille Ball", true);	a2 = new Answer("Desi Arnaz", false);	a3 = new Answer("William Frawley", false);	a4 = new Answer("Vivian Vance", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who died last - Desi Arnaz, Lucille Ball, William Frawley or Vivian Vance?", a, c);
        a1 = new Answer("Sonny Corleone", true);	a2 = new Answer("Vito Corleone", false);	a3 = new Answer("Johnny Fontane", false);	a4 = new Answer("Fredo Corleone", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What role in The Godfather did Robert De Niro test for?", a, c);
        a1 = new Answer("He was allergic to the makeup", true);	a2 = new Answer("He wasn't funny enough", false);	a3 = new Answer("He professed he had a heart", false);	a4 = new Answer("He got too hot in the outfit", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Why was Buddy Ebsen forced to quit his role as the Tin Man in The Wizard of Oz?", a, c);
        a1 = new Answer("Blonde Crazy", true);	a2 = new Answer("Footlight Parade", false);	a3 = new Answer("Angels with Dirty Faces", false);	a4 = new Answer("Boy Meets Girl", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which film did James Cagney say \"You dirty double-crossing rat\"", a, c);
        a1 = new Answer("Charlie Chaplin", true);	a2 = new Answer("Groucho Marx", false);	a3 = new Answer("Federico Fellini", false);	a4 = new Answer("Milton Berle", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who said, \"All I need to make a comedy is a park, a policeman and a pretty girl?\"", a, c);
        a1 = new Answer("Jean Harlow", true);	a2 = new Answer("Lana Turner", false);	a3 = new Answer("Joan Crawford", false);	a4 = new Answer("Greta Garbo", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("On which 30s screen legend was Cat woman in Batman based?", a, c);
        a1 = new Answer("6", true);	a2 = new Answer("10", false);	a3 = new Answer("13", false);	a4 = new Answer("4", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How old was Shirley Temple when she received an honorary Oscar?", a, c);
        a1 = new Answer("Tony Curtis", true);	a2 = new Answer("Jack Lemmon", false);	a3 = new Answer("Tom Ewell", false);	a4 = new Answer("William Powell", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which Marilyn Monroe co-star said, \"Kissing her is like kissing Hitler?\"", a, c);
        a1 = new Answer("85", true);	a2 = new Answer("66", false);	a3 = new Answer("72", false);	a4 = new Answer("51", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How old was Mae West when she starred in the film Sextet?", a, c);
        a1 = new Answer("Captain Kirk", true);	a2 = new Answer("Spock", false);	a3 = new Answer("Dr. McCoy", false);	a4 = new Answer("Leonard Nimoy", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Michael Meyers wore a Halloween mask of what famous character in the film Halloween?", a, c);
        a1 = new Answer("Under Siege 2", true);	a2 = new Answer("Under Siege", false);	a3 = new Answer("On Deadly Ground", false);	a4 = new Answer("The Patriot", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Katherine Heigl played Steven Segal's teenage daughter in what movie?", a, c);
        a1 = new Answer("Leaving Las Vegas", true);	a2 = new Answer("Casino", false);	a3 = new Answer("The Usual Suspects", false);	a4 = new Answer("Mighty Aphrodite", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What 1995 movie's lead character tells the motel clerk he's there to drink himself to death?", a, c);
        a1 = new Answer("Driving Miss Daisy", true);	a2 = new Answer("The Blues Brothers", false);	a3 = new Answer("Ghostbusters", false);	a4 = new Answer("1941", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What 1989 movie has Dan Aykroyd note: \"Cars don't misbehave\"?", a, c);
        a1 = new Answer("Oliver Stone", true);	a2 = new Answer("Martin Scorsese", false);	a3 = new Answer("James Cameron", false);	a4 = new Answer("Stanley Kubrick", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What director earned a Bronze Star and a Purple Heart during his tour of duty in Vietnam?", a, c);
        a1 = new Answer("Philadelphia", true);	a2 = new Answer("Big", false);	a3 = new Answer("Toy Story", false);	a4 = new Answer("Delaware", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie pairs Tom Hanks and Antonio Banderas as lovers?", a, c);
        a1 = new Answer("Al Pacino", true);	a2 = new Answer("Philip Seymour Hoffman", false);	a3 = new Answer("Chris O'Donnell", false);	a4 = new Answer("Charlie Sheen", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who got an Oscar for incessantly exclaiming \"Hoo-ah\"?", a, c);
        a1 = new Answer("Rosebud", true);	a2 = new Answer("Sled", false);	a3 = new Answer("Mine", false);	a4 = new Answer("Love", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What's the first word uttered in Citizen Kane?", a, c);
        a1 = new Answer("Calvin Klein", true);	a2 = new Answer("Hanes", false);	a3 = new Answer("Giorgio Armani", false);	a4 = new Answer("Nautica", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What brand of underwear does Marty McFly wear in Back to the Future?", a, c);
        a1 = new Answer("Annie Hall", true);	a2 = new Answer("Bridget Jones", false);	a3 = new Answer("Pepper Pots", false);	a4 = new Answer("Laura Croft", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What screen character learned to say \"la-dee-dah\" growing up in Chippewa Falls, Wisconsin?", a, c);
        a1 = new Answer("American Graffiti", true);	a2 = new Answer("Howard the Duck", false);	a3 = new Answer("Hook", false);	a4 = new Answer("Body Heat", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What George Lucas film, made for $750,000 is considered the most profitable movie in Hollywood history?", a, c);
        a1 = new Answer("Wayne's World", true);	a2 = new Answer("The Breakfast Club", false);	a3 = new Answer("Pretty in Pink", false);	a4 = new Answer("This Is Spinal Tap", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie makes the claim: \"Led Zeppelin didn't write songs everyone liked.  They left that to the Bee Gees\"?", a, c);
        a1 = new Answer("A Few Good Men", true);	a2 = new Answer("One Flew Over the Cuckoo's Nest", false);	a3 = new Answer("Easy Rider", false);	a4 = new Answer("As Good As It Gets", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What movie has Jack Nicholson yelling: \"You can't handle the truth\"?", a, c);
        a1 = new Answer("Mario Puzo", true);	a2 = new Answer("Bruce Jay Friedman", false);	a3 = new Answer("Italo Calvino", false);	a4 = new Answer("Francis Ford Coppola", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who wrote the line: \"I'll make him an offer he can't refuse\"?", a, c);
        a1 = new Answer("Mae West", true);	a2 = new Answer("Madonna", false);	a3 = new Answer("Marilyn Monroe", false);	a4 = new Answer("Judy Garland", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was it that said \"Too much of a good thing is wonderful\"?", a, c);
        a1 = new Answer("Oliver Stone", true);	a2 = new Answer("Nancy Meyers", false);	a3 = new Answer("Darren Aronofsky", false);	a4 = new Answer("James L. Brooks", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The 2006 movie \"World Trade Center\" was directed by whom?", a, c);
        a1 = new Answer("Aunt", true);	a2 = new Answer("Sister", false);	a3 = new Answer("Cousin", false);	a4 = new Answer("Mother", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How is Jane Fonda related to Bridget Fonda?", a, c);
        a1 = new Answer("The Incredibles", true);	a2 = new Answer("Toy Story", false);	a3 = new Answer("The Emperor's New Groove", false);	a4 = new Answer("Mars Needs Moms", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In what Disney feature do the characters Frozone, Syndrome, and Bomb Voyage appear?", a, c);
        a1 = new Answer("Jack Dawson", true);	a2 = new Answer("Jack Kent", false);	a3 = new Answer("Jack Rider", false);	a4 = new Answer("Jack Senter", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What character did Leonardo DiCaprio play in the movie Titanic?", a, c);
        a1 = new Answer("Brandy", true);	a2 = new Answer("Maya", false);	a3 = new Answer("Janet Jackson", false);	a4 = new Answer("Tia Mowry", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who played Julie's best friend in the film \"I Still Know What You Did Last Summer\"?", a, c);
        a1 = new Answer("Longfellow", true);	a2 = new Answer("Gladwell", false);	a3 = new Answer("Theodore", false);	a4 = new Answer("Rockwell", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is the name of Gary Cooper's character in Mr. Deeds Goes to Town", a, c);
        a1 = new Answer("Kim Basinger and Halle Berry", true);	a2 = new Answer("Jane Seymour and Kim Basinger", false);	a3 = new Answer("Ursula Andress and Halle Berry", false);	a4 = new Answer("Jane Seymour and Ursula Andress", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which two actresses that have been \"Bond Girls\" have won Oscars?", a, c);
        a1 = new Answer("Morgan Freeman", true);	a2 = new Answer("Sean Connery", false);	a3 = new Answer("Denzel Washington", false);	a4 = new Answer("Bill Maher", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was the narrator for English language version of the movie March of the Penguins?", a, c);
        a1 = new Answer("Flare Gun", true);	a2 = new Answer("BB gun", false);	a3 = new Answer("A fishing hook", false);	a4 = new Answer("His mom's underwear", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did Brian try to kill himself with in the film The Breakfast Club?", a, c);
        a1 = new Answer("Grant Goodeve", true);	a2 = new Answer("Tom Cruise", false);	a3 = new Answer("Tom Hanks", false);	a4 = new Answer("Michael Douglas", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What actor was originally supposed to play the character Luke Skywalker in Star Wars?", a, c);
        a1 = new Answer("Jinxie", true);	a2 = new Answer("Rexie", false);	a3 = new Answer("Mumsie", false);	a4 = new Answer("Buttie", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Meet The Parents, what is the name given to Robert de Niro's cat?", a, c);
        a1 = new Answer("Runway", true);	a2 = new Answer("Vogue", false);	a3 = new Answer("Vanity Times", false);	a4 = new Answer("Blush", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Anne Hathaway works for which magazine in The Devil Wears Prada?", a, c);
        a1 = new Answer("Chihuahua", true);	a2 = new Answer("Pug", false);	a3 = new Answer("Labordoodle", false);	a4 = new Answer("Poodle", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Legally Blond, what kind of dog is Bruiser, Reese Witherspoon's dog?", a, c);
        a1 = new Answer("Priest", true);	a2 = new Answer("Deaf man", false);	a3 = new Answer("Doctor", false);	a4 = new Answer("Lawyer", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Rowan Atkinson plays a what in the movie \"Four Weddings and a Funeral\"?", a, c);
        a1 = new Answer("Orange", true);	a2 = new Answer("White", false);	a3 = new Answer("Black", false);	a4 = new Answer("Yellow", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Die Another Day, what color is the famous bikini that Halle Berry wears?", a, c);
        a1 = new Answer("Tom Cruise", true);	a2 = new Answer("Gary Pullman", false);	a3 = new Answer("Ashton Kutcher", false);	a4 = new Answer("Danny Masterson", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Actor William Mapother has a superstar brother, what is his name?", a, c);
        a1 = new Answer("Rock Hudson", true);	a2 = new Answer("Tony Curtis", false);	a3 = new Answer("Cary Grant", false);	a4 = new Answer("Humphrey Bogart", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What star actor was a vacuum cleaner salesman before hitting it big as an actor?", a, c);
        a1 = new Answer("Lauren Bacall", true);	a2 = new Answer("Jean Crawford", false);	a3 = new Answer("Judy Garland", false);	a4 = new Answer("Natalie Wood", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What actress did Humphrey Bogart marry in 1945?", a, c);
        a1 = new Answer("Tollbooth worker", true);	a2 = new Answer("Delivery guy", false);	a3 = new Answer("Waiter", false);	a4 = new Answer("Chef", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Adam Sandler played a what in Big Daddy?", a, c);
        a1 = new Answer("School bus driver", true);	a2 = new Answer("Waiter", false);	a3 = new Answer("Dentist", false);	a4 = new Answer("Reporter", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Bachelor Party, what is Tom Hanks' character's job?", a, c);
        a1 = new Answer("Goldie Hawn", true);	a2 = new Answer("Melissa Hawn", false);	a3 = new Answer("Jane Hawn", false);	a4 = new Answer("Melissa Jane", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is Goldie Hawn's real name?", a, c);
        a1 = new Answer("To Have and To Have Not", true);	a2 = new Answer("How to Marry a Millionaire", false);	a3 = new Answer("Young Man with a Horn", false);	a4 = new Answer("The Big Sleep", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which film did Bacall say to Bogart, \"If you want me just whistle?\"", a, c);
        a1 = new Answer("All About Eve", true);	a2 = new Answer("Jezebel", false);	a3 = new Answer("The Star", false);	a4 = new Answer("Whatever Happened to Baby Jane?", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which film did Bette Davis say,  \"Fasten your seatbelts, it's going to be a bumpy night?\"", a, c);
        a1 = new Answer("Jake", true);	a2 = new Answer("Curtis", false);	a3 = new Answer("Ray", false);	a4 = new Answer("Duck", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was Elwood's brother in The Blues Brothers?", a, c);
        a1 = new Answer("7", true);	a2 = new Answer("2", false);	a3 = new Answer("12", false);	a4 = new Answer("20", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many times was Richard Burton nominated for an Oscar, though he never won?", a, c);
        a1 = new Answer("7", true);	a2 = new Answer("10", false);	a3 = new Answer("5", false);	a4 = new Answer("2", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many years after Terminator was Terminator 2 released?", a, c);
        a1 = new Answer("Lego brick", true);	a2 = new Answer("Snowglobe", false);	a3 = new Answer("Train", false);	a4 = new Answer("Car", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Inside what toy does a love scene in Honey I Shrunk The Kids take place?", a, c);
        a1 = new Answer("Mick", true);	a2 = new Answer("James", false);	a3 = new Answer("Joe", false);	a4 = new Answer("Albert", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Crocodile Dundee's real first name?", a, c);
        a1 = new Answer("Barbara Streisand", true);	a2 = new Answer("Judy Garland", false);	a3 = new Answer("Joan Rivers", false);	a4 = new Answer("Bette Midler", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who said, \"I knew that with a mouth like mine I just had to be a star or something?\"", a, c);
        a1 = new Answer("The Full Monty", true);	a2 = new Answer("Bean", false);	a3 = new Answer("The Borrowers", false);	a4 = new Answer("Spiceworld", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which 1997 film was the then most successful British movie of all time?", a, c);
        		
		new TriviaResponse(c, "Welcome to movie trivia.", ResponseType.SALUTATION);
		
        new TriviaResponse(c, "Like, what?", ResponseType.REPEAT);
        new TriviaResponse(c, "I totally don’t understand what you’re saying. Type that out again?", ResponseType.REPEAT);
        new TriviaResponse(c, "You’re making like, no sense. Boring. ", ResponseType.REPEAT);
        new TriviaResponse(c, "La, la, la I can’t understand you. Try again.", ResponseType.REPEAT);
        new TriviaResponse(c, "Unfair, I don’t know what you’re trying to say! ", ResponseType.REPEAT);

        new TriviaResponse(c, "You’re rocking it!", ResponseType.CORRECT);
        new TriviaResponse(c, "Totally awesome.", ResponseType.CORRECT);
        new TriviaResponse(c, "Awesome.", ResponseType.CORRECT);
        new TriviaResponse(c, "Totally.", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes way.", ResponseType.CORRECT);
        new TriviaResponse(c, "OMG you’re so good at this game!", ResponseType.CORRECT);
        new TriviaResponse(c, "Definitely.", ResponseType.CORRECT);
        new TriviaResponse(c, "Yes!", ResponseType.CORRECT);
        new TriviaResponse(c, "So right.", ResponseType.CORRECT);
        new TriviaResponse(c, "Way to go!", ResponseType.CORRECT);

        new TriviaResponse(c, "You’re so clueless.", ResponseType.INCORRECT);
        new TriviaResponse(c, "OMG don’t you know anything?", ResponseType.INCORRECT);
        new TriviaResponse(c, "No way.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Totally NOT awesome.", ResponseType.INCORRECT);
        new TriviaResponse(c, "I’m kinda like, feeling bad for you right now.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Def so wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Ew, no!", ResponseType.INCORRECT);
        new TriviaResponse(c, "You’re totally off.", ResponseType.INCORRECT);
        new TriviaResponse(c, "That’s so wrong.", ResponseType.INCORRECT);
        new TriviaResponse(c, "Talk to the hand. You’re so off.", ResponseType.INCORRECT);
        
        Logger.info("Bootstrapped movie trivia");
    }
 
}
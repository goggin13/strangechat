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

public class TVBootstrap extends Job {

    public void doJob() {
    	Category c = Category.getOrCreate("TV");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;				
        Category.clearQuestionsAndResponses(c);
            		
        a1 = new Answer("Ellen", true);	a2 = new Answer("Friends", false);	a3 = new Answer("Seinfeld", false);	a4 = new Answer("ER", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which show was originally called These Friends of Mine?", a, c);
        a1 = new Answer("Thing (the hand)", true);	a2 = new Answer("Grandmama", false);	a3 = new Answer("Uncle Fester", false);	a4 = new Answer("Pugsley", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Other than Lurch, who did Ted Cassidy play in The Addams Family?", a, c);
        a1 = new Answer("Theo", true);	a2 = new Answer("Michael", false);	a3 = new Answer("Jim", false);	a4 = new Answer("Allan", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Kojak's first name?", a, c);
        a1 = new Answer("New York City", true);	a2 = new Answer("Los Angeles", false);	a3 = new Answer("San Francisco", false);	a4 = new Answer("Miami", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What city played host to the very first season of \"Real World\"?", a, c);
        a1 = new Answer("The Amazing Race", true);	a2 = new Answer("Survivor", false);	a3 = new Answer("Fear Factor", false);	a4 = new Answer("The Mole", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The host is Phil Keoghan, the prize: one million dollars. Which show is it?", a, c);
        a1 = new Answer("Statue of Liberty", true);	a2 = new Answer("A car", false);	a3 = new Answer("A person", false);	a4 = new Answer("The White House", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("ABC cancelled Get Smart after what was blown up in an episode?", a, c);
        a1 = new Answer("Tracy Ullman", true);	a2 = new Answer("Arsenio Hall", false);	a3 = new Answer("Jay Leno Show", false);	a4 = new Answer("Merv Griffin", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("On whose comedy show did The Simpsons first appear?", a, c);
        a1 = new Answer("Four", true);	a2 = new Answer("Three", false);	a3 = new Answer("Two", false);	a4 = new Answer("Five", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many Monkees were there?", a, c);
        a1 = new Answer("Tattoo", true);	a2 = new Answer("Doozy", false);	a3 = new Answer("Rudder", false);	a4 = new Answer("Tiny Man", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Fantasy Island what was Mr. Roarke's first assistant called?", a, c);
        a1 = new Answer("The Mary Tyler Moore show", true);	a2 = new Answer("Laverne and Shirley", false);	a3 = new Answer("Murphy Brown", false);	a4 = new Answer("Seinfeld", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which series did Ed Asner play grumpy news editor Lou Grant?", a, c);
        a1 = new Answer("McKenzie, Brackman, Chaney & Kuzak", true);	a2 = new Answer("Cage and Fish", false);	a3 = new Answer("Donnell, Young, Dole, & Frutt", false);	a4 = new Answer("Young, Frutt, & Berluti", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of the law firm in LA Law?", a, c);
        a1 = new Answer("Jason Alexander", true);	a2 = new Answer("Jerry Seinfeld", false);	a3 = new Answer("Michael Richards", false);	a4 = new Answer("Wayne Knight", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which Seinfeld star was the voice of Hugo in The Hunchback of Notre Dame?", a, c);
        a1 = new Answer("George Costanza", true);	a2 = new Answer("Cosmo Kramer", false);	a3 = new Answer("Newman", false);	a4 = new Answer("Jerry Seinfeld", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What Seinfeld character takes off his shirt during visits to the toilet?", a, c);
        a1 = new Answer("Frasier", true);	a2 = new Answer("Seinfeld", false);	a3 = new Answer("Murphy Brown", false);	a4 = new Answer("The Critic", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What sitcom features a deadpan pooch named Eddie, played by a dog named Moose?", a, c);
        a1 = new Answer("Welcome Back, Kotter", true);	a2 = new Answer("The Partridge Family", false);	a3 = new Answer("The Brady Bunch", false);	a4 = new Answer("The A-Team", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the only TV show of the 1970s to have its theme top Billboard's Hot 100?", a, c);
        a1 = new Answer("Phil Donahue", true);	a2 = new Answer("Geraldo Rivera", false);	a3 = new Answer("Maury Povich", false);	a4 = new Answer("Jerry Springer", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What man wore a dress while hosting a TV talk show on transvestites in 1988?", a, c);
        a1 = new Answer("His monologue", true);	a2 = new Answer("His muscles", false);	a3 = new Answer("His interview", false);	a4 = new Answer("His cars", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What does Jay Leno fine-tune with Jimmy Brogan for six to seven hours daily?", a, c);
        a1 = new Answer("Ricky Ricardo", true);	a2 = new Answer("Darrin Stephens", false);	a3 = new Answer("Major Anthony Nelson", false);	a4 = new Answer("Cliff Huxtable", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What sitcom character hated that his mother-in-law always called him \"Mickey\"?", a, c);
        a1 = new Answer("Fine", true);	a2 = new Answer("Dear", false);	a3 = new Answer("Smart", false);	a4 = new Answer("Flower", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What's Fran's last name on The Nanny?", a, c);
        a1 = new Answer("A bikini", true);	a2 = new Answer("Nothing", false);	a3 = new Answer("A fur hat", false);	a4 = new Answer("A red dress", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did Baywatch beauty Pamela Anderson wear to her Cancun wedding?", a, c);
        a1 = new Answer("In Living Color", true);	a2 = new Answer("Saturday Night Live", false);	a3 = new Answer("Martin", false);	a4 = new Answer("The Wayans Bros.", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What TV show lost Jim Carrey when he stepped into the movies?", a, c);
        a1 = new Answer("David Schwimmer", true);	a2 = new Answer("Matt LeBlanc", false);	a3 = new Answer("Matthew Perry", false);	a4 = new Answer("Lisa Kudrow", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who plays a paleontologist on Friends?", a, c);
        a1 = new Answer("Eight", true);	a2 = new Answer("Four", false);	a3 = new Answer("Six", false);	a4 = new Answer("Ten", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many fingers does Homer Simpson have?", a, c);
        a1 = new Answer("Phil Hartman", true);	a2 = new Answer("Dan Akroyd", false);	a3 = new Answer("Dana Carvey", false);	a4 = new Answer("Mike Myers", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What Saturday Night Live cast member played Kap'n Karl on Pee-wee's Playhouse?", a, c);
        a1 = new Answer("The Discovery Channel", true);	a2 = new Answer("MTV", false);	a3 = new Answer("VH1", false);	a4 = new Answer("Comedy Central", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What cable network drew twice its usual audience for a show called The Wonderful World of Dung?", a, c);
        a1 = new Answer("Beavis and Butthead", true);	a2 = new Answer("Chandler Bing and Joey Tribbiani", false);	a3 = new Answer("Bart Simpson and Milhouse Van Houten", false);	a4 = new Answer("David Cross and Bob Odenkirk", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What twosome are known as \"The Bad Boys\" in Mexico?", a, c);
        a1 = new Answer("Walter Cronkite", true);	a2 = new Answer("Larry King", false);	a3 = new Answer("Dan Rather", false);	a4 = new Answer("Charles Collingwood", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What former TV anchorman made headlines by attending two Grateful Dead concerts?", a, c);
        a1 = new Answer("Felix the Cat", true);	a2 = new Answer("Mickey Mouse", false);	a3 = new Answer("Donald Duck", false);	a4 = new Answer("Casper the Friendly Ghost", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was the first cartoon character licensed for use on merchandise?", a, c);
        a1 = new Answer("George Clooney", true);	a2 = new Answer("Anthony Edwards", false);	a3 = new Answer("Noah Wyle", false);	a4 = new Answer("Eriq La Salle", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who appeared in Return of the Killer Tomatoes before he landed a role on ER?", a, c);
        a1 = new Answer("One Day At a Time", true);	a2 = new Answer("The Odd Couple", false);	a3 = new Answer("What's Happening", false);	a4 = new Answer("Night Court", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first sitcom to have a divorced single parent as its main character?", a, c);
        a1 = new Answer("Bloopers by the cast", true);	a2 = new Answer("Animations of tools", false);	a3 = new Answer("Animations of the cast", false);	a4 = new Answer("Pictures from the set", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was shown during the closing credits of Home Improvements?", a, c);
        a1 = new Answer("Kwai Chang Caine", true);	a2 = new Answer("Radames Pera", false);	a3 = new Answer("Po", false);	a4 = new Answer("Su Yen", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of David Carradine's character in Kung Fu?", a, c);
        a1 = new Answer("California", true);	a2 = new Answer("Chief", false);	a3 = new Answer("Carolina", false);	a4 = new Answer("Cops", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did the C stand for in CHiPs?", a, c);
        a1 = new Answer("Haim Saban", true);	a2 = new Answer("William Hanna", false);	a3 = new Answer("Joseph Barberra", false);	a4 = new Answer("Fred Quimby", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who created the Mighty Morphin Power Rangers?", a, c);
        a1 = new Answer("Twin Peaks", true);	a2 = new Answer("Dallas", false);	a3 = new Answer("Night Court", false);	a4 = new Answer("Dynasty", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which series asked, \"Who killed Laura Palmer?\"", a, c);
        a1 = new Answer("Arlen, Texas", true);	a2 = new Answer("Waco, Texas", false);	a3 = new Answer("Little Rock, Arkansas", false);	a4 = new Answer("Dallas, Texas", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Where did the Hills live in King of the Hill?", a, c);
        a1 = new Answer("Sock it to me!", true);	a2 = new Answer("Here comes the judge!", false);	a3 = new Answer("Let him in!", false);	a4 = new Answer("Let me in!", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which Laugh In catchphrase did Richard Nixon say on the show?", a, c);
        a1 = new Answer("Stigwood", true);	a2 = new Answer("Wedgewood", false);	a3 = new Answer("Rosewood", false);	a4 = new Answer("Elmwood", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In 1980s' The Cosby Show on which Avenue did the Huxtables live?", a, c);
        a1 = new Answer("Feeny", true);	a2 = new Answer("De Fazio", false);	a3 = new Answer("Dempsey", false);	a4 = new Answer("Fine", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Laverne and Shirley, what was Shirley's last name?", a, c);
        a1 = new Answer("B. A. Baracus", true);	a2 = new Answer("Sergeant", false);	a3 = new Answer("Billy", false);	a4 = new Answer("Bosco", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In the A Team how was Mr. T also known?", a, c);
        a1 = new Answer("Arnold's", true);	a2 = new Answer("Jed's", false);	a3 = new Answer("Jim's", false);	a4 = new Answer("Good's", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose Drive In was a focal point in Happy Days?", a, c);
        a1 = new Answer("Nice hat.", true);	a2 = new Answer("Rachel!", false);	a3 = new Answer("Do you know who I am?", false);	a4 = new Answer("Get over there!", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the Duchess of York's first line in Friends?", a, c);
        a1 = new Answer("Dry cleaner's", true);	a2 = new Answer("Bank", false);	a3 = new Answer("Police", false);	a4 = new Answer("Diner", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The HQ of U.N.C.L.E. was behind a wall in what type of building?", a, c);
        a1 = new Answer("Garry Moore Show", true);	a2 = new Answer("The George Burns and Gracie Allen Show ", false);	a3 = new Answer("The Ed Sullivan Show", false);	a4 = new Answer("The Tonight Show", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In which show did Carol Burnett first come to TV prominence?", a, c);
        a1 = new Answer("Dale Cooper", true);	a2 = new Answer("Maddy Ferguson", false);	a3 = new Answer("Bobby Briggs", false);	a4 = new Answer("Leo Johnson", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of the FBI agent who had to find out who killed Laura Palmer?", a, c);
        a1 = new Answer("The Pacific Princess", true);	a2 = new Answer("The Love Boat", false);	a3 = new Answer("The Oceania", false);	a4 = new Answer("The Atlantic Adventurer", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of the liner on The Love Boat?", a, c);
        a1 = new Answer("Buddy boy", true);	a2 = new Answer("Boss man", false);	a3 = new Answer("Human", false);	a4 = new Answer("Old chap", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In the classic sitcom Mr. Ed, what did Mr. Ed call Wilbur?", a, c);
        a1 = new Answer("Maude", true);	a2 = new Answer("Archie Bunker's Place", false);	a3 = new Answer("Till Death Do Us Part", false);	a4 = new Answer("The Cosby Show", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first spin-off from All In The Family called?", a, c);
        a1 = new Answer("Willie", true);	a2 = new Answer("Mitch", false);	a3 = new Answer("Chap", false);	a4 = new Answer("Charlie", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Gilligan's Island what was the first name of \"Gilligan?", a, c);
        a1 = new Answer("Tates & Campbells", true);	a2 = new Answer("Smiths & Jones", false);	a3 = new Answer("Dempseys & Forrests", false);	a4 = new Answer("McIntire & Champlains", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who were the two main families in TV's Soap?", a, c);
        a1 = new Answer("Teri Hatcher", true);	a2 = new Answer("Eva Longoria", false);	a3 = new Answer("Felicity Huffman", false);	a4 = new Answer("Marcia Cross", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was a cheerleader for the San Francisco 49ers?", a, c);
        a1 = new Answer("Steve Martin", true);	a2 = new Answer("Alec Baldwin", false);	a3 = new Answer("Tom Hanks", false);	a4 = new Answer("Robin Williams", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who's been Saturday Night Live's most frequent host?", a, c);
        a1 = new Answer("Martha Stewart", true);	a2 = new Answer("Oprah", false);	a3 = new Answer("Rachael Ray", false);	a4 = new Answer("Ellen", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What happy host chirps on TV: \"It's a good thing\"?", a, c);
    
        new TriviaResponse(c, "Welcome to TV Trivia.", ResponseType.SALUTATION);

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
        
        Logger.info("Bootstrapped TV trivia");
    }
 
}
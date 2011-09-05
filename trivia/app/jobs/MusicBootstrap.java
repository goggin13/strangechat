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
public class MusicBootstrap extends Job {

    public void doJob() {
    	Category c = Category.getOrCreate("Music");	List<Answer> a; Answer a1; Answer a2; Answer a3; Answer a4;					
    	a1 = new Answer("Eleanor", true);	a2 = new Answer("A Day in the Life", false);	a3 = new Answer("Can't Buy Me Love", false);	a4 = new Answer("Good Day Sunshine", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which hit managed to rhyme groovy with movie?", a, c);
    	a1 = new Answer("David Bowie", true);	a2 = new Answer("Ziggy Stardust", false);	a3 = new Answer("MC Hammer", false);	a4 = new Answer("Snoop Dogg", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who issued bonds in his name for people to invest in in 1997?", a, c);
    	a1 = new Answer("Booby McFerrin", true);	a2 = new Answer("Bob Marley", false);	a3 = new Answer("Herbie Hancock", false);	a4 = new Answer("Chick Corea", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who had an 80s No 1 with Don't Worry Be Happy?", a, c);
    	a1 = new Answer("Tom's", true);	a2 = new Answer("John's ", false);	a3 = new Answer("Bob's ", false);	a4 = new Answer("Bill's", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Suzanne Vega was in whose Diner?", a, c);
    	a1 = new Answer("Born in the USA", true);	a2 = new Answer("Born to Run", false);	a3 = new Answer("Tunnel of Love", false);	a4 = new Answer("Darkness on the Edge of Town", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which Bruce Springsteen album featured Dancing In The Dark and I'm On Fire?", a, c);
    	a1 = new Answer("Boyz II Men", true);	a2 = new Answer("Luther Vandross", false);	a3 = new Answer("Bone Thugs and Harmony", false);	a4 = new Answer("Jay-Z", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who sang with Mariah Carey on One Sweet Day?", a, c);
    	a1 = new Answer("Saving All My Love For You", true);	a2 = new Answer("How Will I Know", false);	a3 = new Answer("I Will Always Love You", false);	a4 = new Answer("I'm Every Woman", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Whitney Houston's first No 1?", a, c);
    	a1 = new Answer("Willie Nelson", true);	a2 = new Answer("Jimmy Webb", false);	a3 = new Answer("Harry Nilsson", false);	a4 = new Answer("Ray Charles", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who formed the Highwaymen with Johnny Cash, Waylon Jennings & Kris Kristofferson?", a, c);
    	a1 = new Answer("Lady", true);	a2 = new Answer("Come Sail Away", false);	a3 = new Answer("Babe", false);	a4 = new Answer("Mr. Roboto", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first top ten hit for Styx?", a, c);
    	a1 = new Answer("Animals", true);	a2 = new Answer("The Quarrymen", false);	a3 = new Answer("The Yardbirds", false);	a4 = new Answer("Cream", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was the first UK group to top the US chart after The Beatles?", a, c);
    	a1 = new Answer("Debbie", true);	a2 = new Answer("Lisa", false);	a3 = new Answer("Rebecca", false);	a4 = new Answer("Donna", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is the first name of Michael Jackson's second wife?", a, c);
    	a1 = new Answer("Sire", true);	a2 = new Answer("Atlantic", false);	a3 = new Answer("Warner Bros.", false);	a4 = new Answer("Maverick", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("On which label did Madonna's first hits appear?", a, c);
    	a1 = new Answer("Allen Sherman", true);	a2 = new Answer("Adam Sandler", false);	a3 = new Answer("Robert Burns", false);	a4 = new Answer("Weird Al Yankovic", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who famously said Hello Muddah Hello Faddah?", a, c);
    	a1 = new Answer("The Loco-Motion", true);	a2 = new Answer("He Hit Me (It Felt Like A Kiss)", false);	a3 = new Answer("Some Kinda Wonderful", false);	a4 = new Answer("Swinging On A Star", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which Little Eva hit did Kylie Minogue sing in the 80s?", a, c);
    	a1 = new Answer("Linda Ronstadt", true);	a2 = new Answer("Neil Young", false);	a3 = new Answer("Emmylou Harris", false);	a4 = new Answer("Dolly Parton", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("The Eagles formed when they met as a backing group for which singer?", a, c);
    	a1 = new Answer("Mandy", true);	a2 = new Answer("Even Now", false);	a3 = new Answer("Looks Like We Made It", false);	a4 = new Answer("Can't Smile Without You", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was Barry Manilow's first No 1 single?", a, c);
    	a1 = new Answer("Jefferson Starship", true);	a2 = new Answer("Jefferson Group", false);	a3 = new Answer("Airplane", false);	a4 = new Answer("Air", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Under what name did Jefferson Airplane regroup at the top of the charts?", a, c);
    	a1 = new Answer("Steve Clark", true);	a2 = new Answer("Joe Elliot", false);	a3 = new Answer("Phil Collen", false);	a4 = new Answer("Rick Savage", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which member of Def Leppard died in 1991?", a, c);
    	a1 = new Answer("We Can Work It Out", true);	a2 = new Answer("Hello, Goodbye", false);	a3 = new Answer("I'll Be On My Way", false);	a4 = new Answer("Hey Jude", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which No 1 for the Beatles contains the line \"life is very short?\"", a, c);
    	a1 = new Answer("Michael Bolton", true);	a2 = new Answer("Michael Stipe", false);	a3 = new Answer("David Bowie", false);	a4 = new Answer("Kenny G", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which superstar fronted The Nomads and Blackjack before going solo?", a, c);
    	a1 = new Answer("Candy", true);	a2 = new Answer("Garbage", false);	a3 = new Answer("Sweet", false);	a4 = new Answer("Low", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What kind of Man gave Sammy Davis Jr. a No 1?", a, c);
    	a1 = new Answer("Paul McCartney", true);	a2 = new Answer("David Bowie", false);	a3 = new Answer("Elton John", false);	a4 = new Answer("Ryan Adams", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose work for orchestra and chorus, Standing Stone, was premiered in 1997?", a, c);
    	a1 = new Answer("All 4 Love", true);	a2 = new Answer("I Wanna Sex You Up", false);	a3 = new Answer("I Adore Mi Amor", false);	a4 = new Answer("Let's Start with Forever", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first 90s No 1 for Color Me Badd?", a, c);
    	a1 = new Answer("Take A Look At Me Now", true);	a2 = new Answer("I Can Do This", false);	a3 = new Answer("Have You Seen Me Lately", false);	a4 = new Answer("Forever and Always", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which words follow Against all Odds in the title of a Phil Collins No 1 single?", a, c);
    	a1 = new Answer("Sonny Bono", true);	a2 = new Answer("Cher ", false);	a3 = new Answer("Chaz Bono", false);	a4 = new Answer("Mary Bono", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Whose 1991 autobiography was called And The Beat Goes On?", a, c);
    	a1 = new Answer("Toni Braxton", true);	a2 = new Answer("Whitney Houston", false);	a3 = new Answer("Chaka Khan", false);	a4 = new Answer("Faith Evan", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Another Sad Love Song was the first top ten hit for which female singer?", a, c);
    	a1 = new Answer("3T", true);	a2 = new Answer("O-Town", false);	a3 = new Answer("Jackson Again", false);	a4 = new Answer("MJ6", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which band consisting of Michael Jacksons's nephews were launched in the 90s?", a, c);
    	a1 = new Answer("In Utero", true);	a2 = new Answer("Bleach ", false);	a3 = new Answer("Nevermind", false);	a4 = new Answer("Come As You Are", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which album was originally going to be called I Hate Myself And Want To Die?", a, c);
    	a1 = new Answer("Donny Osmond", true);	a2 = new Answer("Frank Zappa", false);	a3 = new Answer("Sonny Bono", false);	a4 = new Answer("Simon & Garfunkel", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was Michael Jackson's hit Ben originally intended for?", a, c);
    	a1 = new Answer("Phil Collins", true);	a2 = new Answer("Michael Jackson", false);	a3 = new Answer("Sting", false);	a4 = new Answer("Dire Straits", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who won Grammys for Best Album, Male Vocalist and Producer in 1986?", a, c);
    	a1 = new Answer("James", true);	a2 = new Answer("Geoffrey", false);	a3 = new Answer("Randal", false);	a4 = new Answer("Michael", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is Paul McCartney's real first name?", a, c);
    	a1 = new Answer("The Police", true);	a2 = new Answer("Michael Jackson", false);	a3 = new Answer("Dolly Parton", false);	a4 = new Answer("Vanessa Williams", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who sang a song named after the writer's pet snake?", a, c);
    	a1 = new Answer("Two", true);	a2 = new Answer("Three", false);	a3 = new Answer("One", false);	a4 = new Answer("Four", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many boys were there in The Pet Shop Boys?", a, c);
    	a1 = new Answer("Paul Simon", true);	a2 = new Answer("Art Garfunkel", false);	a3 = new Answer("Jerry Lee Lewis", false);	a4 = new Answer("Phil Everly", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In Woody Allen's 1977 movie hit, Annie Hall, what famous American singer-songwriter played the part of slick record promoter Tony Lacey?", a, c);
    	a1 = new Answer("Bob Dylan", true);	a2 = new Answer("Frank Zappa", false);	a3 = new Answer("Phil Lesh", false);	a4 = new Answer("Jerry Garcia", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which musician was born in Duluth Minnesota, on 24 May, 1941?", a, c);
    	a1 = new Answer("7", true);	a2 = new Answer("3", false);	a3 = new Answer("9", false);	a4 = new Answer("4", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many songs by the Bee Gees were in the film Saturday Night Fever?", a, c);
    	a1 = new Answer("Garth Brooks", true);	a2 = new Answer("Shania Twain", false);	a3 = new Answer("Dolly Parton", false);	a4 = new Answer("Willie Nelson", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was the first country artist to sell over 10 million copies of an album?", a, c);
    	a1 = new Answer("Soundgarden", true);	a2 = new Answer("Savage Garden", false);	a3 = new Answer("Led Zeppelin", false);	a4 = new Answer("The Honeydrippers", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What band is named after a scuplture in Seattle that hums in the wind?", a, c);
    	a1 = new Answer("Ray Charles", true);	a2 = new Answer("Jerry Lee Lewis", false);	a3 = new Answer("Johnny Cash", false);	a4 = new Answer("Solomon Burke", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What legendary soul singer wrecked his Corvette the first time he drove it?", a, c);
    	a1 = new Answer("Hey Jude", true);	a2 = new Answer("Can't Buy Me Love", false);	a3 = new Answer("Eight Days A Week", false);	a4 = new Answer("I Want To Hold Your Hand", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What Beatles single lasted longest on the charts, at 19 weeks?", a, c);
    	a1 = new Answer("Luciano Pavarotti", true);	a2 = new Answer("Placido Domingo", false);	a3 = new Answer("Jose Carreras", false);	a4 = new Answer("Francesco Domino", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What tenor received a record 165 curtain calls at a Berlin opera house in 1988?", a, c);
    	a1 = new Answer("0", true);	a2 = new Answer("3", false);	a3 = new Answer("2", false);	a4 = new Answer("8", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many songs from the Beatles \"Sgt. Pepper's Lonely Hearts Club Band\" were released as singles?", a, c);
    	a1 = new Answer("Barbara Streisand", true);	a2 = new Answer("Whitney Houston", false);	a3 = new Answer("Madonna", false);	a4 = new Answer("Britney Spears", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who's waxed more gold and platinum albums than any other solo female artist?", a, c);
    	a1 = new Answer("Paris", true);	a2 = new Answer("Madrid", false);	a3 = new Answer("Vienna", false);	a4 = new Answer("Frankfurt", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What city's opera house does \" The Phantom of the Opera\" prowl?", a, c);
    	a1 = new Answer("The Beach Boys", true);	a2 = new Answer("The Eagles", false);	a3 = new Answer("The Doors", false);	a4 = new Answer("Journey", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What California group waited 22 years to score their first chart-toping single since 1966?", a, c);
    	a1 = new Answer("An arm", true);	a2 = new Answer("His life", false);	a3 = new Answer("A leg", false);	a4 = new Answer("Two fingers", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did Def Leppard drummer Rick Allen lose in a 1984 auto accident?", a, c);
    	a1 = new Answer("Rap", true);	a2 = new Answer("BET", false);	a3 = new Answer("CNN", false);	a4 = new Answer("Howard Stern", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What did Ice Cube define as \"the network newscast black people never had\"?", a, c);
    	a1 = new Answer("Wayne Newton", true);	a2 = new Answer("Michael Jackson", false);	a3 = new Answer("Barry Manilow", false);	a4 = new Answer("Liberace", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What crooner spent $75,000 to refurbish his pet penguins' pond?", a, c);
    	a1 = new Answer("Hootie", true);	a2 = new Answer("X-Zibit", false);	a3 = new Answer("Lil Jon", false);	a4 = new Answer("Ludacris", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is Darius Rucker's stage name?", a, c);
    	a1 = new Answer("The Rolling Stones", true);	a2 = new Answer("The Eagles", false);	a3 = new Answer("The Doors", false);	a4 = new Answer("Eurythmics", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What group got its name from the title of a 1950 Muddy Waters song?", a, c);
    	a1 = new Answer("Michael Jackson", true);	a2 = new Answer("Donny Osmond", false);	a3 = new Answer("Joni Mitchell", false);	a4 = new Answer("Linda Ronstadt", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Who was the youngest person to have a chart-topping solo single, in 1970?", a, c);
    	a1 = new Answer("Spinal Tap", true);	a2 = new Answer("The Rolling Stones", false);	a3 = new Answer("The Guess Who", false);	a4 = new Answer("The Clash", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What group's lead guitar player is known for his guitar amps with control knobs that go up to 11, \"one more than 10\"?", a, c);
    	a1 = new Answer("Que Sera Sera", true);	a2 = new Answer("North by Northwest", false);	a3 = new Answer("Madeleine's Theme", false);	a4 = new Answer("Psycho", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the name of the only song from a Hitchcock film to ever win an Oscar for Best Song?", a, c);
    	a1 = new Answer("Get Right", true);	a2 = new Answer("Get back", false);	a3 = new Answer("Step right", false);	a4 = new Answer("Step back", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which of the following are a Jennifer Lopez song?", a, c);
    	a1 = new Answer("Snopp Dogg/Pharell Williams", true);	a2 = new Answer("Ice Cube/ X-Zibit", false);	a3 = new Answer("Snoop Dogg/Eminem", false);	a4 = new Answer("Ja Rule / Nelly", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What male vocalist sings \"Drop It Like It's Hot\"", a, c);
    	a1 = new Answer("50 Cent/Olivia", true);	a2 = new Answer("50 Cent/ Ciara", false);	a3 = new Answer("Snoop Dogg/ Ciara", false);	a4 = new Answer("Snoop Dogg/Maya", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What male and female vocalists sing \"Candy Shop\"?", a, c);
    	a1 = new Answer("David Grey", true);	a2 = new Answer("Gavin Degraw", false);	a3 = new Answer("Elton John", false);	a4 = new Answer("Bob Dylan", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What male vocalist sings the song \"This Year's Love\"?", a, c);
    	a1 = new Answer("Ciara", true);	a2 = new Answer("Whitney Houston", false);	a3 = new Answer("Alicia Keys", false);	a4 = new Answer("Maya", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What female vocalist sings the song \"1- 2 - Step\"?", a, c);
    	a1 = new Answer("Maroon 5", true);	a2 = new Answer("Green Day", false);	a3 = new Answer("Boyz II Men", false);	a4 = new Answer("Creed", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What male group sings the song \"This Love\"?", a, c);
    	a1 = new Answer("Miss Independent", true);	a2 = new Answer("Miss Understood", false);	a3 = new Answer("Miss Represented", false);	a4 = new Answer("Miss Take", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Kelly Clarkson's debut song was...?", a, c);
    	a1 = new Answer("Nobody's Home", true);	a2 = new Answer("He's Gone", false);	a3 = new Answer("I'm Gone", false);	a4 = new Answer("I'm Home", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Avril Lavinge sang this song...", a, c);
    	a1 = new Answer("Family Picture", true);	a2 = new Answer("Just Like A Pill", false);	a3 = new Answer("Get This Party Started", false);	a4 = new Answer("None of these", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Which one of these songs are not on Pink's album Missundaztood?", a, c);
    	a1 = new Answer("Lil Jon/ Ludacris", true);	a2 = new Answer("Trick Daddy/Nelly", false);	a3 = new Answer("Nelly/ Ludacris", false);	a4 = new Answer("Lil Jon/Nelly", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("Usher made two songs with two other rap artists. Who are they?", a, c);
    	a1 = new Answer("\"I Want To Hold Your Hand\"", true);	a2 = new Answer("\"Can't Buy Me Love\"", false);	a3 = new Answer("\"She Loves You\"", false);	a4 = new Answer("\"Please Please Me\"", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first #1 hit for the Beatles?", a, c);
    	a1 = new Answer("Whitney Houston", true);	a2 = new Answer("The Beatles", false);	a3 = new Answer("Michael Jackson", false);	a4 = new Answer("Bee Gees", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What act has had more consecutive #1 hits than any other in the history of Rock and Roll?", a, c);
    	a1 = new Answer("0", true);	a2 = new Answer("2", false);	a3 = new Answer("3", false);	a4 = new Answer("1", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("How many #1 hits has superstar Bruce Springsteen had?", a, c);
    	a1 = new Answer("The Beatles", true);	a2 = new Answer("Elton John", false);	a3 = new Answer("Michael Jackson", false);	a4 = new Answer("The Supremes", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What act holds the record for most #1 hits?", a, c);
    	a1 = new Answer("Hey Jude", true);	a2 = new Answer("Love Me Do", false);	a3 = new Answer("Yesterday", false);	a4 = new Answer("Get Back", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What Beatles' song held the #1 position the longest?", a, c);
    	a1 = new Answer("Dolly Parton", true);	a2 = new Answer("Barbara Streisand", false);	a3 = new Answer("Carly Simon", false);	a4 = new Answer("Olivia Newton John", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("In 1992 Whitney Houston's \"I Will Always Love You\" spent 14 weeks at #1. Who wrote it?", a, c);
    	a1 = new Answer("Hall & Oates", true);	a2 = new Answer("Simon & Garfunkel", false);	a3 = new Answer("Everly Brothers", false);	a4 = new Answer("Sonny & Cher", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What duo has had more #1 hits than any other duo in the Rock and Roll era?", a, c);
    	a1 = new Answer("\"Rapper's Delight\" by Sugar Hill Gang", true);	a2 = new Answer("\"Walk This Way\" by Run DMC", false);	a3 = new Answer("\"Ice Ice Baby\" by Vanilla Ice", false);	a4 = new Answer("\"U Can't Touch This\" by MC Hammer", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What was the first rap song to ever hit #1?", a, c);
    	a1 = new Answer("\"The Twist\" by Chubby Checker", true);	a2 = new Answer("\"Twist and Shout\" by The Beatles", false);	a3 = new Answer("\"Unchained Melody\" by Righteous Brothers", false);	a4 = new Answer("\"Stand By Me\" by Ben E. King", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("What is the only song in the rock era to ever hit #1 twice?", a, c);
    	a1 = new Answer("\"All You Need Is Love\"", true);	a2 = new Answer("\"We Can Work It Out\"", false);	a3 = new Answer("\"Eight Days A Week\"", false);	a4 = new Answer("\"Paperback Writer\"", false);	a = new LinkedList<Answer>(); 	a.add(a1); a.add(a2); a.add(a3); a.add(a4);	new Question("All of these Beatles' songs hit #1, but one of them only held the top spot for one week. Which one?", a, c);
    
    
		new TriviaResponse(c, "Man, I can’t understand ya!", ResponseType.REPEAT);
		new TriviaResponse(c, "Come again?", ResponseType.REPEAT);
		new TriviaResponse(c, "I don’t have the time to play these games. What’re ya saying?", ResponseType.REPEAT);
		new TriviaResponse(c, "You’re making no sense. Tell me again?", ResponseType.REPEAT);
		new TriviaResponse(c, "You’re dancing to your own drum, man. Tell me that again?", ResponseType.REPEAT);
		new TriviaResponse(c, "Dude try typing that out again.", ResponseType.REPEAT);
		  
		new TriviaResponse(c, "Now we’re jammin’!", ResponseType.CORRECT);
		new TriviaResponse(c, "Rock on.", ResponseType.CORRECT);
		new TriviaResponse(c, "Jam out!", ResponseType.CORRECT);
		new TriviaResponse(c, "Now you’re feeling me.", ResponseType.CORRECT);
		new TriviaResponse(c, "Crushing it.", ResponseType.CORRECT);
		new TriviaResponse(c, "Groovy!", ResponseType.CORRECT);
		new TriviaResponse(c, "Yeahhhhh.", ResponseType.CORRECT);
		  
		new TriviaResponse(c, "No worries but you’re so wrong.", ResponseType.INCORRECT);
		new TriviaResponse(c, "You don’t know what you’re talking about. Sorry, man.", ResponseType.INCORRECT);
		new TriviaResponse(c, "My brother you’re failing here! ", ResponseType.INCORRECT);
		new TriviaResponse(c, "Get on my level, man.", ResponseType.INCORRECT);
		new TriviaResponse(c, "You’re killing my vibes here, dude.", ResponseType.INCORRECT);
		
		new TriviaResponse(c, "You want to jam with me?", ResponseType.SALUTATION);
		
		Logger.info("Bootstrapped music trivia");
    }
 
}
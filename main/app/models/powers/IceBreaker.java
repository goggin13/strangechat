package models.powers;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import models.User;
import play.Logger;

public class IceBreaker extends SuperPower {
	
	public IceBreaker() {
		super(
		    "Ice Breaker",
			"http://lh3.ggpht.com/HygyyzxG6-EgjNjQvlfszfqXOEx4144eSqUCEKAEay7Fp_vYIIsAk0N1lp18Hdbh-aopXHo84oBPxiqK",
			"Display an ice breaker message",
			true,
			false
		);
	}
		
	public int isQualified (User user) {
	    return 0;
	}
	
	public int chooseIndex (User caller, User subject) {
	    if (caller == null || subject == null) {
	        if (caller == null) {
	            Logger.error("ice breaker called with a null user");
	        }
	        return IceBreakers.getRandomIndex();
	    }
        Set<Integer> user1Seen = caller.getSeenIceBreakers();
        Set<Integer> user2Seen = subject.getSeenIceBreakers();     
        Set<Integer> seenIndices = caller.getSeenIceBreakers();
        
        seenIndices.addAll(user2Seen);
        int user1SeenCount = user1Seen.size();
        int user2SeenCount = user2Seen.size();
        int availableCount = IceBreakers.size();
        int index;
        
        if (seenIndices.size() >= availableCount) {  // one, both, or together has seen every one
        
            if (user1SeenCount < availableCount) {          // user1 hasn't seen them all
                index = IceBreakers.getRandomIndex(user1Seen);                
            } else if (user2SeenCount < availableCount) {   // user2 hasn't seen them all
                index = IceBreakers.getRandomIndex(user2Seen);
            } else {                                        // they've both seen them all
                index = IceBreakers.getRandomIndex();
            }
                    
        } else {  // we can choose one neither has seen
            index = IceBreakers.getRandomIndex(seenIndices);
        }
        return index;
	}
	
	public synchronized String use (User caller, User subject) {
        int index = chooseIndex(caller, subject);
        caller.addSeenIceBreaker(index);
        if (subject != null) {
            subject.addSeenIceBreaker(index);
        }
        String txt = IceBreakers.get(index);
        return txt;
	}
	
	public static int iceBreakersCount () {
	    return IceBreakers.size();
	}
	
	private static class IceBreakers {
	    private static List<String> messages = new LinkedList<String>();
	    
	    static {
            messages.add("Did you like your high school experience?");
            messages.add("Have any unrequited loves?");
            messages.add("Have you ever wanted to be an astronaut?");
            messages.add("Would you rather be stuck in an elevator or locked in a bathroom?");
            messages.add("Do you think the world will end in 2012?");
            messages.add("Who are your heroes?");
            messages.add("When do you have your best ideas?");
            messages.add("If you won the lottery, what would be your first big purchase?");
            messages.add("If you could only wear one color clothing for the rest of your life, what would you choose?");
            messages.add("What’s your favorite kind of candy?");
            messages.add("What the craziest thing you’ve ever done?");
            messages.add("Does deja vu exist or are we all just slightly psychic?");
            messages.add("Would you rather fly or be able to run faster than 60 mph?");
            messages.add("What’s more important: a dream wedding or a perfect birthday?");
            messages.add("Where did you have your first kiss?");
            messages.add("What’s the worst thing you’ve ever done?");
            messages.add("If you had all the money you needed, would you still work?");
            messages.add("What's your kryptonite?");
            messages.add("Is it more important for superheros to be strong or fast?");
            messages.add("Are you better with recognizing faces or remembering names?");
            messages.add("Do you think Barack Obama is a good President?");
            messages.add("Why do villains always have to spend twenty minutes explaining why they're evil when they catch you?");
            messages.add("Do you like Facebook or Twitter more?");
            messages.add("Who is your favorite author?");
            messages.add("What was the worst class in middle school?");
            messages.add("What super power do you wish you possessed?");
            messages.add("What’s your favorite board game?");
            messages.add("Do you have any superhero siblings?");
            messages.add("Have you ever quit a job and why?");
            messages.add("Were you a fan of the Backstreet Boys? Who was your favorite?");
            messages.add("What’s your favorite word?");
            messages.add("What’s your favorite number?");
            messages.add("What would be the title of your memoir?");
            messages.add("How would you describe yourself in two words?                   ");
            messages.add("What's something romantic to do on a first date?");
            messages.add("What's it like to be in love?");
            messages.add("What's your fondest memory from being small?");
            messages.add("Do you have any tattoos?");
            messages.add("What's the best meal you've ever had?");
            messages.add("What is your favorite food to eat in a bed?");
            messages.add("What's a secret you have?");
            messages.add("What’s your favorite smell?");
            messages.add("What did you dress up as for Halloween last year?");
            messages.add("What movie makes you cry?");
            messages.add("What's your stance on the afterlife?");
            messages.add("What's your take on soul mates?");
            messages.add("What is your greatest regret?");
            messages.add("Do you like spicy food and why?");
            messages.add("Who was your first crush?");
            messages.add("Do you own any pets?");
            messages.add("What is Angelina Jolie's super power?");
            messages.add("What are the 3 most important things in your life?");
            messages.add("What time period would you time travel to?");
            messages.add("Which female superhero would you want to hang out with?");
            messages.add("What instruments do you play?");
            messages.add("What is your favorite band?");
            messages.add("Where did humans come from?");
            messages.add("Do you think there are aliens? What're they like?");
            messages.add("If God had a face what would it look like?");
            messages.add("What's a funny joke?");
            messages.add("What do women want?");
            messages.add("Is it more important to be happy or to be stable in life?");
            messages.add("How do you know if it's love?");
            messages.add("How do you know if someone likes you?");
            messages.add("What makes you laugh?");
            messages.add("What makes people successful?");
            messages.add("Why do people do drugs?");
            messages.add("Why do people drink?");
            messages.add("How do you fall asleep?");
            messages.add("What's your secret lair like?");
            messages.add("Do you have a sidekick?");
            messages.add("Does your true identity wear glasses?");
            messages.add("What would you do to stop a crashing airplane?");
            messages.add("What would you do to stop an astroid from hitting Earth?");
            messages.add("How would you save a baby from a burning building?");
            messages.add("How often do you help little old ladies cross the street?");
            messages.add("How did you become a superhero?");
            messages.add("Who is your nemesis?");
            messages.add("When was the last time you cried?");
            messages.add("How many times have you saved a life?");
            messages.add("What's the best thing you've ever done?");
            messages.add("What was your first love like?");
            messages.add("Are your parents superheroes?");
            messages.add("Who is your favorite X-Men?");
            messages.add("Who is your greatest nemesis?");
            messages.add("What are your weaknesses?");
            messages.add("Blondes, redheads or brunettes?");
            messages.add("The Simpsons, South Park, Futurama or Family Guy?");
            messages.add("What's your superhero slogan?");
            messages.add("What's your theme song?");
            messages.add("What song do you sing in the shower?");
            messages.add("Where do you come from?");
            messages.add("What's your favorite television show?");
            messages.add("Do you peep out people's underwear with your x-ray vision?");
            messages.add("Do you have special superhero transportation?");
            messages.add("Where do you change into your superhero costume?");
            messages.add("Have you ever had any wardrobe malfunctions?");
            messages.add("Does your mask get itchy?");
            messages.add("Have you ever smelled what The Rock is cooking?  ");
            messages.add("Do you think Jean Grey should have been with Wolverine or Cyclops?");
            messages.add("Was Godzilla just misunderstood?");
            messages.add("What was your best age?");
            messages.add("Would you give up everything to accomplish your dreams?");
            messages.add("Do politics matter?");
            messages.add("What's your favorite inspirational quote?");
            messages.add("In-n-Out or Shake Shack?");
            messages.add("Favorite city?");
            messages.add("What planet are you from?");
            messages.add("Why did the chicken cross the road?");
            messages.add("Where will you be when Ozzy comes for you?");
            messages.add("When did you learn you were special?");
            messages.add("How do you get to work?");
            messages.add("What did you do after your first kiss?");
            messages.add("Why are you so odd?");          
	    }
	    
	    // seen MUST BE SORTED
	    public static int getRandomIndex (Set<Integer> seen) {
	        if (seen.size() == 0 || seen.size() == IceBreakers.size()) {
	            return getRandomIndex();
	        }
	        List<Integer> unseen = new LinkedList<Integer>();
	        for (int k = 0; k < size(); k++) {
	            if (!seen.contains(k)) {
	                unseen.add(k);
	            }
	        }
	        if (unseen.size() == 0) {
	            return getRandomIndex();
	        }
	        Collections.shuffle(unseen);	        
	        return unseen.get(0);
	    }

	    public static int getRandomIndex () {
	        Random r = new Random();
	        int size = messages.size();
	        int rand = r.nextInt(size);
	        return rand;
	    }
	    
	    public static String get (int i) {
	        return messages.get(i);
	    }
	    
	    public static int size () {
	        return messages.size();
	    }
	}
	
}
	
	

	
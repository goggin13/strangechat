package models.powers;
import java.util.*;
import enums.Power;
import play.Logger;
import models.*;

public class IceBreaker extends IntervalPower {
	
	public IceBreaker() {
		super(
		    "Ice Breaker",
			"http://lh3.ggpht.com/HygyyzxG6-EgjNjQvlfszfqXOEx4144eSqUCEKAEay7Fp_vYIIsAk0N1lp18Hdbh-aopXHo84oBPxiqK",
			"Display an ice breaker message",
			false,
			false
		);
		this.award_interval = 120;
	}
		
	public Long getFieldValue (User user) {	    
	    return user.chatTime;
	}
	
	// since u get a freebie ice breaker we decrement the count by 1
	public int countPowers (User user) {
	    return super.countPowers(user) - 1;
	}
	
	public int chooseIndex (User caller, User subject) {
        Set<Integer> user1Seen = caller.getSeenIceBreakers();
        Set<Integer> user2Seen = subject.getSeenIceBreakers();     
        Set<Integer> seenIndices = caller.getSeenIceBreakers();
        seenIndices.addAll(user2Seen);
        int user1SeenCount = user1Seen.size();
        int user2SeenCount = user2Seen.size();
                int availableCount = IceBreakers.size();
        int index;
        
        if (seenIndices.size() == availableCount) {  // one, both, or together has seen every one
        
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
	
	public String use (User caller, User subject) {
        int index = chooseIndex(caller, subject);
        caller.addSeenIceBreaker(index);
        subject.addSeenIceBreaker(index);
        String txt = IceBreakers.get(index);
        return txt;
	}
	
	public static int iceBreakersCount () {
	    return IceBreakers.size();
	}
	
	private static class IceBreakers {
	    private static List<String> messages = new ArrayList<String>();
	    private static Random r = new Random();
	    
	    static {
            messages.add("Did you like your high school experience?");
            messages.add("Would you rather watch The Office or Mad Men?");
            messages.add("Have you ever wanted to be an astronaut?");
            messages.add("Will Lindsay Lohan ever make a comeback?");
            messages.add("Do you think the world will end in 2012?");
            messages.add("Who are your heroes?");
            messages.add("Which is better: Harry Potter or Twilight?");
            messages.add("Do you prefer sweet or savory?");
            messages.add("If you could only wear one color clothing for the rest of your life, what would you choose?");
            messages.add("What’s your favorite kind of candy?");
            messages.add("What the craziest thing you’ve ever done?");
            messages.add("If you had to lose an arm or lose a leg, which would you choose?");
            messages.add("Would you rather fly or be able to run faster than 60 mph?");
            messages.add("What’s more important: a dream wedding or a perfect birthday?");
            messages.add("Where did you have your first kiss?");
            messages.add("What’s the worst thing you’ve ever done?");
            messages.add("If you had all the money you needed, would you still work?");
            messages.add("How important is sex in an relationship?");
            messages.add("Is Bill Murray the best comedian of all time?");
            messages.add("What’s better: Italian or Chinese food?");
            messages.add("What’s the best pizza topping?");
            messages.add("What do you put in your coffee?");
            messages.add("Do you think Barack Obama is a good President?");
            messages.add("Do you like to sit by the window or on the aisle on planes?");
            messages.add("Do you like Facebook or Twitter more?");
            messages.add("Who is your favorite author?");
            messages.add("Have you ever had sex in public?");
            messages.add("What was the worst class in middle school?");
            messages.add("What super power do you wish you possessed?");
            messages.add("What’s your favorite board game?");
            messages.add("Do you play Angry Birds?");     
            messages.add("Have you ever quit a job and why?");
            messages.add("Were you a fan of the Backstreet Boys? Who was your favorite?");
            messages.add("What’s your favorite word?");
            messages.add("What’s your favorite number?");
            messages.add("What would be the title of your memoir?");
            messages.add("How would you describe yourself in two words?");                   
            messages.add("How do you make a relationship work?");
            messages.add("What's something romantic to do on a first date?");
            messages.add("What's it like to be in loveeee?");
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
            messages.add("Which is a stronger emotion: anger or love? How do you know?");
            messages.add("What are the 3 most important things in your life?");
            messages.add("What time period would you time travel to?");
            messages.add("What's the best chain restaurant to make out in?");
            messages.add("What instruments do you play?");
            messages.add("Where is a gross place you've made out at?");
            messages.add("Is love real? How can you be so sure?");
            messages.add("What is your favorite band?");
            messages.add("Where did humans come from?");
            messages.add("Do you think there are aliens? What're they like?");
            messages.add("If God had a face what would it look like?");
            messages.add("What's a funny joke?");
            messages.add("What do women want?");
            messages.add("Why is glitter so popular?");
            messages.add("What do boys smell like? What about girls?");
            messages.add("How do you know if it's love?");
            messages.add("How do you know if someone likes you?");
            messages.add("What makes you laugh?");
            messages.add("What makes people successful?");
            messages.add("What is a soul?");
            messages.add("What do farts feel like?");
            messages.add("Why do people do drugs?");
            messages.add("Why do people drink?");
            messages.add("How do you fall asleep?");
            messages.add("What is social media?");            
	    }
	    
	    // seen MUST BE SORTED
	    public static int getRandomIndex (Set<Integer> seen) {
	        List<Integer> unseen = new LinkedList<Integer>();
	        for (int k = 0; k < size(); k++) {
	            if (!seen.contains(k)) {
	                unseen.add(k);
	            }
	        }
	        Collections.shuffle(unseen);
	        return unseen.get(0);
	    }
	    
	    public static int getRandomIndex () {
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
	
	

	
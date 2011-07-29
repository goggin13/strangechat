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
		this.interval = 120;
	}
		
	public Long getFieldValue (User user) {	    
	    return user.chatTime;
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
	    }
	    
	    // seen MUST BE SORTED
	    public static int getRandomIndex (Set<Integer> seen) {
	        int k = 0;
	        for (int i : seen) {
	            if (k < i) {
	                return k;
	            }
	            k++;
	        }
	        return k;
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
	
	

	
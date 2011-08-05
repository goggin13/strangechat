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
	
	// since u get two freebie ice breaker we decrement the count by 2
	public int countPowers (User user) {
	    return super.countPowers(user) - 2;
	}
	
	public int chooseIndex (User caller, User subject) {
	    if (caller == null || subject == null) {
	        Logger.error("ice breaker called with a null user");
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
        
        Logger.info("IB: CHOOSE INDEX u1(" + caller.id + ")->" + user1Seen.size() + 
                            ", u2(" + subject.id + ")->" + user2Seen.size() + 
                            ", both->" + seenIndices.size() + 
                            ", all->" + availableCount);
        
        if (seenIndices.size() >= availableCount) {  // one, both, or together has seen every one
        
            if (user1SeenCount < availableCount) {          // user1 hasn't seen them all
                index = IceBreakers.getRandomIndex(user1Seen);                
            } else if (user2SeenCount < availableCount) {   // user2 hasn't seen them all
                index = IceBreakers.getRandomIndex(user2Seen);
            } else {                                        // they've both seen them all
                index = IceBreakers.getRandomIndex();
            }
                    
        } else {  // we can choose one neither has seen
            Logger.info("IB: return a random one neither has seen");
            index = IceBreakers.getRandomIndex(seenIndices);
        }
        return index;
	}
	
	public synchronized String use (User caller, User subject) {
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
            messages.add("Is Bill Murray the best comedian of all time?");
            messages.add("What’s better: Italian or Chinese food?");
            messages.add("What’s the best pizza topping?");
            messages.add("What do you put in your coffee?");
            messages.add("Do you think Barack Obama is a good President?");
            messages.add("Do you like to sit by the window or on the aisle on planes?");
            messages.add("Do you like Facebook or Twitter more?");
            messages.add("Who is your favorite author?");
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
            messages.add("What is your favorite band?");
            messages.add("Where did humans come from?");
            messages.add("Do you think there are aliens? What're they like?");
            messages.add("If God had a face what would it look like?");
            messages.add("What's a funny joke?");
            messages.add("What do women want?");
            messages.add("What do boys smell like? What about girls?");
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
	    }
	    
	    // seen MUST BE SORTED
	    public static int getRandomIndex (Set<Integer> seen) {
	        
	        Logger.info("IB: get rando index (seen size = " + seen.size() + ")");
	        
	        if (seen.size() == 0 || seen.size() == IceBreakers.size()) {
	            Logger.debug("return rando");
	            return getRandomIndex();
	        }
	        List<Integer> unseen = new LinkedList<Integer>();
	        for (int k = 0; k < size(); k++) {
	            if (!seen.contains(k)) {
	                unseen.add(k);
	            }
	        }
	        if (unseen.size() == 0) {
	            Logger.info("IB: This should never happen; fall through in get random ice breaker index");
	            return getRandomIndex();
	        }
	        Logger.info("IB: return a rando from " + unseen.size() + " choices");
	        Collections.shuffle(unseen);	        
	        return unseen.get(0);
	    }

	    public static Set<Integer> getMessages () {
	        Set<Integer> msgs = new TreeSet<Integer>();
	        for (int i = 0; i < size(); i++) {
	            msgs.add(i);
	        }
	        return msgs;
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
	
	

	
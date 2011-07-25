package models.powers;
import java.util.*;
import enums.Power;
import play.Logger;
import models.*;

public class IceBreaker extends SuperPower {
	public static final HashMap<Integer, Long> levels = new HashMap<Integer, Long>();
	public static int levelCount = 1;
    public static final int bonusLevel;
    public static final int bonus = 3;
    public static final int penultimateLevelInterval = 300; // every 5 minutes
    private static final Long min = 60L;
    
	static {
	    levels.put(0, 0L);         // one for free when u start
        levels.put(levelCount++, 2L * min);
        levels.put(levelCount++, 5L * min);
        levels.put(levelCount++, 10L * min);
        levels.put(levelCount++, 20L * min);
        levels.put(levelCount++, 25L * min);    // 25 minutes
        levels.put(levelCount++, 35L * min);
        levels.put(levelCount++, 50L * min);
        bonusLevel = levelCount;
        levels.put(levelCount++, 60L * min);    // 1 hour
        levels.put(levelCount++, 65L * min);    // start receiving one every 5 minutes
        levels.put(levelCount, min * 60L * 3L); // 3 hours
	}
	
	public IceBreaker() {
		super(
		    "Ice Breaker",
			"http://lh3.ggpht.com/HygyyzxG6-EgjNjQvlfszfqXOEx4144eSqUCEKAEay7Fp_vYIIsAk0N1lp18Hdbh-aopXHo84oBPxiqK",
			"Display an ice breaker message",
			false,
			false
		);
	}
    
	public int isQualified (User user) {	    
	    Power p = this.getPower();
	    int targetLevel = user.currentLevel(p) + 1;
	    Long targetTime = levels.get(targetLevel);
	    
	    if (targetLevel > levelCount) {  // infinity, but never qualify for new ones
	        return 0;
	        
	    } else if (targetLevel < levelCount) {
	        boolean qualified = (user.chatTime == null ? 0 : user.chatTime) >= targetTime;
    		return qualified ? targetLevel : 0;

	    } else if (user.chatTime >= levels.get(levelCount)) { // they made it to the final level!
	        return levelCount;

	    } else if (targetLevel == levelCount) { 
	        // penultimate level (any excuse to use that word)
	        // get one every 5 minutes until they get to final level
            int secsUsed = penultimateLevelInterval * (user.countPowers(p) - levelCount - 2);
	        Long secsAvailable = user.chatTime - secsUsed - levels.get(levelCount - 1);
	        return (secsAvailable >= penultimateLevelInterval) ? levelCount - 1 : 0;
	        
	    } else {       
	        Logger.error("in fall through statement for IceBreaker.isQualified()"); 
	        return 0;
	    }

	}
	
	// overridden from superpower
    public boolean awardIfQualified (User user) {
        int level = isQualified(user);
        
        if (level == bonusLevel) {
            for (int b = 0; b < bonus + 1; b++) {
                super.grantTo(user, level);
            }
        } else if (level > 0) {
            StoredPower sp = super.grantTo(user, level);            
    		if (level == levelCount) {
    		    sp.available = Integer.MAX_VALUE;
                sp.save();
                System.out.println("set stored power to " + Integer.MAX_VALUE);
    		}
    	}
    	 
    	return level > 0;
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
            Collections.shuffle(messages);       
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
	
	

	
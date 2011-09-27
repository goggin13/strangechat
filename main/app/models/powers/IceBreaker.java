package models.powers;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
		
	@Override
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

    @Override
    public String use (User caller, User subject, List<String> params) {
        return use(caller, subject);
    }
		
	@Override
	public String use (User caller, User subject) {
		int index = chooseIndex(caller, subject);
		caller.addSeenIceBreaker(index);
		caller.save();
		if (subject != null) {
		    subject.addSeenIceBreaker(index);
		    subject.save();
		}
        String txt = IceBreakers.get(index);
        return txt;
	}
	
	public static int iceBreakersCount () {
	    return IceBreakers.size();
	}
	
	public static class IceBreakers {
	    private static List<String> messages = new LinkedList<String>();
         		    
	    public static void loadMessages (File file) {
	        BufferedReader br;
			try {
				messages.clear();
				br = new BufferedReader(new FileReader(file));
		        String line = null;  
				while ((line = br.readLine()) != null) {
					line = line.trim();
					if (line.indexOf("##") == -1 && line.length() > 0) {
						messages.add(line);
					}
				}
				br.close();
			} catch (FileNotFoundException e) {
				Logger.error(e.getMessage());
			}catch (IOException e) {
				Logger.error(e.getMessage());				
			}
			Logger.info("loaded ice breakers");
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
	
	

	
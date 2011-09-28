package models.powers;
import java.util.HashMap;

import models.User;
import enums.Power;

public class Emotion extends SuperPower {
    public static final HashMap<Integer, Long> levels = new HashMap<Integer, Long>();
	public static int levelCount = 1;
	private static Long min = 15L;  // just make a "minute" shorter for new interval powers

	static {
        levels.put(levelCount++, 30L * min);
        levels.put(levelCount++, 5L * min);
        levels.put(levelCount, 10L * min);
	}	
	
	public Emotion () {
		super(
		    "Emotion",
			"",
			"Show your feelings",
			true,
			true
		);
	    this.multiRoom = true;
	    this.autoOn = true;		
	}
	
	@Override
	public int isQualified (User user) {
	    
	    Power p = this.getPower();
	    int targetLevel = user.currentLevel(p) + 1;
	    if (targetLevel > levelCount) {
	        return 0;
	    }
	    
	    Long targetTime = levels.get(targetLevel);
	    Long chatTime = user.chatTime;
	    
	    if (chatTime >= targetTime && targetLevel < 2) { // PREVENT LEVELING UP HERE FOR NOW
	        return targetLevel;
	    } else {
	        return 0;
	    }
	}
	
	@Override
	public String use (User caller, User subject) {
	    return "emotion";
	}
	
}

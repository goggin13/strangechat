package enums;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import models.karma.Fart;
import models.karma.GoldCoin;
import models.karma.Reward;

// Creating a new Reward
// 1: Create a new member in this class
// 2: The string name of the power MUST (MUST!) MATCH THE ENUM CONSTANT!!!
//      (after all uppers and spaces translated to "_")
//      e.g. "Gold Coin" must have an enum constant GOLD_COIN, etc..
// 3: Extend karma.Reward.java

public enum KarmaReward {
	Gold_Coin(new GoldCoin()),
	Fart(new Fart()); 
    
	private String str; 
	private Reward reward;
	
	KarmaReward (Reward r) { 
		this.str = r.name; 
		this.reward = r;
	}
	
	public Reward getReward () {
		return this.reward;
	}	
	
	public String toString() {
		return this.str;
	}
	
	public static KarmaReward getRandom (boolean isGood) {
		KarmaReward[] rewards = KarmaReward.values();
		List<KarmaReward> options = new LinkedList<KarmaReward>();
		for (KarmaReward karmaReward : rewards) {
			if (karmaReward.getReward().isGood == isGood) {
				options.add(karmaReward);
			}
		}
		Collections.shuffle(options);
		return options.get(0);
	}
		
}
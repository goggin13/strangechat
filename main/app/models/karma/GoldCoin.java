package models.karma;
import java.util.Random;

import models.Utility;

import com.google.gson.reflect.TypeToken;

public class GoldCoin extends Reward {
	public final int coins;
	
	public GoldCoin () {
		super("Gold Coin", true);
        Random generator = new Random();
        coins = 1 + generator.nextInt(10);
	}

	public String toJson () {
	    return Utility.toJson(this, new TypeToken<GoldCoin>(){});
	}
		
}

package models.karma;
import java.util.Random;

import models.Utility;
import com.google.gson.reflect.TypeToken;

public class Fart extends Reward {
	public final int coins;
	
	public Fart () {
		super("Fart", false);
        Random generator = new Random();
        coins = 1 + generator.nextInt(5);
	}

	public String toJson () {
	    return Utility.toJson(this, new TypeToken<Fart>(){});
	}
}

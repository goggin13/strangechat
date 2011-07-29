package enums;
import models.*;
import models.powers.*;

// Creating a new SuperPower
// 1: Create a new member in this class
// 2: The string name of the power MUST (MUST!) MATCH THE ENUM CONSTANT!!!
//      (after all uppers and spaces translated to "_")
//      e.g. "Ice Breaker" must have an enum constant ICE_BREAKER, etc..
// 3: Extend SuperPower.java and define qualifying function for new power

public enum Power {
	ICE_BREAKER(new IceBreaker()), 
	MIND_READER(new MindReader()),
	OMNISCIENCE(new Omniscience()),
	CLONING(new Cloning()),	
	EMOTION(new Emotion()),
	X_RAY_VISION(new XRayVision());
    // BRAIN_MASH(new BrainMash()),
    // FREEZE(new Freeze()),    
    
	private String str; 
	private SuperPower sp;
	
	Power (SuperPower sp) { 
		this.str = sp.name; 
		this.sp = sp;
	}
	
	public SuperPower getSuperPower () {
		return this.sp;
	}	
	
	public String toString() {
		return this.str;
	}
		
}
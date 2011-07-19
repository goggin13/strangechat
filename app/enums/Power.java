package enums;
import models.*;

// Creating a new SuperPower
// 1: Create a new member in this class
// 2: Extend SuperPowerType.java and define qualifying function for new power

public enum Power {
	ICE_BREAKER("Ice Breaker", new IceBreaker()), 
	X_RAY_LEVEL_1("Level 1 X-Ray Vision", new XRayLevelOne());

	private String str; 
	private SuperPower sp;
	
	Power (String str, SuperPower sp) { 
		this.str = str; 
		this.sp = sp;
	}
	
	public SuperPower getSuperPower () {
		return this.sp;
	}	
	
	public String toString() {
		return this.str;
	}
		
}
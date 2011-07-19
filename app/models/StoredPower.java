
package models;
 
import java.util.*;
import javax.persistence.*;
import play.data.validation.*;
import enums.Power;
import play.db.jpa.*;
 
@Entity
public class StoredPower extends Model {
	
	@Required
	@Enumerated(EnumType.STRING)
	public Power power;	
	public boolean used;
	
	@Required
	@ManyToOne
    public User owner;
	
	public StoredPower (Power p, User u) {
		this.power = p;
		this.owner = u;
		this.used = false;
	}
	
	public String toString () {
		return power.toString();
	}
	
}



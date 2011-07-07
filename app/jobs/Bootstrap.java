package jobs;

import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
        Fixtures.deleteAll();
		if (Play.mode == Play.Mode.DEV) {
        	Fixtures.loadModels("bootstrap-data-dev.yml");
		} else {
			Fixtures.loadModels("bootstrap-data-prod.yml");
		}
    }
 
}
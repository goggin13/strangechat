package jobs;

import play.*;
import play.jobs.*;
import play.test.*;
 
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 
    public void doJob() {
        Fixtures.deleteAll();
        Fixtures.loadModels("bootstrap-data.yml");
    }
 
}
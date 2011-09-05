package jobs;

import models.powers.IceBreaker;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import play.vfs.VirtualFile;

@OnApplicationStart
public class Bootstrap extends Job {
    private final static String pathToFileDev = "conf/ice_breakers.txt";
    private final static String pathToFileProd = "data/ice_breakers.txt";
    
    public static void initIceBreakers () {
        String filePath = Play.mode == Play.Mode.DEV ? pathToFileDev : pathToFileProd;
        IceBreaker.IceBreakers.loadMessages(Play.getFile(filePath));
    }
        
    public void doJob() {
        if (Play.mode == Play.Mode.DEV) {
            Fixtures.deleteDatabase();
            Fixtures.loadModels("bootstrap-data-dev.yml");
        }
        initIceBreakers();
    }
 
}
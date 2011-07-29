package jobs;

import play.*;
import play.jobs.*;
import play.test.*;
 
import java.io.File;
import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
 	private final String pathToMasterFileDev = "conf/master.txt";
 	private final String pathToChatFileDev = "conf/chat.txt";
 	private final String pathToMasterFileProd = "data/master.txt";
 	private final String pathToChatFileProd = "data/chat.txt";

    public void doJob() {
		UserEvent.resetEventQueue();
		if (Play.mode == Play.Mode.DEV) {
		    Fixtures.deleteAll();
        	Fixtures.loadModels("bootstrap-data-dev.yml");
		} else {
            // Fixtures.deleteAll();         
            // Fixtures.loadModels("bootstrap-data-staging.yml");
            // Fixtures.loadModels("bootstrap-data-prod.yml");
		}
		
		if (Play.mode == Play.Mode.DEV) {
			Server.setMasterServer(Play.getVirtualFile(pathToMasterFileDev) != null);
			Server.setChatServer(Play.getVirtualFile(pathToChatFileDev) != null);
		} else {
			Server.setMasterServer(Play.getVirtualFile(pathToMasterFileProd) != null);
			Server.setChatServer(Play.getVirtualFile(pathToChatFileProd) != null);
		}
		Logger.info("finished bootstrapping " 
					 + (Play.mode == Play.Mode.DEV ? "development" : "production") 
					 + " server; ischat = " + Server.imAChatServer()  
					 + ", ismaster = " + Server.onMaster());
    }
 
}
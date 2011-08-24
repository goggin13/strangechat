package jobs;

import play.*;
import play.jobs.*;
import play.test.*;
import java.util.*;
import java.io.File;
import models.*;
import models.eliza.*;
import controllers.*;

@OnApplicationStart
public class Bootstrap extends Job {
 	private final String pathToMasterFileDev = "conf/master.txt";
 	private final String pathToChatFileDev = "conf/chat.txt";
 	private final String pathToMasterFileProd = "data/master.txt";
 	private final String pathToChatFileProd = "data/chat.txt";

    public void doJob() {
		if (Play.mode == Play.Mode.DEV) {
            Fixtures.deleteDatabase();
            Fixtures.loadModels("bootstrap-data-dev.yml");
		}
    }
 
}
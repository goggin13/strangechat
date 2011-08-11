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
		UserEvent.resetEventQueue();
		if (Play.mode == Play.Mode.DEV) {
            Fixtures.deleteAll();
            Fixtures.loadModels("bootstrap-data-dev.yml");
            Users.remeetEligible = -1;
		} else {
			Users.remeetEligible = 0;
            // Server master = Server.getMasterServer();
            // master.isChat = false;
            // master.volume = 0;
            // master.save();
            // 
            // Server chatter = Server.getChatServers().get(0); 
            // chatter.volume = 1.0;
            // chatter.save();
            // 
/*            List<User> users = User.all().fetch(400);
              for (User u : users) {
                  Logger.info("resetting user " + u.id);
                  u.icebreakers_seen = new TreeSet<Integer>();
                  u.save();
              }*/
            
            // List<Room> rooms = Room.all().fetch(100);
            // for (Room r : rooms) {
            //     r.delete();
            //     // if (r.isEmpty()) {
            //         // r.delete();
            //         // r.save();
            //     // }
            // }
            
            // List<StoredPower> allPowers = StoredPower.all.fetch(1500);
            // for (StoredPower p : allPowers) {
            //     
            // }
            
		}
		
		if (Play.mode == Play.Mode.DEV) {
			Server.setMasterServer(Play.getVirtualFile(pathToMasterFileDev) != null);
			Server.setChatServer(Play.getVirtualFile(pathToChatFileDev) != null);
		} else {
			Server.setMasterServer(Play.getVirtualFile(pathToMasterFileProd) != null);
			Server.setChatServer(Play.getVirtualFile(pathToChatFileProd) != null);
		}
							 
		List<Server> servers = Server.all().fetch(100);
		double totalVolume = 0;
		for(Server s : servers) {
		    totalVolume += s.volume;
		}
		if (totalVolume != 1) {
		    Logger.error("Invalid server volume assignments; expected 1.0, found " + totalVolume);
		}
		
		if (Server.onMaster()) {
		    Eliza.init();
		    Logger.info("Initialized Eliza");
		}
		
		Logger.info("finished bootstrapping " 
					 + (Play.mode == Play.Mode.DEV ? "development" : "production") 
					 + " server; ischat = " + Server.imAChatServer()  
					 + ", ismaster = " + Server.onMaster());	
    }
 
}
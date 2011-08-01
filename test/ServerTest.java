import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;
import enums.*;
import models.powers.*;

public class ServerTest extends UnitTest {
    
    @Test
    public void testAssignHeartbeatServers () {
        Fixtures.deleteAll();
        Fixtures.load("data-dummy-servers.yml");
        
        Long root_id = 500L;
        for (int i = 0; i < 100; i++) {
            User user = User.getOrCreate(root_id++);
            Server server = Server.getMyHeartbeatServer(user);
    		user.heartbeatServer = server;
    		user.save();
        }
        
        List<Server> servers = Server.find("order by volume asc").fetch(100);
        Long last = 0L;
        
        // ensure that each server was assigned more users than the last
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            Long count = User.count("byHeartbeatServer", s);
            System.out.println(count + " for " + s.name);
            assertTrue(count >= last);
            last = count;
        }
        
        Fixtures.deleteAll();
    }
}
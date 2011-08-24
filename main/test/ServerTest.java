import java.util.List;

import models.Server;
import models.User;

import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class ServerTest extends UnitTest {
    
    @Test
    public void testAssignHeartbeatServers () {
        Fixtures.deleteAll();
        Fixtures.load("data-dummy-servers.yml");
        
        long root_id = 500;
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
            assertTrue(count >= last);
            last = count;
        }
        
        Fixtures.deleteAll();
    }
}
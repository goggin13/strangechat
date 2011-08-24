package controllers;

import java.util.HashMap;

import models.Room;
import models.User;
import models.UserEvent;
import models.UserSession;
import models.Utility;
import models.eliza.Eliza;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Elizas extends Index {
    private static HashMap<String, String> convo_ids = new HashMap<String, String>();
    
    public static void reply (String qry, long room_id) {
        if (qry == null) qry = "";
        String response = Eliza.respondTo(qry);
        UserSession u = currentSession(); 
        UserSession u2 = currentForSession();
        if (u == null || u2 == null) {
            returnFailed("both from_user and user_id must map to existing users");
        }
        UserSession.Faux elizaSess = new UserSession.Faux(Eliza.user_id, "eliza_session");
        u2.sendMessage(u.toFaux(), room_id, "@Azile " + qry);
        u.sendMessage(elizaSess, room_id, response);
        u2.sendMessage(elizaSess, room_id, response);
        returnOkay();
    }
    
    /**
	 * Get a room with the requested bot; will immediately generate a join
	 * message
	 * @param botid the string identifying the bot to request */
	public static void requestBotRoom (String bot_id) {
	    User bot = User.find("byBotid", bot_id).first();
        if (bot == null) {
            returnFailed(bot_id + " does not map to a valid bot");
        }   
	    UserSession u = currentSession();        
	    UserSession botSess = new UserSession(bot, "-1");        
        Room.createBotRoomFor(u, botSess);
		returnOkay();
	}

    
    public static void talkTo (String bot_id, long bot_user_id, String qry, long room_id) {
    	UserSession.Faux sess = currentFauxSession();
        String url = "http://www.pandorabots.com/pandora/talk-xml";
        String key = sess.user_id + "_" + bot_id;
        String custid = convo_ids.get(key);
        
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("botid", bot_id);
        params.put("input", qry);
        if (custid != null) {
            params.put("custid", custid);
        }
        
        String xmlStr = Utility.fetchUrl(url, params).getString();
        String reply = xmlStr.substring(
            xmlStr.indexOf("<that>") + 6,
            xmlStr.indexOf("</that>")
        );
        
        if (custid == null) {
            custid = xmlStr.substring(
                xmlStr.indexOf("custid=\"") + 8,
                xmlStr.indexOf("\"><input")
            );
            convo_ids.put(key, custid);
        }
        
        new UserEvent.RoomMessage(bot_user_id, reply);
        returnOkay();
    }
        
}
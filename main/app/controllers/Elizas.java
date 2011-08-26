package controllers;

import java.util.HashMap;

import models.User;
import models.UserEvent;
import models.Utility;
import models.eliza.Eliza;
import models.pusher.Pusher;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Elizas extends Index {
    private static HashMap<String, String> convo_ids = new HashMap<String, String>();
    
    public static void reply (String channel, String qry) {
        if (qry == null) {
            qry = "";
        }
        System.out.println(qry);
        String response = Eliza.get().respondTo(qry);
        System.out.println(response);
        String json = new UserEvent.RoomMessage(Eliza.user_id, response).toJson();
        System.out.println(json);
        System.out.println(channel);
        new Pusher().trigger(channel, "roommessage", json);
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
//	    UserSession u = currentSession();        
//	    UserSession botSess = new UserSession(bot, "-1");        
		returnOkay();
	}

    
    public static void talkTo (String bot_id, long bot_user_id, String channel, String qry) {
        String url = "http://www.pandorabots.com/pandora/talk-xml";
        String key = channel + "_" + bot_id;
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
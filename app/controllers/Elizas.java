package controllers;

import java.util.*;
import java.lang.reflect .*;

import play.*;
import play.mvc.*;
import play.libs.*;
import play.libs.F.*;
import play.cache.Cache;
import play.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import models.*;
import java.util.concurrent.CopyOnWriteArrayList;
import play.db.jpa.GenericModel.JPAQuery;
import play.db.jpa.JPA;
import javax.persistence.Query;
import models.eliza.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

/**
 * This controller is responsible for keeping track of users, updating their
 * status in the system, assigning them rooms, passing chat requests, etc..
 */
public class Elizas extends Index {
    private static HashMap<String, Long> bot_ids = new HashMap<String, Long>();
    private static HashMap<String, String> bot_names = new HashMap<String, String>();
    private static HashMap<String, String> convo_ids = new HashMap<String, String>();
    
    static {
        bot_names.put("test", "843ad5ae9e34019a");
        bot_ids.put("test", -3L);
    }
    
    public static void reply (String qry, long from_user, long user_id, long room_id, String callback) {
        if (qry == null) qry = "";
        String response = Eliza.respondTo(qry);
        User u = User.findById(user_id);
        User u2 = User.findById(from_user);
        if (u == null || u2 == null) {
            returnFailed("both from_user and user_id must map to existing users", callback);
        }
        u.sendMessage(from_user, room_id, "@Eliza " + qry);
        u.sendMessage(Eliza.user_id, room_id, response);
        u2.sendMessage(Eliza.user_id, room_id, response);
        returnOkay(callback);
    }
    
    
    
    /**
	 * Get a room with the requested bot; will immediately generate a join
	 * message
	 * @param user_id your user_id
	 * @param botid the string identifying the bot to request
	 * @param callback optional JSONP callback*/
	public static void requestBotRoom (long user_id, String bot_name, String callback) {
	    bot_name = bot_name.toLowerCase();
        if (!bot_ids.containsKey(bot_name)) {
            returnFailed(bot_name + " is not a valid bot id", callback);
        }	   
        Long bot_id = bot_ids.get(bot_name);
        User bot = User.find("byUser_id", bot_id).first(); 
        if (bot == null) {
            returnFailed(bot_id + " does not map to a valid bot id", callback);
        }
        Room.createBotRoomFor(user_id, bot);
		returnOkay(callback);
	}
        
    public static void talkTo (String bot_name, String qry, long user_id, long room_id, String callback) {
        User u = User.findById(user_id);
        if (u == null) {
            returnFailed("user_id must map to an existing user", callback);
        }
        bot_name = bot_name.toLowerCase();
        if (!bot_names.containsKey(bot_name)) {
            returnFailed(bot_name + " is not a valid bot id", callback);
        }
        
        String url = "http://www.pandorabots.com/pandora/talk-xml";
        String key = u.id + "_" + bot_name;
        String custid = convo_ids.get(key);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("botid", bot_names.get(bot_name));
        params.put("input", qry);
        if (custid != null) {
            params.put("custid", custid);
            System.out.println("CUSTID SET");
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
        u.sendMessage(bot_ids.get(bot_name), room_id, reply);
        returnOkay(callback);
    }
        
}
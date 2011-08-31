package controllers;

import models.UserEvent;
import models.UserSession;
import models.pusher.BasicUserInfo;
import models.pusher.PresenceChannelData;
import models.pusher.Pusher;
import play.Play;
import play.data.validation.Required;

/**
 * This controller is in charge of pushing events into the event streams
 * for the individual chat servers, as well as providing an interface for
 * clients to listen to. */  
public class Notify extends Index {
	
	public static void pusherAuth (String socket_id, String channel_name) {
	    Pusher pusher = new Pusher();
        UserSession userSession = UserSession.find("bySocket", socket_id).first();
        if (userSession == null) {
            returnFailed("pusherAuth failed, no valid user session passed");
        }
        BasicUserInfo userInfo = new BasicUserInfo(
            userSession.user.id,
            userSession.user.alias, 
            userSession.user.avatar
        );        
        PresenceChannelData channelData = new PresenceChannelData(userSession.session, userInfo);
        String auth = pusher.createAuthString(socket_id, channel_name, channelData);
        renderJSONP(
            auth,
            null
        );
	}

	public static void push (
	                @Required String channel, 
	                @Required String event, 
	                @Required String message, 
	                String socket_id,
	                String callback) 
	{
        if (validation.hasErrors()) {
            returnFailed(validation.errors());
        }	    
	    Pusher pusher = new Pusher();
	    if (socket_id != null) {
	        pusher.trigger(channel, event, message, socket_id);
	    } else {
	        pusher.trigger(channel, event, message);
	    }
	    UserEvent.Event ue = UserEvent.deserializeEvent(message);
	    if (ue != null) {
	        UserEvent.get().publish(ue);
	    } 
	    returnOkay();
	}
	
	public static void setMySocket (String socket_id) {
	    UserSession sess = currentSession();
	    if (sess == null) {
	        returnFailed("Need valid session to set socket id");
	    }
	    sess.socket = socket_id;
	    sess.save();
	    returnOkay();
	}
		
}
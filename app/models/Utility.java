package models;
 
import play.libs.WS;
import org.w3c.dom.Document;
import java.net.*;
import java.io.*;
import java.util.Collection;
import java.util.*;
import java.text.Format;
import com.google.gson.*;
import play.cache.Cache;
import java.text.SimpleDateFormat;

/**
 * A collection of random, static utility functions */
public class Utility {
	
	/**
	 * Get the response from <code>url</code> as a JSON string
	 * @param url the url to hit
     * @param params to be added the request
	 * @return a response object from the given url
	 */
	public static WS.HttpResponse fetchUrl (String url, HashMap<String, Object> params) {
		System.out.println(url);
		WS.HttpResponse resp = 
				WS.url(url)
			   .params(params)
			   .setHeader("content-type", "application/json")
			   .get();
		return resp;
	}
	
	/**
	 * Get the response from <code>url</code> as a JSON string, posting a request with
	 * <code>params</code>
	 * @param url the url to hit
     * @param params to be added the request
	 * @return a response object from the given url
	 */
	public static WS.HttpResponse fetchPostUrl (String url, HashMap<String, Object> params) {
		WS.HttpResponse resp = 
				WS.url(url)
			   .params(params)
			   .setHeader("content-type", "application/json")
			   .post();
		return resp;
	}
	
	/**
	 * Get the facebook friends for the given user; used as a helper for the login function
	 * @param facebook_id
	 * @param access_token an up to date access_token for logging into the facebook API
	 * @return a json object containing all the users facebook friends; this object should
	 * 		   be checked for presence of an "error" element indicating there was an issue 
	 *		   contacting the facebook API
	 */	
	public static JsonObject getMyFacebookFriends (Long facebook_id, String access_token) {
		String cacheKey = "friends_" + facebook_id;
		JsonObject friends = Cache.get(cacheKey, JsonObject.class);
		if (friends == null) {
			String url = "https://graph.facebook.com/" + facebook_id + "/friends";
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("access_token", access_token);
			WS.HttpResponse resp = Utility.fetchUrl(url, params);
			friends = resp.getJson().getAsJsonObject();
			// Cache.set(cacheKey, friends, "30mn");
		}
		return friends;		
	}
	
	/**
	 * Calculate the difference between 2 dates in seconds 
	 * @param d1 the first date
	 * @param d2 the second date
	 * @return number of seconds <code>d2</code> is greater than 
	 * 		   <code>d1</code> */
	public static Long diffInSecs (Date d1, Date d2) {
		Long diff = d1.getTime() - d2.getTime();
		diff = diff / 1000;
		return diff; 
	}
	
	/**
	 * A formatted string of <code>date</code>
	 * @param date the date to format
	 * @return a string of the form "HH:mm:ss a" */
	public static String formattedTime (Date date) {
	 	Format formatter = new SimpleDateFormat("HH:mm:ss a");
		String str = formatter.format(date);
		return str;
	}
	
}
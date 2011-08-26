package models;
 
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;

import play.cache.Cache;
import play.libs.WS;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


/**
 * A collection of random, static utility functions */
public class Utility {
	
	
	/**
	 * Get the response from <code>url</code> as a JSON string
	 * @param url the url to hit
     * @param params to be added the request
	 * @return a response object from the given url
	 */
	public static WS.HttpResponse fetchUrl (String url, HashMap<String, String> params) {
		System.out.println("GET " + url);
		HashMap<String, Object> objs = new HashMap<String, Object>();
		for (String s : params.keySet()) {
		    objs.put(s, (Object)params.get(s));
		}
		WS.HttpResponse resp = 
				WS.url(url)
			   .params(objs)
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
	public static WS.HttpResponse fetchPostUrl (String url, HashMap<String, String> params) {
		System.out.println("POST " + url);
		HashMap<String, Object> objs = new HashMap<String, Object>();
		for (String s : params.keySet()) {
		    objs.put(s, (Object)params.get(s));
		}		
		WS.HttpResponse resp = 
				WS.url(url)
			   .params(objs)
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
			HashMap<String, String> params = new HashMap<String, String>();
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
	public static long diffInSecs (Date d1, Date d2) {
		long diff = d1.getTime() - d2.getTime();
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
	
	public static String toJson (Object obj, TypeToken t) {
        Gson gson = new GsonBuilder()
                        .setExclusionStrategies(new User.ChatExclusionStrategy())
                        .create();
        return gson.toJson(obj, t.getType());
	}
	
	/** 
	 * the MD5 hash of a string 
	 * @param str
	 * @return the hashed string */
	public static String md5 (String str) {
	    return DigestUtils.md5Hex(str);
	}
	
	/**
	 * UnixTime */
    public static long time () {
        long unixTime = System.currentTimeMillis() / 1000L;
        return unixTime;
    }
	
}
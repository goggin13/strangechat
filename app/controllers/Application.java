package controllers;

import play.*;
import play.mvc.*;
import play.libs.WS;

/**
 * Demo page and home page, which is blank for now */
public class Application extends Index {

	// leave this for now, could be a good SO question;
	// public static void aResponse () {
	// 	renderText("hello world");
	// }
	// 
	// public static void testSelfRequest () {
	// 	String url =  "localhost:9000/application/aresponse";
	// 	
	// 	// WS.HttpResponse resp = WS.url(url)
	// 		   				   // .setHeader("content-type", "text/plain")
	// 		   				   // .get();
	// 	// ControllerInstrumentation.initActionCall();
	// 	// Application.aResponse();
	// 
	// 	renderText(currentRequest().host);
	// }

    public static void index() {
        render();
    }

	public static void demo () {
		render();
	}
	
}
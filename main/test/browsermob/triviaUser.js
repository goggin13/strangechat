var selenium = browserMob.openBrowser();
var c = browserMob.getActiveHttpClient();

c.blacklistRequests("http(s)?://www\\.google-analytics\\.com/.*", 200);
c.blacklistRequests("http://.*\\.quantserve.com/.*", 200);
c.blacklistRequests("http://www\\.quantcast.com/.*", 200);
c.blacklistRequests("http://c\\.compete\\.com/.*", 200);
c.blacklistRequests("http(s)?://s?b\\.scorecardresearch\\.com/.*", 200);
c.blacklistRequests("http://s\\.stats\\.wordpress\\.com/.*", 200);
c.blacklistRequests("http://partner\\.googleadservices\\.com/.*", 200);
c.blacklistRequests("http://ad\\.adtegrity\\.net/.*", 200);
c.blacklistRequests("http://ad\\.doubleclick\\.net/.*", 200);
c.blacklistRequests("http(s)?://connect\\.facebook\\.net/.*", 200);
c.blacklistRequests("http://platform\\.twitter\\.com/.*", 200);
c.blacklistRequests("http://.*\\.addthis\\.com/.*", 200);
c.blacklistRequests("http://widgets\\.digg\\.com/.*", 200);
c.blacklistRequests("http://www\\.google\\.com/buzz/.*", 200);
c.blacklistRequests("http://www\\.google\\.com/buzz/.*", 200);
c.blacklistRequests("http://static\\.chartbeat\\.com.*", 200);
c.blacklistRequests("http://api\\.mixpanel\\.com.*", 200);

var timeout = 30000;
var icebreaker_timeout = 4000;
var NUM_ITERS = 20;
var MESSAGE_PAUSE_TIME = 1000;

selenium.setTimeout(timeout);

var tx = browserMob.beginTransaction();
var step = browserMob.beginStep("Step 1");
var sendChat = function (msg) {
  selenium.pause(MESSAGE_PAUSE_TIME);  
  selenium.type("css=.chat_input", msg);
  selenium.keyDown("css=.chat_input", "\\13");    
}

selenium.openAndWait("http://superheroclubhouse.com/staging");
selenium.waitForTextPresent("Create your super secret identity");

selenium.type("id=super_hero_name", "Tester");  

selenium.click("id=button_startchat");
selenium.waitForTextPresent("Looking for trouble");

selenium.openAndWait("http://superheroclubhouse.com/staging/chat?triviaGroup=movies");
selenium.waitForTextPresent("Welcome to movie trivia.");

sendChat("notmovie");
selenium.waitForTextPresent("Try again");

selenium.click("css=.restart_chat.fake_link");
selenium.waitForTextPresent("Welcome to movie trivia.");
sendChat("movies");

browserMob.endStep();
browserMob.endTransaction();
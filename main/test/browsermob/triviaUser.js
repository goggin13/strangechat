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
selenium.waitForTextPresent("Times are tough.");

selenium.openAndWait("http://superheroclubhouse.com/staging/trivia");
selenium.waitForTextPresent("Trivia battle!");
selenium.click("id=Movies");
selenium.waitForTextPresent("When you are ready, just type ");

sendChat("notplay");
selenium.waitForTextPresent("Try again");

selenium.click("css=.restart_chat.fake_link");
selenium.waitForTextPresent("When you are ready, just type ");
sendChat("play");

var countIcebreakers = function () {
  return parseInt(selenium.getXpathCount(xPath), 10);
}

var answerQuestion = function () {
  selenium.pause(MESSAGE_PAUSE_TIME);
  sendChat("a");
  var xPath = '//img[@alt="Movies"]';  
  var nextCount = countIcebreakers() + 1;
  var code = "countIcebreakers() >= nextCount";
  waitForCondition(code);
  // selenium.waitForXpathCount(xPath, nextCount);  
}

for (var i = 0; i < 10 ; i++) {
  answerQuestion();
}

selenium.waitForTextPresent("Round Completed!");

browserMob.endStep();
browserMob.endTransaction();
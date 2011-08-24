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

var timeout = 30000;
var icebreaker_timeout = 4000;
var NUM_ITERS = 50;

selenium.setTimeout(timeout);

var tx = browserMob.beginTransaction();
var step = browserMob.beginStep("Step 1");

selenium.open("http://superheroclubhouse.com/staging");
selenium.waitForTextPresent("Create your super secret identity");

selenium.click("id=button_startchat");
selenium.waitForTextPresent("Enters your view");

var myName = selenium.getText("id=your_name");
var theirName = selenium.getText("css=.message:first-child .name");

var timestamp = function () {
  return Math.round((new Date()).getTime() / 1000);
}

var sendChat = function (msg) {
  selenium.type("css=.chat_input", msg);
  selenium.keyDown("css=.chat_input", "\\13");    
}

var iceBreakerTimer = -1;
var sendIceBreaker = function () {
  var xPath = '//img[@alt="IceBreaker"]';  
  var curCount = parseInt(selenium.getXpathCount(xPath), 10);
  var nextCount = curCount + 1;
  var testScript = "selenium.getXpathCount('" + xPath +"') >= " + nextCount;
  selenium.click("css=.chatting .ice_breaker");
  // selenium.waitForCondition(testScript, icebreaker_timeout);
  // selenium.waitForXpathCount(xPath, nextCount);  
  iceBreakerTimer = timestamp();
}

sendChat("hello " + theirName + ", " + " my name is " + myName);

sendIceBreaker();

for (i = 1; i < NUM_ITERS; i++) {  
  sendChat(myName + " hello" + i);    
  selenium.waitForTextPresent(theirName + " hello" + i);
  var elapsedTime = timestamp() - iceBreakerTimer;
  browserMob.log("------------------------------------------------------------------------");
  browserMob.log("elapsed = " + elapsedTime);
  browserMob.log("------------------------------------------------------------------------");  
  if (elapsedTime > 17) {
    sendIceBreaker();
  }
}

browserMob.endStep();
browserMob.endTransaction();
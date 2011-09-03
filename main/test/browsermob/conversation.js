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
var NUM_ITERS = 25;
var MESSAGE_PAUSE_TIME = 1000;

selenium.setTimeout(timeout);

var tx = browserMob.beginTransaction();
var step = browserMob.beginStep("Step 1");

selenium.open("http://superheroclubhouse.com/staging");
selenium.waitForTextPresent("Create your super secret identity");

var layers = [
  'tshirt_choose_color', 
  'background_choose_color', 
  'shirt_choose_color',
  'sweatband_choose_color',
  'gloves_choose_color',
  'wristbands_choose_color',
  'mask_layer_choose_color',
  'cape_choose_color',
];

var flipCoin = function (odds) {
  return Math.floor(Math.random() * 10) % odds == 0;
};

var pause = function () {
  selenium.pause(MESSAGE_PAUSE_TIME);
};

var timestamp = function () {
  return Math.round((new Date()).getTime() / 1000);
};

var sendChat = function (msg) {
  pause();
  selenium.type("css=.chat_input", msg);
  selenium.keyDown("css=.chat_input", "\\13");    
};

var iceBreakerTimer = -1;
var sendIceBreaker = function () {
  pause();
  var xPath = '//img[@alt="IceBreaker"]';  
  var curCount = parseInt(selenium.getXpathCount(xPath), 10);
  var nextCount = curCount + 1;
  selenium.click("css=.chatting .ice_breaker");
  // selenium.waitForXpathCount(xPath, nextCount);  
  iceBreakerTimer = timestamp();
};


if (flipCoin(2)) {
  selenium.click("css=#gender_chooser .square:last");
}

for (var i = 0; i < layers.length; i++) {
  var r = Math.floor(1 + Math.random() * 8);
  selenium.click("css=#" + layers[i] +" .color.box_" + r);
}

selenium.click("id=button_startchat");
selenium.waitForTextPresent("Enters your view");

var myName = selenium.getText("id=your_name");
var theirName = selenium.getText("css=.message:first-child .name");

sendChat("hello " + theirName + ", " + " my name is " + myName);

if (flipCoin(5)) {
  sendIceBreaker();  
} else {
  iceBreakerTimer = timestamp() - 17;
}

for (i = 1; i < NUM_ITERS; i++) {  
  sendChat(myName + " hello" + i);    
  selenium.waitForTextPresent(theirName + " hello" + i);
  var elapsedTime = timestamp() - iceBreakerTimer;
  if (elapsedTime > 17 && i < NUM_ITERS - 2 && flipCoin(3)) {
    sendIceBreaker();
  }
}

pause();

browserMob.endStep();
browserMob.endTransaction();
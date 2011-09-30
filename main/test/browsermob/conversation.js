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
var NUM_ITERS = 10;
var MESSAGE_PAUSE_TIME = 1000;

selenium.setTimeout(timeout);

var tx = browserMob.beginTransaction();
var step = browserMob.beginStep("Step 1");

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

countIcebreakers = function () {
  var xPath = '//img[@alt="IceBreaker"]';
  return parseInt(selenium.getXpathCount(xPath), 10);
}

var iceBreakerTimer = -1;
var sentBefore = false;
var sendIceBreaker = function () {
  pause();
  selenium.click("css=.chatting .ice_breaker");
  var nextCount = countIcebreakers() + 1;
  var JS = "parseInt(selenium.getXpathCount(\"//img[@alt='IceBreaker']\"), 10) >= " + nextCount;
  selenium.waitForCondition(JS, 10000);
  iceBreakerTimer = timestamp();
};

var sendKarma = function () {
  pause();
  var xPath = '//img[@alt="Karma"]';  
  var nextCount = parseInt(selenium.getXpathCount(xPath), 10) + 1;
  selenium.click("css=.chatting .karma");
  
  selenium.waitForElementPresent("css=#yes_splash_option");
  selenium.click("css=#yes_splash_option");
  
  var JS = "parseInt(selenium.getXpathCount(\"//img[@alt='Karma']\"), 10) >= " + nextCount;
  selenium.waitForCondition(JS, 10000);  
};


/****************************************
 * TEST BODY
 *****************************************/

selenium.open("http://superheroclubhouse.com/staging");
selenium.waitForTextPresent("Edit Costume");

selenium.click("css=.edit_costume");
selenium.waitForTextPresent("Superhero Name");

if (flipCoin(2)) {
  selenium.click("css=#gender_chooser .item:last");
}

// for (var i = 0; i < layers.length; i++) {
//   var r = Math.floor(1 + Math.random() * 8);
//   selenium.click("css=#" + layers[i] +" .item.box_" + r);
// }

selenium.click("css=.chat_random");
selenium.waitForTextPresent("Enters your view");

var myName = selenium.getText("id=your_name");
var theirName = selenium.getText("css=.message:first-child .name");

sendChat("hello " + theirName + ", " + " my name is " + myName);

if (flipCoin(5)) {
  sendIceBreaker();  
} else {
  iceBreakerTimer = timestamp() - 17;
}

sendIceBreaker();
for (i = 1; i < NUM_ITERS; i++) {  
  sendChat(myName + " hello" + i);    
  selenium.waitForTextPresent(theirName + " hello" + i);
  var elapsedTime = timestamp() - iceBreakerTimer;
  if (i < NUM_ITERS - 2) {
    if (elapsedTime > 17) {
      sendIceBreaker();
    } else if (flipCoin(4)) {
      sendKarma();
    }    
  }
}

pause();

browserMob.endStep();
browserMob.endTransaction();
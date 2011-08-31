var user1 = {
		user_id: 999,
		avatar: "http://bnter.com/web/assets/images/8996__w320_h320.jpg",
		alias: "SuperMan"
	}, user2 = {
		user_id: 998,
		avatar: "http://bnter.com/web/assets/images/17169__w184_h184.png",
		alias: "Lex Luther"		
	}, user3 = {
		user_id: 997,
		avatar: "http://bnter.com/web/assets/images/13616__w320_h320.jpg",
		alias: "Mr.T"		
	}, user4 = {
	  user_id: 996,
	  avatar: "http://bnter.com/web/assets/images/13616__w320_h320.jpg",
	  alias: "AnotherGuy"		
  };
  
var beforeEachFunction = function () {
  var isDone = false;
   $.getJSON("http://localhost:9000/mock/init", function () {
     isDone = true;
   });
   waitsFor(function () {
     return isDone;
   }, "reset failed", 1000);
   runs(function () {
     var testLogin = function (JSON, user) {
       var me = JSON[user.user_id];
       expect(me).toBeDefined();
       expect(me.alias).toEqual(user.alias);
       expect(me.avatar).toEqual(user.avatar);
     },
     loggedIn = 0;
     user1["api"] = ChatAPI(user1.user_id, user1.avatar, user1.alias, function (JSON) {
       testLogin(JSON, user1);
       loggedIn++;
       $.each(JSON, function (k, v) {
          user1["powers"] = v.superPowers;
        });  
     });
     user2["api"] = ChatAPI(user2.user_id, user2.avatar, user2.alias, function (JSON) {
       testLogin(JSON, user2);
       loggedIn++;
     });
     user3["api"] = ChatAPI(user3.user_id, user3.avatar, user3.alias, function (JSON) {
       testLogin(JSON, user3);
       loggedIn++;
     });  
     user4["api"] = ChatAPI(user4.user_id, user4.avatar, user4.alias, function (JSON) {
       testLogin(JSON, user4);
       loggedIn++;
     });         
     user1.api.hideSocketID();
     user2.api.hideSocketID();
     user3.api.hideSocketID();
     user4.api.hideSocketID();
     waitsFor(function () { 
       return loggedIn == 4;
     }, "waiting for users to log in", 4000);
   });
};

var afterEachFunction = function () {
  var user4Data = user3.api.im_talking_to[user4.api.user_id],
     user3Data = user4.api.im_talking_to[user3.api.user_id],
     dataList = [user4Data, user3Data],
     apiList = [user1.api, user2.api, user3.api, user4.api];
   $.each(dataList, function (k, data) {
     if (data && data.channel) {
       data.channel.bindToMessage(function (message) {});
       data.channel.bindToIsTyping(function (message) {});
       data.channel.bindToUsedPower(function (power) {});
     }
   });
   $.each(apiList, function (k, api) {
     api.bindNewPower(function (power) {});
     api.logout();
   });
};
describe('PushFunctions', function () {
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

  beforeEach(function () {
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
       waitsFor(function () { 
         return loggedIn == 4;
       }, "waiting for users to log in", 4000);
     });
   });

  afterEach(function () {
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
    });
  });
  
  describe("get a room", function () {
    it('should pair two users', function () {     
      var msg1 = false, msg2 = false, msg3 = false, msg4 = false;
      
      user1.api.requestRandomRoom(function (user) {
        expect(user.avatar).toEqual(user2.avatar);
        expect(user.alias).toEqual(user2.alias);        
        msg1 = true;
      });
      user2.api.requestRandomRoom(function (user) {
        expect(user.avatar).toEqual(user1.avatar);
        expect(user.alias).toEqual(user1.alias);        
        msg2 = true;
      });
      
      waitsFor(function () {
        return msg1 && msg2;
      }, "waiting to get joined", 4000);
      
      runs(function () {
        var user2Data = user1.api.im_talking_to[user2.api.user_id],
          user1Data = user2.api.im_talking_to[user1.api.user_id];
        expect(user1Data).toBeDefined();
        expect(user2Data).toBeDefined();        
        
        user3.api.requestRandomRoom(function () {
          msg3 = true;
        });
        
        waits(1000);
        runs(function () {
          
          expect(msg3).toEqual(false);
          user4.api.requestRandomRoom(function () {
            msg4 = true;
          });
          
          waitsFor(function () {
            return msg3 && msg4;
          }, "second set to join", 4000);
          runs(function () {
            var user3Data = user4.api.im_talking_to[user3.api.user_id],
              user4Data = user3.api.im_talking_to[user4.api.user_id];
            expect(user3Data).toBeDefined();
            expect(user4Data).toBeDefined();        
          });
        });
      });
		});
  });

  describe("sending events", function () {
    it('other user should receive message', function () {
      var msg1 = false, msg2 = false;
      
      user3.api.requestRandomRoom(function (user) { 
        msg1 = true; 
        expect(user.user_id).toEqual(user4.api.user_id);
      });
      user4.api.requestRandomRoom(function (user) { 
        msg2 = true; 
        expect(user.user_id).toEqual(user3.api.user_id);        
      });
      
      waitsFor(function () {
        return msg1 && msg2;
      }, "get matched up", 4000);
      
      runs(function () {
        var user4Data = user3.api.im_talking_to[user4.api.user_id],
          user3Data = user4.api.im_talking_to[user3.api.user_id],
          user3channel = user3Data.channel,
          user4channel = user4Data.channel,
          messageCount = 0,
          power_id = user4.api.superPowers[0].id,
          mh = function (message, from_user, from_user_lbl, type) {
            expect(message.type).toEqual(type);            
            expect(message.from).toEqual(from_user);
            expect(message.text).toEqual("hello from " + from_user_lbl);
            messageCount++;
          },
          ph = function (power) {
            messageCount++;
          };
        // user3channel.bindLogin(function () { loggedInCount++ });
        // user4channel.bindLogin(function () { loggedInCount++ });        
        user3channel.bindToMessage(function (message) {
          console.debug("MH " + 1);
          mh(message, user4.api.user_id, "4", "roommessage");              
        });
        user4channel.bindToMessage(function (message) {
          console.debug("MH " + 2);          
          mh(message, user3.api.user_id, "3", "roommessage");               
        });       
        user3channel.bindToIsTyping(function (message) {
          console.debug("MH " + 3);          
          mh(message, user4.api.user_id, "4", "useristyping");              
        });
        user4channel.bindToIsTyping(function (message) {
          console.debug("MH " + 4);          
          mh(message, user3.api.user_id, "3", "useristyping");               
        });
        user3channel.bindToUsedPower(function (power) {
          console.debug("MH " + 5);  
          ph();
        });
        user4channel.bindToUsedPower(function (power) {
          console.debug("MH " + 6);          
          ph();
        });    
        
        user3channel.message(user3.api.user_id, "hello from 3");
        user4channel.message(user4.api.user_id, "hello from 4");        
        user3channel.userIsTyping(user3.api.user_id, "hello from 3");
        user4channel.userIsTyping(user4.api.user_id, "hello from 4");
        user4channel.usePower(power_id, user4.api.user);   
        
        waitsFor(function () {
          return messageCount == 6;
        }, "messages to go through", 4000);                 
      });   
    });
  });  
  
  describe("qualify for mind reader", function () {
    it("should occur after using 3 icebreakers", function () {
      var msg1 = false, msg2 = false;
      
      user3.api.requestRandomRoom(function (user) { msg1 = true; });
      user4.api.requestRandomRoom(function (user) { msg2 = true; });
      
      waitsFor(function () { return msg1 && msg2; }, "get matched up", 4000);
      
      runs(function () {
        var user4Data = user3.api.im_talking_to[user4.api.user_id],
          user4channel = user4Data.channel,
          power_id = user4.api.superPowers[0].id,
          gotIt = false;
          
        for (var i = 0; i < 3; i++) {
          user4channel.usePower(power_id, user4.api.user);
        }
        
        user4.api.bindNewPower(function (power) {
          if (power.superPower.name == "Mind Reader") {          
            expect(power.storedPower.id).toBeDefined();
            expect(power.superPower.name).toEqual("Mind Reader");
            expect(power.storedPower.level).toEqual(1);
            gotIt = true;
          }
        });
        
        user4.api.checkPowers();
        waitsFor(function () {
          return gotIt;
        }, "receiving mind reader", 12000);
      });
    });
  });
  
  describe("qualify for gold coins", function () {
    it("should occur after chatting for 100 seconds", function () {
      var msg = 0;
      
      user3.api.requestRandomRoom(function (user) { msg++; });
      user4.api.requestRandomRoom(function (user) { msg++; });
      
      waitsFor(function () { return msg == 2; }, "get matched up", 4000);
      
      runs(function () {
        var user4Data = user3.api.im_talking_to[user4.api.user_id],
          user3Data = user4.api.im_talking_to[user3.api.user_id],
          user3channel = user3Data.channel,
          user4channel = user4Data.channel,
          power_id = user4.api.superPowers[0].id,
          gotIt = false;
          
        user3channel.bindToIsTyping(function () {});  
        user4channel.bindToIsTyping(function () {});  
        for (var i = 0; i < 20; i++) {
          user4channel.userIsTyping(user4.api.user_id, "hello from 4");
        }
        
        user4.api.bindNewPower(function (power) {
          if (power.superPower.name == "Gold Coin") {
            expect(power.storedPower.id).toBeDefined();
            expect(power.storedPower.level).toEqual(1);
            expect(power.storedPower.available).toEqual(1);            
            gotIt = true;            
          }
        });
        
        user4.api.checkPowers();
        waitsFor(function () {
          return gotIt;
        }, "receiving a gold coin", 5000);

      });
    });
  });
  
});

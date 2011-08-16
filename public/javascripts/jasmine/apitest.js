
describe('ChatApi', function () {
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
		};

  describe("logging in", function () {
    it('Will log you in', function () {
     var testLogin = function (JSON, user) {
       var me;
       $.each(JSON, function (k, v) {
         me = v;
         me["powers"] = v.superPowers;
       }); 
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
     waitsFor(function () { 
       return loggedIn == 3;
     }, "waiting for users to log in", 2000);     
    });
  });
  
  describe("Joining a room", function () {
    it('should match two users', function () {
      var msg1 = false,
        msg2 = false,
        checkJoinFor = function (JSON, user_id, avatar, alias, isUser1) {
          var hasJoin = false;
          $.each(JSON, function (k, val) {
            if (val.data.type == "join") {
              if (isUser1) {
                user1["room_id"] = val.data.room_id;
              } else {
                user2["room_id"] = val.data.room_id;     
              }
              expect(val.data.new_user).toEqual(user_id);
              expect(val.data.avatar).toEqual(avatar);
              expect(val.data.alias).toEqual(alias); 
              hasJoin = true;  
            }
          });
          expect(hasJoin).toEqual(true);
        };
        mh1 = function (JSON) {           
          checkJoinFor(JSON, user2.api.user_id, user2.avatar, user2.alias, true);
          user1.api.resetMessageHandler();
          msg1 =  true;
        },
        mh2 = function (JSON) {
          checkJoinFor(JSON, user1.api.user_id, user1.avatar, user1.alias, false);
          user2.api.resetMessageHandler();
          msg2 =  true
        };
      
      user1.api.registerMessageHandler(mh1);
      user2.api.registerMessageHandler(mh2);
      user1.api.requestRandomRoom();
      user2.api.requestRandomRoom();
      
      waitsFor(function () { 
        return msg1 && msg2;
      }, "Join rooms timed out", 3000);
      
      runs(function () {
        expect(user1.room_id).toBeDefined();
        expect(user2.room_id).toBeDefined();        
        expect(user1.room_id).toEqual(user2.room_id);
      });
    });
  });    

  describe("Using powers", function () {
    it('should notify the other user', function () {
      var received = false,
        mh2 = function (JSON) {           
          user2.api.resetMessageHandler();
          var hasEvent = false;
          $.each(JSON, function (k, val) {
            if (val.data.type == "usedpower") {
              expect(val.data.by_user).toEqual(user1.api.user_id);
              expect(val.data.superPower.name).toEqual("Ice Breaker");
              expect(val.data.room_id).toEqual(user1.room_id);   
              hasEvent = true;           
            }
          });
          expect(hasEvent).toEqual(true);                   
          received =  true;
        };
      user2.api.registerMessageHandler(mh2);
      $.each(user1.powers, function (i, power) {
        if (power.power == "ICE_BREAKER") {
          user1.api.usePower(power.id, user2.api.user_id, user1.room_id);
        }
      });
      
      waitsFor(function () { 
       return received;
      }, "user2 to power notification", 3000);      
    });
  });  

  describe("Sending a message", function () {
    it('Other user should get it', function () {
       var isDone = false,
         mh2 = function (JSON) {
           var hasMsg = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "roommessage") {
               expect(val.data.from).toEqual(user1.api.user_id);
               expect(val.data.text).toEqual("test message");
               expect(val.data.room_id).toEqual(user1.room_id);   
               hasMsg = true;           
             }
             expect(hasMsg).toEqual(true);
           });        
           isDone = true;
         };
       user2.api.registerMessageHandler(mh2);
       user1.api.roomMessage(user2.api.user_id, "test message", user1.room_id);
       
       waitsFor(function () { 
        return isDone;
       }, "user2 to get message", 3000);        
    });
  });

  describe("Leaving", function () {
    it('Other user should get a notification', function () {
       var isDone = false,
         mh2 = function (JSON) {
           var hasLeave = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "leave") {
               expect(val.data.left_user).toEqual(user1.api.user_id);
               expect(val.data.room_id).toEqual(user1.room_id);   
               hasLeave = true;           
             }
             expect(hasLeave).toEqual(true);
           });        
           isDone = true;
         };
         
       user2.api.registerMessageHandler(mh2);
       user1.api.leaveRoom(user1.room_id);
       
       waitsFor(function () { 
        return isDone;
       }, "user2 to get notification user1 left", 3000);        
    });
  });

  describe("Watch for useristyping", function () {
    it('Other user should get a notification when I type', function () {
       var isDone = false,
         mh2 = function (JSON) {
           var hasEvent = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "useristyping") {
               expect(val.data.typing_user).toEqual(user1.api.user_id);
               expect(val.data.room_id).toEqual(user1.room_id);   
               expect(val.data.text).toEqual("hello world");   
               hasEvent = true;           
             }
             expect(hasEvent).toEqual(true);
           });        
           isDone = true;
         };
         
       user2.api.registerMessageHandler(mh2);
       var input = $("<input type='text' id='" + user2.api.user_id + "' room_id='" + user1.room_id +"'/>");
       user1.api.watchInput(input);
       input.val("hello world");
       
       waitsFor(function () { 
        return isDone;
       }, "user2 to get notification user1 is typing", 3000);        
    });
  });

  describe("talking to Azile", function () {
    it('We should both get messages back from Azile', function () {
       var isDone1 = false,
         isDone2 = false,
         checkResponse = function (JSON) {
           var hasEvent = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "roommessage") {
               expect(val.data.from == "-2" 
                      || val.data.from == user1.api.user_id).toBeTruthy();
               expect(val.data.text.length > 0).toBeTruthy();
               expect(val.data.room_id).toEqual(user1.room_id);   
               hasEvent = true;           
             }
             expect(hasEvent).toEqual(true);
           });         
         };
        
       user1.api.registerMessageHandler(function (JSON) {
         checkResponse(JSON);
         isDone1 = true;
       });   
       user2.api.registerMessageHandler(function (JSON) {
         checkResponse(JSON);
         isDone2 = true;         
       });
       
       user1.api.eliza(user2.api.user_id, user1.room_id, "whats up gurl");
       
       waitsFor(function () { 
        return isDone1 && isDone2;
       }, "users to hear back from Azile", 3000); 
       runs(function () {
         user1.api.resetMessageHandler();
         user2.api.resetMessageHandler();         
       });       
    });
  });

  describe("talking to a bot", function () {
    it('We should back messages from the bot', function () {
       var bot_id = "843ad5ae9e34019a",
         room_id = false,
         bot_user_id = false,
         joined = false,
         mh1 = function (JSON) {           
           user1.api.resetMessageHandler();
           var hasJoin = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "join") {
               room_id = val.data.room_id;
               bot_user_id = val.data.new_user;
               expect(val.data.avatar).toEqual("http://bnter.com/web/assets/images/4571__w320_h320.png");
               expect(val.data.alias).toEqual("Test"); 
               hasJoin = true;  
             }
           });   
           expect(hasJoin).toEqual(true);        
           joined = true;
         };
       user1.api.registerMessageHandler(mh1);       
       user1.api.requestBotRoom(bot_id);
       
       waitsFor(function () {
         return joined;
       }, "users to join room with bot", 3000);
       
       runs(function () {
         var gotMsg = false;
         user1.api.registerMessageHandler(function (JSON) {
           user1.api.resetMessageHandler();
           var hasMsg = false;
            $.each(JSON, function (k, val) {
              if (val.data.type == "roommessage") {
                expect(val.data.room_id).toEqual(room_id);
                expect(val.data.from).toEqual(bot_user_id);
                hasMsg = true;  
              }
            });   
            expect(hasMsg).toEqual(true);        
            gotMsg = true;
         });
         user1.api.talkToBot(bot_user_id, bot_id, "whats up bot", room_id);
         waitsFor(function () {
           return gotMsg;
         }, "user to receive message from bot", 3000);
       });
    });
  });

  describe("Group Chat", function () {
    it('should get messages to all participants', function () {
       var joined = 0,
         room_id = false,
         groupKey = "123sd53%R$E",
         gotMsg = 0,
         checkResponse = function (JSON) {
           var hasEvent = false;
           $.each(JSON, function (k, val) {
             if (val.data.type == "roommessage") {
               gotMsg++;
               expect(val.data.from).toEqual(user1.api.user_id);
               expect(val.data.text).toEqual("hello world");
               expect(val.data.room_id).toEqual(room_id);   
               hasEvent = true;           
             }
           });
         };

       user1.api.startGroupChat(groupKey, function (JSON) {
         joined++;
       });               
       user2.api.startGroupChat(groupKey, function (JSON) {
         joined++;
         room_id = parseInt(JSON.message, 10);
       });
       user3.api.startGroupChat(groupKey, function () {
         joined++;
       });
           
       waitsFor(function () {
         return joined == 3;
       }, "users 1, 2 and 3 to join", 3000);
       
       runs(function () {         
         user1.api.sendGroupChat("hello world", room_id);
         user2.api.registerMessageHandler(function (JSON) {
           checkResponse(JSON);
           isDone1 = true;         
         });
         user3.api.registerMessageHandler(function (JSON) {
           checkResponse(JSON);
           isDone2 = true;         
         });
         waitsFor(function () { 
          return gotMsg == 2;
         }, "users to all get group message", 3000);
       });
    });
  });
  
});      



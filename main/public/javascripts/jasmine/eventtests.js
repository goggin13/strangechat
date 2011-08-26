describe('events', function () {
  
  beforeEach(function () {
     beforeEachFunction();
   });
  
   afterEach(function () {
     afterEachFunction();
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
            if (message.from == from_user) {
              expect(message.type).toEqual(type);            
              expect(message.from).toEqual(from_user);
              expect(message.text).toEqual("hello from " + from_user_lbl);
              messageCount++;              
            }
          },
          ph = function (power) {
            messageCount++;
          };
        // user3channel.bindLogin(function () { loggedInCount++ });
        // user4channel.bindLogin(function () { loggedInCount++ });        
        user3channel.bindToMessage(function (message) {
          mh(message, user4.api.user_id, "4", "roommessage");              
        });
        user4channel.bindToMessage(function (message) {
          mh(message, user3.api.user_id, "3", "roommessage");               
        });       
        user3channel.bindToIsTyping(function (message) {
          mh(message, user4.api.user_id, "4", "useristyping");              
        });
        user4channel.bindToIsTyping(function (message) {
          mh(message, user3.api.user_id, "3", "useristyping");               
        });
        user3channel.bindToUsedPower(function (power) {
          ph();
        });
        user4channel.bindToUsedPower(function (power) {
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
});
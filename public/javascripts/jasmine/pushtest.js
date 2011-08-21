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

  describe("get a room", function () {
    it('should pair two users', function () {
      var msg1 = false,
        msg2 = false,
        checkJoinFor = function (JSON, user_id, avatar, alias, isUser1) {
          if (isUser1) {
            user1["room_id"] = val.data.room_id;
          } else {
            user2["room_id"] = val.data.room_id;     
          }
          expect(val.data.new_user).toEqual(user_id);
          expect(val.data.avatar).toEqual(avatar);
          expect(val.data.alias).toEqual(alias); 
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
      
      user1.api.registerJoinHandler(mh1);
      user2.api.registerJoinHandler(mh2);
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

});
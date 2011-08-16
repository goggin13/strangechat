
describe('ChatApi', function () {
	var user1 = {
			user_id: 15,
			avatar: "http://bnter.com/web/assets/images/8996__w320_h320.jpg",
			alias: "SuperMan"
		}, user2 = {
			user_id: 16,
			avatar: "http://bnter.com/web/assets/images/17169__w184_h184.png",
			alias: "Lex Luther"		
		};

  it('Will log you in', function () {
		var testLogin = function (JSON, user) {
			var me;
			$.each(JSON, function (k, v) {
				me = v;
			}); 
			expect(me.alias).toEqual(user.alias);
			expect(me.avatar).toEqual(user.avatar);
		};
		
		var loggedIn1 = false,
			loggedIn2 = false;
		user1["api"] = ChatAPI(user1.user_id, user1.avatar, user1.alias, function (JSON) {
			testLogin(JSON, user1);
			loggedIn1 = true;
		});
		user2["api"] = ChatAPI(user2.user_id, user2.avatar, user2.alias, function (JSON) {
			testLogin(JSON, user2);
			loggedIn2 = true;
		});
		waitsFor(function () { 
			return loggedIn1 && loggedIn2;
		}, "logins timed out", 3000);
  });

});


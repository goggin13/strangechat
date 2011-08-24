
var TestUser = function (spec) {
  var that = {},
    my = {};
  my.hero_id = spec.hero_id;
  my.avatar = spec.avatar;
  my.alias = spec.alias;
    
  my.joinCallback = function (user) {
    MyUtil.debug(user.user_id);
  };
    
  my.loginCallback = function () {
    my.api.requestRandomRoom(my.joinCallback);
  }; 
  
  my.init = function () {
    my.api = ChatAPI(my.hero_id, my.avatar, my.alias, my.loginCallback);
    return that;
  };
  
  return my.init();
};


var t = TestUser({
  hero_id: 999,
	avatar: "http://bnter.com/web/assets/images/8996__w320_h320.jpg",
	alias: "SuperMan"
});
var t2 = TestUser({
  hero_id: 1000,
	avatar: "http://bnter.com/web/assets/images/8996__w320_h320.jpg",
	alias: "SuperMan22"
});


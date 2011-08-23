


var Channel = function (spec) {
	var that = {},
		my = {};
	that.channel_name = spec.channel_name;
	my.pusher = spec.pusher;
	my.subscribed = false;
	
	my.memberToUser = function (m) {
	  return User({
      user_id: m.info.user_id,
      alias: m.info.name,
      session: m.info.session,
      avatar: m.info.avatar	    
	  });
	};
	
	that.subscribe = function () {
	  if (my.subscribed) {
	    return;
	  }
	  my.channel = my.pusher.subscribe(that.channel_name);
	  my.channel.bind('subscription_error', function(status) {
	    alert("HTTP: " + status + " on subscribe attempt to " + that.channel_name);
	    my.subscribed = false;
    });
    my.subscribed = true;
	};

	that.bindLogon = function (f) {
	  that.bind(types.PUSHER_MEMBER_LOGON, function (member) {
	    f(my.memberToUser(member));
	  });	  
	};

	that.bindLogoff = function (f) {
	  that.bind(types.PUSHER_MEMBER_LOGOFF, function (member) {
	    f(my.memberToUser(member));
	  });
	};
	
	that.bindLogin = function (f) {
	  that.bind(types.PUSHER_LOGIN, function (members) {
	    var users = [];
	    members.each(function (m) {
	      users.push(my.memberToUser(m));
	    });
	    f(users);
	  });
	};
	
	that.bind = function (event, f) {
	  that.subscribe();
	  my.channel.bind(event, f);
	};
	
	that.disconnect = function () {
	  my.pusher.unsubscribe(that.channel_name);
	  my.subscribed = false;
	};
	
	that.send = function (path, data) {
    my.pusher.send(path, data);
	};
	
	that.push = function (event, data) {
	  my.pusher.push(that.channel_name, event, data);
	};
	
	my.init = function () {
		return that;
	};
	
	return my.init();
};

/*global document, User, MyUtil, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true, newcap: true */

var types = {
  PUSHER_LOGIN: "pusher:subscription_succeeded",
  PUSHER_MEMBER_LOGON: "pusher:member_added",
  PUSHER_MEMBER_LOGOFF: "pusher:member_removed",
  BROADCAST: 'broadcast',
  BLACKLIST: 'blacklist'
};

var Channel = function (spec) {
  "use strict";
  var that = {},
    my = {};
  my.pusher = spec.pusher;
  that.channel_name = spec.channel_name + (my.pusher.isLocal() ? "-local" : "");  
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
    my.channel.bind('subscription_error', function (status) {
      alert("HTTP: " + status + " on subscribe attempt to " + that.channel_name);
      my.subscribed = false;
    });
    my.subscribed = true;
  };

  that.bindBroadcast = function (f) {
    that.bind(types.BROADCAST, f);
  };

  that.bindBlackList = function (f) {
    that.bind(types.BLACKLIST, f);
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

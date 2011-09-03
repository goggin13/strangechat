/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, MatchMaker, APusher, Pusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */


var RoomMessage = function (spec) {
  "use strict";
  var that = Event(spec);
  that.type = "roommessage";
  that.text = spec.text;
  return that;
};

var UserIsTyping = function (spec) {
  "use strict";
  var that = RoomMessage(spec);
  that.type = "useristyping";
  return that;
};


var RoomChannel = function (spec) {
  "use strict";  
  var that = Channel(spec),
    my = {};
  my.types = {
    ROOM_MESSAGE: "roommessage",
    USER_TYPING: "useristyping",
    USED_POWER: "usedpower",
    OPENED_KUBE: "openedkube",
    REJECTED_KUBE: "rejectedkube"
  };
  
  my.push = function (obj) {
    that.push(obj.type, obj.toJson());
  };
  
  that.bindToMessage = function (f) {
    that.bind(my.types.ROOM_MESSAGE, function (data) {
      f(data);
    });
  };

  that.bindToIsTyping = function (f) {
    that.bind(my.types.USER_TYPING, function (data) {
      f(data);
    });
  };

  that.useKarma = function (power_id, user, on_user, isGood) {
    that.usePower(power_id, user, on_user, isGood ? 1 : 0);
  };

  that.usePower = function (power_id, user, on_user, params) {
    var path = 'usepower';
    that.send(path, {
      power_id: power_id,
      channel: that.channel_name,
      user_id: user.user_id,
      session: user.session,
      for_user: on_user ? on_user.user_id : -1,
      for_session: on_user ? on_user.session : "",
      "params[0]": params
    });
  };

  that.bindToOpenKube = function (f) {
    that.bind(my.types.OPENED_KUBE, function (response) {
      f(JSON.parse(response.reward), JSON.parse(response.kube));
    });
  };

  that.bindToRejectedKube = function (f) {
    that.bind(my.types.REJECTED_KUBE, f);
  };

  that.bindToUsedPower = function (f) {
    that.bind(my.types.USED_POWER, f);
  };
  
  that.userIsTyping = function (from, text) {
    my.push(UserIsTyping({
      from: from,
      text: text
    }));
  };

  that.message = function (from, msg) {
    my.push(RoomMessage({
      from: from,
      text: msg
    }));
  };
  
  return that;
};
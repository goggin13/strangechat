

var RoomMessage = function (spec) {
  var that = Event(spec);
  that.type = "roommessage";
  that.text = spec.text;
  return that;
};

var UserIsTyping = function (spec) {
  var that = RoomMessage(spec);
  that.type = "useristyping";
  return that;
};


var RoomChannel = function (spec) {
  var that = Channel(spec),
    my = {};
  my.types = {
    ROOM_MESSAGE: "roommessage",
    USER_TYPING: "useristyping",
    USED_POWER: "usedpower"
  };
  
  my.push = function (obj) {
    that.push(obj.type, obj.toJson());
  };
  
  that.bindToMessage = function (f) {
    that.subscribe();
    that.bind(my.types.ROOM_MESSAGE, function (data) {
      console.debug("NEW MESSAGE");
      f(data);
    });
  };

  that.bindToIsTyping = function (f) {
    that.subscribe();
    that.bind(my.types.USER_TYPING, function (data) {
      f(data);
    });
  };

  that.usePower = function (power_id, user) {
    var path = 'usepower';
    that.send(path, {
      power_id: power_id,
      channel: that.channel_name,
      user_id: user.user_id,
      session: user.session
    });
  };

  that.bindToUsedPower = function (f) {
    that.subscribe();
    that.bind(my.types.USED_POWER, function (data) {
      f(data);
    });
  };
  
  that.userIsTyping = function (to, from, text) {
    my.push(UserIsTyping({
      to: to,
      from: from,
      text: text,
    }));
  };

  that.message = function (to, from, msg) {
    my.push(RoomMessage({
      to: to,
      from: from,
      text: msg,
    }));
    console.debug("PUsh Message");
  };
  
  return that;
};


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
    that.bind(my.types.ROOM_MESSAGE, function (data) {
      f(data);
    });
  };

  that.bindToIsTyping = function (f) {
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
    that.bind(my.types.USED_POWER, function (data) {
      f(data);
    });
  };
  
  that.userIsTyping = function (from, text) {
    my.push(UserIsTyping({
      from: from,
      text: text,
    }));
  };

  that.message = function (from, msg) {
    my.push(RoomMessage({
      from: from,
      text: msg,
    }));
    console.debug("PUsh Message");
  };
  
  return that;
};


var types = {
  PUSHER_LOGIN: "pusher:subscription_succeeded",
  PUSHER_MEMBER_LOGON: "pusher:member_added",
  PUSHER_MEMBER_LOGOFF: "pusher:member_removed",
  BROADCAST: 'broadcast',
  BLACKLIST: 'blacklist'
};

var Event = function (spec) {
  var that = {},
    my = {};
  that.type = "event";
  that.to = spec.to;
  that.from = spec.from;
  
  that.toString = function () {
    return this.type + " (" + this.for_user + ")";
  };
  
  that.toJson = function () {
    return JSON.stringify(that);
  };
  
  return that;
};

var Broadcast = function (spec) {
  var that = Event(spec);
  that.type = types.BROADCAST;
  that.text = spec.text;
  return that;
};

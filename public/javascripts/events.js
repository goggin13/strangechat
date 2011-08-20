

Object.spawn = function (parent, props) {
  var defs = {}, key;
  for (key in props) {
    if (props.hasOwnProperty(key)) {
      defs[key] = {value: props[key], enumerable: true};
    }
  }
  return Object.create(parent, defs);
}

var Event = {
  type: "event",
  to_user: -1,
  from_user: -1,
  toString: function () {
    return this.type + " (" + this.for_user + ")";
  },
  toJson: function () {
    return JSON.stringify(this);
  }
};

var types = {
  JOIN_REQUEST: "JoinRequest",
  ACCEPT_REQUEST: "AcceptRequest",
  PUSHER_LOGIN: "pusher:subscription_succeeded",
  PUSHER_MEMBER_LOGON: "pusher:member_added",
  PUSHER_MEMBER_LOGOFF: "pusher:member_removed"
};

var JoinRequest = Object.spawn(Event, {
  type: types.JOIN_REQUEST
});
var AcceptRequest = Object.spawn(Event, {
  type: types.ACCEPT_REQUEST
});
// var join = Object.spawn(JoinRequest, {for_user: 4});
// var accept = Object.spawn(AcceptJoin, {for_user: 2});



// Event.prototype.getType = function () {
  // return that.type;
// };


// var mfc = new Event("test");
// var mfc2 = new Event("test2");
// alert(mfc.getType());
// alert(mfc.getType());

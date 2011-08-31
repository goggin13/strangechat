/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, types, console, HTTP, RoomChannel, MatchMaker, APusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var Event = function (spec) {
  "use strict";
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
  "use strict";  
  var that = Event(spec);
  that.type = types.BROADCAST;
  that.text = spec.text;
  return that;
};

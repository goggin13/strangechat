/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, RoomChannel, MatchMaker, APusher, Pusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var User = function (spec) {
  "use strict";
  var that = {};
  that.user_id = spec.user_id;
  that.alias = spec.alias;
  that.session = spec.session;
  that.avatar = spec.avatar;
  return that;
};
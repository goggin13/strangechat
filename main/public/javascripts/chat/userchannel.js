/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, RoomChannel, MatchMaker, APusher, Pusher, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var UserChannel = function (spec) {
  "use strict";
  spec.channel_name = spec.user.user_id + "_channel";
  var that = Channel(spec),
    my = {};
  my.types = {
    NEW_POWER: "newpower",
    KARMA: "karma",
    NEW_COINS: "newcoins"
  };

  that.bindNewKarmaKube = function (f) {
    that.bind(my.types.KARMA, f);
  };  

  that.bindNewCoins = function (f) {
    that.bind(my.types.NEW_COINS, f);
  };
  
  that.bindNewPower = function (f) {
    that.bind(my.types.NEW_POWER, f);
  };
  
  return that;
};
/*jslint eqeq: true, newcap: false, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, HTTP, RoomChannel, MatchMaker, APusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */


var ChatAPI = function (spec) {
  "use strict";
  var that = {},
    my = {};
  my.home_url = spec.home_url;
  
  that.user_id = "";
  my.inputsToWatch = [];
  that.room_ids = [];
  my.session_id = null;
  that.im_talking_to = {}; // map user_ids to room_ids
  that.superPowers = []; // filled after login so clients can retrieve
  that.superPowerDetails = {};
  that.karmaKubes = [];
  that.coinCount = 0;
  
  that.user = null;
  my.superhero_id = spec.user_id;
  my.channels = {};
  my.broadcastHandler = false;
  my.pusher = false;
  that.serverErrorCallback = false;
  
  // send an API call, either POST, or GET, with given data.
  // on success the given callback function is called
  that.send = function (url, method, data, callback) {
    if (that.user !== null) {
      data.session = that.user.session;
    }      
    return HTTP.send(url, method, data, callback, that.serverErrorCallback);
  };

  that.getGroupChannel = function (groupKey) {
    var channel_name = "presence-group-" + groupKey;
    return RoomChannel({
      channel_name: channel_name,
      pusher: my.pusher
    });
  };

  my.loginCallback = function (JSON, alias, callback) {
    if (JSON.hasOwnProperty("status") && JSON.status === "error") {
      callback(JSON);
      return;
    }
    var me = JSON[my.superhero_id];
    that.user = User({
      user_id: me.id,
      session: me.session_id,
      alias: me.alias,
      avatar: me.avatar
    });
    
    that.superPowers = me.superPowers;
    that.superPowerDetails = me.superPowerDetails;  
    that.karmaKubes = me.karmaKubes;
    that.coinCount = me.coinCount;
    
    // MyContacts.put(that.user);
    that.user_id = that.user.user_id;
    if (callback) {
      callback(JSON);
    }
  };
    
  that.logout = function () {
    my.pusher.disconnect();
  };
  
  that.login = function (user_id, avatar, alias, callback) {
    var url = my.home_url + 'signin',
      data = {
        sign_in_id: user_id,
        avatar: avatar,
        alias: alias
      };
    that.send(url, "POST", data, function (JSON) {
      if (JSON.hasOwnProperty("status") && JSON.status === "error") {
        $("#content").html(JSON.message);
      } else {
        my.loginCallback(JSON, alias, callback);
      }
      
    });     
  };

  my.sendMySocketID = function (callback) {
    var url = my.home_url + "setsocket",
      data = {
        user_id: that.user.user_id,
        socket_id: my.pusher.getSocketID()
      };    
    that.send(url, "POST", data, callback);
  };

  // get a response from Eliza
  that.eliza = function (channel, qry, callback) {
    var url = my.home_url + 'eliza',
      data = {
        channel: channel,
        qry: $.trim(qry)
      };
    that.send(url, "GET", data, callback);
  };

  // indicate you wish to get paired with a random user
  that.requestRandomRoom = function (request_callback) {
    // subscribe to random-presence channel
    var joinCallback = function (user) {
        request_callback(user);
        that.im_talking_to[user.user_id] = user;
        my.channels[user.channel.channel_name] = user.channel;
      },
      matchMaker = MatchMaker({
        user: that.user,
        callback: joinCallback,
        pusher: my.pusher
      });
    matchMaker.matchMe();
  };
  
  that.spendCoins = function (amount, callback) {
    var url = my.home_url + "users/spendCoins",
      data = {
        amount: amount,
        user_id: that.user.user_id
      };
    that.send(url, "GET", data, function (resp) {
      callback(resp);
    });    
  };
  
  that.openKube = function (kube_id, channel) {
    var url = my.home_url + "openkube",
      data = {
        kube_id: kube_id,
        user_id: that.user.user_id,
        channel: channel
      };
    that.send(url, "GET", data);
  };
  
  that.rejectKube = function (kube_id, channel) {
    var url = my.home_url + "rejectkube",
      data = {
        kube_id: kube_id,
        channel: channel,
        user_id: that.user.user_id
      };
    that.send(url, "GET", data);
  };  
  
  that.checkPowers = function () {
    $.get(my.home_url + "mock/checkPowers");
  };
  
  that.getBroadcastChannel = function () {
    return my.broadcastChannel;
  };

  that.bindNewKarmaKube = function (f) {
    my.userChannel.bindNewKarmaKube(f);
  };

  that.bindNewPower = function (f) {
    my.userChannel.bindNewPower(f);
  };
  
  that.bindBroadcast = function (f) {
    my.broadcastHandler = f;
  };
  
  my.blacklistWrapper = function (msg) {
    if (msg.text === that.user.user_id) {
      window.location.reload();
    }
  };
  
  my.broadcastWrapper = function (msg) {
    if (!my.broadcastHandler) {
      alert(msg.text);
    } else {
      my.broadcastHandler(msg);
    }
  };
  
  that.hideSocketID = function () {
    my.pusher.HideSocketID = true;
  };
  
  my.initPusher = function (login_callback) {
    my.pusher = APusher({home_url: my.home_url});
    my.pusher.bindConnected(function () {  // wait to connect
      that.login(spec.user_id, spec.avatar, spec.alias, function (JSON_login) {  // send Play! our socket key
        my.pusher.setUserInfo(that.user.session, that.user.user_id);
        my.userChannel = UserChannel({
          user: that.user,
          pusher: my.pusher
        });     
           
        my.sendMySocketID(function (JSON) {             
          if (JSON.status === "okay") {
        
            my.broadcastChannel = Channel({
              channel_name: "presence-SHCH-broadcast",
              pusher: my.pusher
            });
            my.broadcastChannel.bindBroadcast(my.broadcastWrapper);           
            my.broadcastChannel.bindBlackList(my.blacklistWrapper);
            if (login_callback) {
              login_callback(JSON_login);
            }
        
          }
        });
      });

    });
  };
  
  my.init = function () {
    my.initPusher(spec.login_callback);
    return that;
  };
  
  return my.init();
};

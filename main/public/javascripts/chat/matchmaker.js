/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, RoomChannel, MatchMaker, APusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var JoinRequest = function (spec) {
  "use strict";
  var that = Event(spec),
    my = {};
  that.type = "JoinRequest";
  return that;
};
  
var AcceptRequest = function (spec) {
  "use strict";
  var that = Event(spec),
    my = {};
  that.type = "AcceptRequest";
  that.avatar = spec.avatar;
  that.alias = spec.alias;
  that.session = spec.session;  
  return that;  
};


var MatchMaker = function (spec) {
  "use strict";  
  var that = {},
    my = {};
  my.pusher = spec.pusher;
  spec.channel_name = "presence-random-chat";
  my.randomChannel = Channel(spec);    
  my.types = {
    JOIN_REQUEST: "JoinRequest",
    ACCEPT_REQUEST: "AcceptRequest"
  };
  my.user = spec.user;
  my.callback = spec.callback;
  my.successful = false;
  
  that.membersToChannelName = function (user_id, user_id2) {
    var list = [user_id, user_id2];
    list.sort();
    return "presence-" + list[0] + "_" + list[1];
  };

  my.propsalFailed = function () {
    my.pendingResponse = false;
    my.waitingForChat = true;
  };

  my.canIPairWithMember = function (user_id) {
    return user_id 
           && my.user.user_id 
           && user_id != my.user.user_id;
  };
  
  my.proposeToAll = function (eligible) {
    var availableCount = eligible.length,
      i = 0,
      proposeTo = function () {
        if (!my.successful && i < availableCount) {
          my.proposeMeetUp(eligible[i++]);        
          setTimeout(proposeTo, 1000);          
        }
      };

    if (availableCount > 0) {
      proposeTo();
    }
  };
  
  my.proposeMeetUp = function (user_id) {
    my.pendingResponse = user_id;
    var join = JoinRequest({
      from: my.user.user_id,
      to: user_id
    });
    setTimeout(function () {
      my.propsalFailed();
    }, 1000);    
    my.randomChannel.push(my.types.JOIN_REQUEST, join.toJson());
  };
  
  my.acceptMeetUp = function (with_user_id) {
    var accept = AcceptRequest({
      from: my.user.user_id,
      to: with_user_id,
      session: my.user.session,
      avatar: my.user.avatar,
      alias: my.user.alias
    });
    my.randomChannel.push(my.types.ACCEPT_REQUEST, accept.toJson());
    my.successful = true;
  };    
  
  // login to the given channel, and ensure the user described by userData joins you there.
  // if they do, call the appropriate callback
  my.loginToChannel = function (channel_name, userData) {
    var otherUserSignedIn = false,
      channel = null,
      returnData = function () {
        userData.channel = channel;
        my.callback(userData);
        otherUserSignedIn = true;
      };
    
    channel = RoomChannel({
      channel_name: channel_name,
      pusher: my.pusher
    });
    
    channel.subscribe();
    channel.bindLogin(function (users) {
      $.each(users, function (k, user) {
        if (user.user_id == userData.user_id) {
          returnData();
          return false;
        }
      });
    });
    channel.bindLogon(function (user) {
      if (user.user_id == userData.user_id) {
        returnData();
      }
    });
    setTimeout(function () {
      if (!otherUserSignedIn) {
        that.matchMe();
      }
    }, 1500);
  };
  
  that.matchMe = function () {
    var waitingForChat = true,
      sentAcceptTo = false;
    my.randomChannel.subscribe();
    
    my.randomChannel.bind(my.types.JOIN_REQUEST, function(data) {
      if (data.to == my.user.user_id
          && (waitingForChat || my.pendingResponse == data.from)) {
        my.acceptMeetUp(data.from); 
        sentAcceptTo = data.from;
      }
    });

    my.randomChannel.bind(my.types.ACCEPT_REQUEST, function (data) {
      if (data.to == my.user.user_id && waitingForChat) {
        waitingForChat = false;
        my.randomChannel.disconnect();
        if (sentAcceptTo != data.from) {
          my.acceptMeetUp(data.from);
          sentAcceptTo = data.from;
        }        
        
        var userData = User({
            user_id: data.from,
            avatar: data.avatar,
            session: data.session,
            alias: data.alias
          }),
          channel_name = that.membersToChannelName(data.from, my.user.user_id);
          
        my.loginToChannel(channel_name, userData);        
      }
    });

    my.randomChannel.bindLogin(function (users) {    
      var eligible = [];      
      $.each(users, function(k, user) {
        if (my.canIPairWithMember(user.user_id)) {
          if ($.inArray(user.user_id, eligible) == -1) {
            eligible.push(user.user_id); 
          }
        }
        my.proposeToAll(eligible);
      });
    });  

    my.randomChannel.bindLogon(function (user) {      
      if (my.waitingForChat && my.canIPairWithMember(user.user_id)) {
        my.proposeMeetUp(user.user_id);        
      }
    }); 

    my.randomChannel.bindLogoff(function (user) {    
      if (my.pendingResponse == user.user_id) {
        my.propsalFailed();
      }
    });    
  };
  
  return that;
};
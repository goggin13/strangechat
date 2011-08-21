/*global document: false, MyUtil, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

var channels = {
  RANDOM_CHANNEL: "presence-random-chat"
};

var MyUtil = (function (user_id, avatar, alias, callback) {
 var that = {};
 
 that.debug = function (msg) {
   if (window.console) {
     console.debug(msg);      
   }
 };
 
 that.removeFromArray = function (arr, ele) {
   var index = $.inArray(ele, arr);
   if (index === -1) {
     return;
   }
   arr.splice(index, 1);
 };
 
 return that;
}());

var ChatAPI = function (user_id, avatar, alias, callback) {
  "use strict";
  var that = {},
  my = {};
  my.home_url = "http://localhost:9000/";
  // my.home_url = "http://10.0.1.50:9000/";  // dev  
  // my.home_url = "http://173.246.100.79/";  // prod 
  // my.home_url = "http://173.246.101.45/";  // staging

  that.user_id = "";
  my.heartbeatServer = "";
  my.currentListen = "";
  my.HEARTBEAT_FREQUENCY = 5000;
  my.inputsToWatch = [];
  my.room_ids = [];
  my.lastReceived = 0;   
  my.waitingForJoin = 0; 
  my.session_id = null;
  my.im_talking_to = {}; // map user_ids to room_ids
  that.superPowers = []; // filled after login so clients can retrieve
  that.superPowerDetails = {};
  
  my.pendingResponse = false;
 
	my.messageHandler = function (JSON) {};
  my.joinHandler = function (JSON) {};

	that.registerJoinHandler = function (f) {
		my.joinHandler = f;
	};

  // assign the message function to be called when 
  // new events arrive
  that.registerMessageHandler = function (f) {
    my.messageHandler = f;
  };

  that.resetMessageHandler = function () {
    my.messageHandler = function (JSON) {};
  };

// send an API call, either POST, or GET, with given data.
  // on success the given callback function is called
  that.send = function (url, method, data, callback) {
    // add callback if this is cross domain
    if (url.indexOf(window.location.host) === -1) {
      url += "?callback=?";
    }
    
    if (my.session_id != null) {
      data["session"] = my.session_id;
    }
    var hash = url + "?" + serialize(data);
    
    // hash.indexOf("heartbeat") === -1 && 
    if (hash.indexOf('imtyping') === -1) {
      MyUtil.debug("GET " + hash);
    }
    
	  $.ajaxSetup({cache: false});  // required for IE to not cache AJAX requests    
    $.ajax({
        type: "GET",
        url: url,
        data: data,
        dataType: 'json',
        success: function(JSON) {
          if (!JSON.hasOwnProperty('status')) {
            MyUtil.debug(JSON);
          } else if (JSON.status == "error") {
            MyUtil.debug("ERROR!!!");
            MyUtil.debug(JSON);
          }
          if (callback) {
            callback(JSON, hash);
          }
        },
        error: function (jqXHR, textStatus, errorThrown) {
          MyUtil.debug("BAD RESPONSE");
          MyUtil.debug(textStatus);
          MyUtil.debug(errorThrown);
        }
    });
    return hash;
  };

  my.loginCallback = function (JSON, alias, callback) {
    if (JSON.hasOwnProperty("status") && JSON.status == "error") {
      callback(JSON);
      return;
    }
    $.each(JSON, function (key, val) {
      MyContacts.put(key, val.name, val.alias, val.heartbeatServer.uri, val.avatar, val.session_id, val);
      if (val.alias == alias) {
        my.heartbeatServer = val.heartbeatServer.uri;
        my.session_id = val.session_id;
        that.user_id = val.id;
        that.superPowers = val.superPowers;
        that.superPowerDetails = val.superPowerDetails;
      }
      
    });
		if (callback) {
			callback(JSON);
		}
  };
  
  that.login = function (user_id, avatar, alias, callback) {
    var url = my.home_url + 'signin',
      data = {
        sign_in_id: user_id,
        avatar: avatar,
        alias: alias,
      };
      that.send(url, "POST", data, function (JSON) {
        my.loginCallback(JSON, alias, callback);
      });     
  };

  my.sendMySocketID = function (callback) {
    var url = my.home_url + "setsocket",
      data = {
        user_id: that.user_id,
        socket_id: my.pusher.connection.socket_id
      };    
    that.send(url, "POST", data, callback);
  };

  that.membersToChannelName = function (member1, member2) {
    var list = [member1.name, member2.name];
    list.sort();
    return member1.name + "_" + member2.name;
  };

  that.push = function (channel, event, data) {
    var url = my.home_url + "push",
      data = {
        user_id: that.user_id,
        event: event,
        channel: channel,
        message: data,
        socket_id: my.pusher.connection.socket_id
      };    
    that.send(url, "POST", data, callback);
  };

  my.propsalFailed = function () {
    my.pendingResponse = false;
    my.waitingForChat = true;
  };

  that.proposeMeetUp = function (member) {
    my.pendingResponse = member.info.user_id;
    console.debug("propose to " + my.pendingResponse);
    var join = Object.spawn(JoinRequest, {
      to_user: member.info.user_id,
      from_user: that.user_id
    });
    setTimeout(function () {
      my.propsalFailed();
    }, 1000);    
    that.push(channels.RANDOM_CHANNEL, types.JOIN_REQUEST, join.toJson());

  };

  that.acceptMeetUp = function (with_user_id) {
    var accept = Object.spawn(AcceptRequest, {
      to_user: with_user_id,
      from_user: that.user_id
    });
    that.push(channels.RANDOM_CHANNEL, types.ACCEPT_REQUEST, accept.toJson());
  };

  that.canIPairWithMember = function (member) {
    return member.info.user_id != that.user_id;
  };

  // indicate you wish to get paired with a random user
  that.requestRandomRoom = function () {
    // subscribe to random-presence channel
    my.randomChatChannel = my.pusher.subscribe(channels.RANDOM_CHANNEL);
    
    my.randomChatChannel.bind(types.JOIN_REQUEST, function(data) {
      if (data.to_user == that.user_id
          && (my.waitingForChat || my.pendingResponse == data.from_user)) {
        that.acceptMeetUp(data.from_user); 
        my.randomChatChannel.disconnect();
        document.writeln("<p>paired [" + data.from_user + "," + that.user_id + "]</p>");        
      }
    });

    my.randomChatChannel.bind(types.ACCEPT_REQUEST, function (data) {
      if (data.to_user == that.user_id) {
				my.joinHandler({
					user_id: data.from_user
				});	
      }
    });
    
    my.randomChatChannel.bind(types.PUSHER_LOGIN, function (members) {    
      var matched = false;
      members.each(function(member) {
        if (!matched && that.canIPairWithMember(member)) {
          matched = true;
          that.proposeMeetUp(member);
        }
      });
      if (!matched) {
        my.waitingForChat = true;
      }
    });  
    
    my.randomChatChannel.bind(types.PUSHER_MEMBER_LOGON, function (member) {    
      if (my.waitingForChat && that.canIPairWithMember(member)) {
        that.proposeMeetUp(member);        
      }
    }); 
    
    my.randomChatChannel.bind(types.PUSHER_MEMBER_LOGOFF, function (member) {    
      if (my.pendingResponse == member.info.user_id) {
        my.propsalFailed();
      }
    });     
  };
  
  my.initPusher = function (login_callback, socket_callback) {
    my.pusher = new Pusher('28fec1752d526c34d156');
		my.pusher.connection.bind('connected', function() {	// wait to connect
		  that.login(user_id, avatar, alias, function (JSON) {  // send Play! our socket key
		    if (login_callback) {
					login_callback(JSON);
				}
		    my.sendMySocketID(function (JSON) {             
  			  if (JSON.status == "okay") {                  // if all good, request a room
            socket_callback();
  			  }
  			});
		  });
		});
  };
  
  my.init = function () {
		my.initPusher(callback, function () {
		  that.requestRandomRoom();
		});
    return that;
  };
  
  return my.init();
};

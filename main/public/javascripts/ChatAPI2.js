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
  
  my.checkInputsForTyping = function () {
    $.each(my.inputsToWatch, function (key, data) {
      var newVal = data.input.val();
      if (newVal != data.last) {
        that.imTypingInRoom(
          data.input.attr('id'), 
          newVal, 
          data.input.attr('room_id')
        );
      }
      data.last = newVal;
    });
    setTimeout(my.checkInputsForTyping, 1000);
  };
  
  // mark an input to watch to send useristyping notifications
  that.watchInput = function (input) {
    my.inputsToWatch.push({
        input: input,
        last: ""
    });
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

  // tell the API to keep you alive in the given room.
  // As soon as you call Leaveroom with this id, it will automatically
  // remove you. 
  that.beatInRoom = function (room_id) {
    if ($.inArray(my.room_ids, room_id) == -1) {
      my.room_ids.push(room_id);
    }
  };

  // assign the message function to be called when 
  // new events arrive
  that.registerMessageHandler = function (f) {
    my.messageHandler = f;
  };

  that.resetMessageHandler = function () {
    my.messageHandler = function (JSON) {
    };
  };

  // listen for chat invitations, direct messages, etc.
  that.listen = function (lastReceived) {
    var url = my.heartbeatServer + 'listen',
      data = {
        user_id: that.user_id,
        lastReceived: lastReceived
      };
    my.currentListen = that.send(url, "GET", data, function (JSON, hash) {
      var response = {},
        joins = {}; // room_id => event-id
        
      $.each(JSON, function (key, val) {
        var d = val.data,
          isErr = d.hasOwnProperty("status") && d.status == "error";
          
        response[key] = val;          
        
        if (!isErr && (d.type === "userlogon" || d.type === "join")) {
          MyContacts.put(d.new_user, d.name, d.alias, d.server, d.avatar, d.new_session, d);
        }
        if (!isErr && d.type === "leave" 
                   && joins.hasOwnProperty(val.data.room_id)) {
          my.waitingForJoin++;
          delete response[key];
          delete response[joins[val.data.room_id]];
          MyUtil.debug("ReRequesting");
          that.requestRandomRoom();
        } else if (!isErr && d.type === "join") {
          my.im_talking_to[val.data.new_user] = val.data.room_id;
          my.waitingForJoin--;
          joins[val.data.room_id] = key;
        }          
        
      });
      my.messageHandler(response);
      if (hash === my.currentListen) {
        if (JSON.length > 0) {
          that.listen(JSON[JSON.length - 1].id);          
        } else {
          that.listen(lastReceived);
        }
      }
    });
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
    if (my.heartbeatServer) {
      that.listen(my.lastReceived);
    } else {
      JSON = {
        status: "error",
        message: "Sorry! We couldn't log you in"
      };
    }
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

  that.logout = function (callback) {
    var url = my.home_url + 'signout',
      data = {
        user_id: that.user_id,
      };
      that.send(url, "POST", data, callback);     
  };
  
  // send a direct message to another friend
  that.directMessage = function (to, msg) {
    var url = MyContacts.getServerFor(to) + 'message',
      data = {
        user_id: that.user_id,
        for_user: to,
        msg: msg
      };
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  that.roomMessage = function (to, msg, room_id, errCallback) {
    var server = MyContacts.getServerFor(to);
    that.roomMessageInner(server, to, msg, room_id, errCallback);
  };  
  
  that.roomMessageInner = function (server, to, msg, room_id, errCallback) {
    var url =  server + 'roommessage',
      data = {
        user_id: that.user_id,
        msg: msg,
        room_id: room_id
      };
    if (typeof(to) != "object") {
      to = [to];
    }   
    $.each(to, function (i, recip) {
      data["for_user[" + i + "]"] = recip;
      data["for_session[" + i + "]"] = MyContacts.getSessionId(recip);   
    });      
    that.send(url, "POST", data, function (JSON) {
      if (JSON.status == "error") {
        if (errCallback) errCallback();
      }
    });
  };
  
  // indicate you wish to get paired with a random user
  that.requestBotRoom = function (bot_id) {
    var url = my.home_url + 'elizas/requestbotroom',
      data = {
        user_id: that.user_id,
        bot_id: bot_id
      };
    that.send(url, "POST", data);
  };
  
  that.imTypingInRoom = function (to, text, room_id) {
    if (!to) {
      return;
    }
    var url =  MyContacts.getServerFor(to) + 'imtyping',
      data = {
        for_user: to,
        for_session: MyContacts.getSessionId(to),
        user_id: that.user_id,
        room_id: room_id,
        text: text
      };
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  that.usePower = function (power_id, other_id, room_id, isGroup, callback) {
    var url = my.home_url + 'usepower',
      data = {
        user_id: that.user_id,
        power_id: power_id,
        for_user: isGroup ? -1 : other_id,
        for_session: isGroup ? "" : MyContacts.getSessionId(other_id),
        room_id: room_id,
      };
    that.send(url, "POST", data, callback);
  };
  
  that.leaveRoom = function (room_id) {
    var url = my.home_url + 'leaveroom',
      data = {
        user_id: that.user_id,
        room_id: room_id
      };
    MyUtil.removeFromArray(my.room_ids, room_id);
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  // get a response from Eliza
  that.eliza = function (to, room_id, qry, callback) {
    var url = my.home_url + 'eliza',
      data = {
        qry: $.trim(qry)
      };
    that.send(url, "GET", data, callback);
  };
  
  // get a response from a bot
  that.talkToBot = function (bot_user_id, bot_id, qry, room_id, callback) {
    var url = my.heartbeatServer + 'elizas/talkTo',
      data = {
        bot_user_id: bot_user_id,
        bot_id: bot_id,
        user_id: that.user_id,
        room_id: room_id,
        qry: qry
      };
    that.send(url, "GET", data, callback);
  };
  
  // initiate or join a group chat
  that.startGroupChat = function (group_key, callback) {
    var url = my.home_url + 'group',
      data = {
        user_id: that.user_id,
        key: group_key
      };
    that.send(url, "GET", data, function (JSON) {
      if (JSON.status == "okay") {
        that.beatInRoom(JSON.message);
        if (callback) {
          callback(JSON);
        }        
      }
    });
  };
  
  that.sendGroupChat = function (msg, room_id, errCallback) {
    var people = MyContacts.getIdListByServer(that.user_id);
    $.each(people, function (server, people) {
      that.roomMessageInner(server, people, msg, room_id, errCallback);
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
    console.debug("RESET");
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
        document.writeln("<p>paired [" + data.from_user + "," + that.user_id + "]</p>");        
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
  
  my.initPusher = function (callback) {
    my.pusher = new Pusher('28fec1752d526c34d156');
		my.pusher.connection.bind('connected', function() {	// wait to connect
		  that.login(user_id, avatar, alias, function () {  // send Play! our socket key
		    document.writeln("<p>I'm " + that.user_id + "</p>");
		    my.sendMySocketID(function (JSON) {             
  			  if (JSON.status == "okay") {                  // if all good, request a room
            callback();
  			  }
  			});
		  });
		});
  };
  
  my.init = function () {
		my.initPusher(function () {
		  that.requestRandomRoom();
		});
    my.checkInputsForTyping();
    return that;
  };
  
  return my.init();
};

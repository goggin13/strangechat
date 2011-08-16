/*global document: false, Util, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

var Util = (function (user_id, avatar, alias, callback) {
	var that = {};
	
	that.debug = function (msg) {
		console.debug(msg);
	};
	
	return that;
}());

var ChatAPI = function (user_id, avatar, alias, callback) {
  "use strict";
  var that = {},
  my = {};
  // my.home_url = "http://localhost:9000/";  // dev
  // my.home_url = "http://10.0.1.50:9000/";  // dev  
  // my.home_url = "http://173.246.100.79/"; // prod 
  my.home_url = "http://173.246.101.45/";  // staging

  that.user_id = "";
  my.heartbeatServer = "";
  my.currentListen = "";
  my.HEARTBEAT_FREQUENCY = 5000;
  my.inputsToWatch = [];
  my.room_ids = [];
  my.lastReceived = 0;    
  my.session_id = -1;
  my.im_talking_to = {}; // map user_ids to room_ids
  that.superPowers = []; // filled after login so clients can retrieve
  that.superPowerDetails = {};
  
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
  that.watchInput = function (input, room_id) {
    my.inputsToWatch.push({
        input: input,
        room_id: room_id,
        last: ""
    });
  };
  
  // send an API call, either POST, or GET, with given data.
  // on success the given callback function is called
  that.send = function (url, method, data, callback) {
    // add callback if this is cross domain
    if (url.indexOf(window.location.host) === -1) {
      url += "?callback=?";
      // data["callback"] = "?";
    }
    
    var hash = url + "?" + serialize(data);
    
    if (hash.indexOf("heartbeat") === -1 && hash.indexOf('imtyping') === -1) {
      Util.debug("GET " + hash);
    }
    
	  $.ajaxSetup({cache: false});  // required for IE to not cache AJAX requests    
    $.ajax({
        type: "GET",
        url: url,
        data: data,
        dataType: 'json',
        success: function(JSON) {
          if (!JSON.hasOwnProperty('status')) {
            Util.debug(JSON);
          } else if (JSON.status == "error") {
            Util.debug("ERROR!!!");
            Util.debug(JSON);
          }
          if (callback) {
            callback(JSON, hash);
          }
        },
        error: function (jqXHR, textStatus, errorThrown) {
          Util.debug("BAD RESPONSE");
          Util.debug(textStatus);
          Util.debug(errorThrown);
        }
    });
    return hash;
  };

  // tell the API to keep you alive in the given room.
  // As soon as you call Leaveroom with this id, it will automatically
  // remove you. 
  that.beatInRoom = function (room_id) {
    my.room_ids.push(room_id);
  };

  // assign the message function to be called when 
  // new events arrive
  that.registerMessageHandler = function (f) {
    my.messageHandler = f;
  };

  // listen for chat invitations, direct messages, etc.
  that.listen = function (lastReceived) {
    var url = my.heartbeatServer + 'listen',
      data = {
        user_id: that.user_id,
        lastReceived: lastReceived
      };
    my.currentListen = that.send(url, "GET", data, function (JSON, hash) {
      var response = {};
      $.each(JSON, function (key, val) {
        if (val.data.type === "userlogon" && !MyContacts.has(val.data.new_user)) {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server, val.data.avatar);
        }
        if (val.data.type === "join") {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server, val.data.avatar);
        }     
        if (val.data.session_id == my.session_id ||
            (val.data.type !== "join" && // these are the only types 
            val.data.type !== "leave" && // that are session_id signed, currently
            val.data.type !== "userlogin" &&
            val.data.type !== "userlogout" &&
            val.data.type !== "newpower" &&
            val.data.type !== "usedpower")) {
          response[key] = val;
          if (val.data.type === 'join') {
             my.im_talking_to[val.data.new_user] = val.data.room_id;
          } else if (val.data.type === "leave") {
             // dont delete them from the phonebook, need them to 
             // display left message; client will delete when done
          }
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
      MyContacts.put(key, val.name, val.alias, val.heartbeatServer.uri);
      
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
      setInterval(my.heartbeat, my.HEARTBEAT_FREQUENCY);
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
        user_id: user_id,
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
        from_user: that.user_id,
        for_user: to,
        msg: msg
      };
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  that.roomMessage = function (to, msg, room_id, errCallback) {
    that.roomMessageInner(MyContacts.getServerFor(to), to, msg, room_id, errCallback);
  };  
  
  that.roomMessageInner = function (server, to, msg, room_id, errCallback) {
    var url =  server + 'roommessage',
      data = {
        from_user: that.user_id,
        for_user: to,
        msg: msg,
        room_id: room_id
      };
    that.send(url, "POST", data, function (JSON) {
      if (JSON.status == "error") {
        errCallback();
      }
    });
  };
  
  // indicate you wish to get paired with a random user
  that.requestRandomRoom = function () {
    var url = my.home_url + 'requestrandomroom',
      data = {
        user_id: that.user_id
      };
    that.send(url, "POST", data, function (JSON) {
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
        user_id: that.user_id,
        room_id: room_id,
        text: text
      };
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  that.usePower = function (power_id, other_id, room_id, callback) {
    var url = my.home_url + 'usepower',
      data = {
        user_id: that.user_id,
        power_id: power_id,
        other_id: oApp.groupKey ? -1 : other_id,
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
    Util.removeFromArray(my.room_ids, room_id);
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  // get a response from Eliza
  that.eliza = function (to, room_id, qry, callback) {
    var url = my.home_url + 'eliza',
      data = {
        user_id: to,
        from_user: that.user_id,
        room_id: room_id,
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
        my.room_ids.push(JSON.message);
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
  
  // send out a heartbeat to let the server know "I'm still here"
  my.heartbeat = function () {
    var url = my.heartbeatServer + "heartbeat",
      data = {
        for_user: that.user_id,
        room_ids: my.room_ids
      };
    that.send(url, "POST", data, function (JSON) {
    });
  };
  
  my.init = function () {
    my.checkInputsForTyping();
		that.login(user_id, avatar, alias, callback);
    return that;
  };
  
  return my.init();
};

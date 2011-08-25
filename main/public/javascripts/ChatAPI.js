/*global document: false, MyUtil, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

var MyUtil = (function (user_id, avatar, alias, callback) {
 var that = {};
 
 that.debug = function (msg) {
   if (window && window.console) {
     console.log(msg);      
   }
 };
 
 that.removeFromArray = function (arr, ele) {
   var index = $.inArray(ele, arr);
   if (index === -1) {
     return;
   }
   arr.splice(index, 1);
 };
 
 // serialize a dict to url form, key=value&key2=....
 that.serialize = function (dict) {
   "use strict";
   var vals = [],
     key;
   for (key in dict) {
     if (dict.hasOwnProperty(key)) {
       vals.push(key + '=' + dict[key]);
     }
   }
   return vals.join('&');
 };
 
 
 return that;
}());

var ChatAPI = function (user_id, avatar, alias, login_callback) {
  "use strict";
  var that = {},
  my = {};
  // my.home_url = "http://localhost:9000/";
  my.home_url = "http://10.0.1.50:9000/";  // dev  
  // my.home_url = "http://173.246.100.79/";  // prod 
  // my.home_url = "http://173.246.101.45/";  // staging
  // my.home_url = "http://173.246.101.127/";
  
  that.user_id = "";
  my.inputsToWatch = [];
  that.room_ids = [];
  my.session_id = null;
  that.im_talking_to = {}; // map user_ids to room_ids
  that.superPowers = []; // filled after login so clients can retrieve
  that.superPowerDetails = {};
  
  that.user = null;
  my.superhero_id = user_id;
  my.channels = {};
  my.broadcastHandler = false;
  my.pusher = false
  
  // send an API call, either POST, or GET, with given data.
  // on success the given callback function is called
  that.send = function (url, method, data, callback) {
    // add callback if this is cross domain
    if (url.indexOf(window.location.host) === -1) {
      url += "?callback=?";
    }
    
    if (that.user != null) {
      data["session"] = that.user.session;
    }
    var hash = url + "?" + MyUtil.serialize(data);
    
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

  that.getGroupChannel = function (groupKey) {
    var channel_name = "presence-group-" + groupKey;
    return RoomChannel({
	    channel_name: channel_name,
	    pusher: my.pusher
	  });
  };

  my.loginCallback = function (JSON, alias, callback) {
    if (JSON.hasOwnProperty("status") && JSON.status == "error") {
      callback(JSON);
      return;
    }
    var me = JSON[my.superhero_id];
    console.debug(me);
    that.user = User({user_id: me.id,
      session: me.session_id,
      alias: me.alias,
      avatar: me.avatar
    });
    
    that.superPowers = me.superPowers;
    that.superPowerDetails = me.superPowerDetails;  
    
    // MyContacts.put(that.user);
    that.user_id = that.user.user_id;
		if (callback) {
			callback(JSON);
		}
  };
    
  that.logout = function () {
    // var url = my.home_url + 'signout',
    //   data = {
    //     user_id: that.user_id,
    //   };
    //   that.send(url, "POST", data, callback);
  };
  
  that.login = function (user_id, avatar, alias, callback) {
    var url = my.home_url + 'signin',
      data = {
        sign_in_id: user_id,
        avatar: avatar,
        alias: alias,
      };
      that.send(url, "POST", data, function (JSON) {
        if (JSON.hasOwnProperty("status") && JSON.status == "error") {
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
  
  that.getBroadcastChannel = function () {
    return my.broadcastChannel;
  };
  
  that.bindNewPower = function (f) {
    my.userChannel.bindNewPower(f);
  };
  
  that.bindBroadcast = function (f) {
    my.broadcastHandler = f;
  };
  
  my.blacklistWrapper = function (msg) {
    if (msg.text == that.user.user_id) {
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
  
  my.initPusher = function (login_callback) {
    my.pusher = APusher({home_url: my.home_url});
		my.pusher.bindConnected(function() {	// wait to connect
		  that.login(user_id, avatar, alias, function (JSON_login) {  // send Play! our socket key
        my.pusher.setUserInfo(that.user.session, that.user.user_id);
    		my.userChannel = UserChannel({
          user: that.user,
          pusher: my.pusher
    		});     
    		   
		    my.sendMySocketID(function (JSON) {             
  			  if (JSON.status == "okay") {
      	
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
		my.initPusher(login_callback);
    return that;
  };
  
  return my.init();
};

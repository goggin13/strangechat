/*global document: false, Util, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

// should never leave debug statements in prod code, but at least
// here it won't break everything that doesn't support it (IE, I'm looking at you)
if (!console.debug) {
  console.debug = function (str) {};
}

// serialize a dict to url form, key=value&key2=....
var serialize = function (dict) {
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

// address book, maintains map of ids to names and servers
var MyContacts = (function () {
  "use strict";
  var my = {},
    that = {};
  my.contacts = {};
  
  that.get = function (id) {
    return my.contacts[id].name;
  };
  
  that.has = function (id) {
    return my.contacts.hasOwnProperty(id);
  };
  
  that.getServerFor = function (id) {
    return my.contacts[id].server;
  };

  that.getAliasFor = function (id) {
    return my.contacts[id].alias;
  };
  
  that.put = function (id, name, alias, server) {
    my.contacts[id] = {
      name: name,
      alias: alias,
      server: server
    };
  };
  
  return that;
}());

var ChatAPI = (function () {
  "use strict";
  var that = {},
    my = {};
  my.home_url = "http://localhost:9000/";
  // my.home_url = "http://10.0.1.50:9000/";
  // my.home_url = "http://173.246.102.246/";
  my.facebook_id = "";
  my.user_id = "";
  my.heartbeatServer = "";
  my.currentListen = "";
  my.HEARTBEAT_FREQUENCY = 5000;
  my.inputsToWatch = [];
  my.room_ids = [];
  my.lastReceived = 0;    
  my.session_id = -1;
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
    }
    
    var hash = url + "?" + serialize(data);
    
    if (hash.indexOf("heartbeat") === -1 && hash.indexOf('imtyping') === -1) {
      console.debug("GET " + hash);
    }
          
    if (method === 'POST') {
      
    } else {
      $.ajax({
          type: "GET",
          url: url,
          data: data,
          dataType: 'json',
          success: function(JSON) {
            if (!JSON.hasOwnProperty('status')) {
              console.debug(JSON);
            }
            callback(JSON, hash);
          },
          error: function (jqXHR, textStatus, errorThrown) {
            console.debug("ERROR!!!");
            console.debug(jqXHR);
            console.debug(textStatus);
            console.debug(errorThrown);
          }
      });
    }
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
        user_id: my.user_id,
        lastReceived: lastReceived
      };
    my.currentListen = that.send(url, "GET", data, function (JSON, hash) {
      var response = {};
      $.each(JSON, function (key, val) {
        if (val.data.type === "userlogon" && !MyContacts.has(val.data.new_user)) {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server);
        }
        if (val.data.type === "join") {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server);
        }     
        if (val.data.session_id == my.session_id ||
            (val.data.type !== "join" && // these are the only types 
            val.data.type !== "leave" && // that are session_id signed, currently
            val.data.type !== "userlogin" &&
            val.data.type !== "userlogout")) {
          response[key] = val;
        }
      });
      my.messageHandler(response);
      if (hash === my.currentListen) {
        that.listen(JSON[JSON.length - 1].id);
      }
    });
  };

  // log in the given user, and return a list of their friends
  that.login = function (facebook_id, name, avatar, alias, updatefriends, facebook_token, callback) {
    var url = my.home_url + 'signin',
      data = {
        facebook_id: facebook_id,
        access_token: facebook_token,
        name: name,
        avatar: avatar,
        alias: alias,
        updatefriends: updatefriends
      };
    my.facebook_id = facebook_id;
    that.send(url, "GET", data, function (JSON) {
      $.each(JSON, function (key, val) {
        MyContacts.put(key, val.name, val.alias, val.heartbeatServer.uri);
        if (val.name == name) {
          my.heartbeatServer = val.heartbeatServer.uri;
          my.session_id = val.session_id;
          my.user_id = val.id;
        }
      });
      that.listen(my.lastReceived);
      my.heartbeat();
      callback(JSON);
    });
  };
  
  // send a direct message to another friend
  that.directMessage = function (to, msg) {
    var url = MyContacts.getServerFor(to) + 'message',
      data = {
        from_user: my.user_id,
        for_user: to,
        msg: msg
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.roomMessage = function (to, msg, room_id) {
    var url =  MyContacts.getServerFor(to) + 'roommessage',
      data = {
        from_user: my.user_id,
        for_user: to,
        msg: msg,
        room_id: room_id
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };  
  
  // indicate you wish to get paired with a random user
  that.requestRandomRoom = function () {
    var url = my.home_url + 'requestrandomroom',
      data = {
        user_id: my.user_id
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.imTypingInRoom = function (to, text, room_id) {
    var url =  MyContacts.getServerFor(to) + 'imtyping',
      data = {
        for_user: to,
        user_id: my.user_id,
        room_id: room_id,
        text: text
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.leaveRoom = function (room_id) {
    var url = my.home_url + 'leaveroom',
      data = {
        user_id: my.user_id,
        room_id: room_id
      };
    Util.removeFromArray(my.room_ids, room_id);
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  // send out a heartbeat to let the server know "I'm still here"
  my.heartbeat = function () {
    var url = my.heartbeatServer + "heartbeat",
      data = {/*global document: false, Util, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

// should never leave debug statements in prod code, but at least
// here it won't break everything that doesn't support it (IE, I'm looking at you)
if (!console.debug) {
  console.debug = function (str) {};
}

// serialize a dict to url form, key=value&key2=....
var serialize = function (dict) {
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

// address book, maintains map of ids to names and servers
var MyContacts = (function () {
  "use strict";
  var my = {},
    that = {};
  my.contacts = {};
  
  that.get = function (id) {
    return my.contacts[id].name;
  };
  
  that.has = function (id) {
    return my.contacts.hasOwnProperty(id);
  };
  
  that.getServerFor = function (id) {
    return my.contacts[id].server;
  };

  that.getAliasFor = function (id) {
    return my.contacts[id].alias;
  };
  
  that.put = function (id, name, alias, server) {
    my.contacts[id] = {
      name: name,
      alias: alias,
      server: server
    };
  };
  
  return that;
}());

var ChatAPI = (function () {
  "use strict";
  var that = {},
    my = {};
  my.home_url = "http://localhost:9000/";
  // my.home_url = "http://10.0.1.50:9000/";
  // my.home_url = "http://173.246.102.246/";
  my.facebook_id = "";
  my.user_id = "";
  my.heartbeatServer = "";
  my.currentListen = "";
  my.HEARTBEAT_FREQUENCY = 5000;
  my.inputsToWatch = [];
  my.room_ids = [];
  my.lastReceived = 0;    
  my.session_id = -1;
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
    }
    
    var hash = url + "?" + serialize(data);
    
    if (hash.indexOf("heartbeat") === -1 && hash.indexOf('imtyping') === -1) {
      console.debug("GET " + hash);
    }
          
    if (method === 'POST') {
      
    } else {
      $.ajax({
          type: "GET",
          url: url,
          data: data,
          dataType: 'json',
          success: function(JSON) {
            if (!JSON.hasOwnProperty('status')) {
              console.debug(JSON);
            }
            callback(JSON, hash);
          },
          error: function (jqXHR, textStatus, errorThrown) {
            console.debug("ERROR!!!");
            console.debug(jqXHR);
            console.debug(textStatus);
            console.debug(errorThrown);
          }
      });
    }
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
        user_id: my.user_id,
        lastReceived: lastReceived
      };
    my.currentListen = that.send(url, "GET", data, function (JSON, hash) {
      var response = {};
      $.each(JSON, function (key, val) {
        if (val.data.type === "userlogon" && !MyContacts.has(val.data.new_user)) {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server);
        }
        if (val.data.type === "join") {
          MyContacts.put(val.data.new_user, val.data.name, val.data.alias, val.data.server);
        }     
        if (val.data.session_id == my.session_id ||
            (val.data.type !== "join" && // these are the only types 
            val.data.type !== "leave" && // that are session_id signed, currently
            val.data.type !== "userlogin" &&
            val.data.type !== "userlogout")) {
          response[key] = val;
        }
      });
      my.messageHandler(response);
      if (hash === my.currentListen) {
        that.listen(JSON[JSON.length - 1].id);
      }
    });
  };

  // log in the given user, and return a list of their friends
  that.login = function (facebook_id, name, avatar, alias, updatefriends, facebook_token, callback) {
    var url = my.home_url + 'signin',
      data = {
        facebook_id: facebook_id,
        access_token: facebook_token,
        name: name,
        avatar: avatar,
        alias: alias,
        updatefriends: updatefriends
      };
    my.facebook_id = facebook_id;
    that.send(url, "GET", data, function (JSON) {
      $.each(JSON, function (key, val) {
        MyContacts.put(key, val.name, val.alias, val.heartbeatServer.uri);
        if (val.name == name) {
          my.heartbeatServer = val.heartbeatServer.uri;
          my.session_id = val.session_id;
          my.user_id = val.id;
        }
      });
      that.listen(my.lastReceived);
      my.heartbeat();
      callback(JSON);
    });
  };
  
  // send a direct message to another friend
  that.directMessage = function (to, msg) {
    var url = MyContacts.getServerFor(to) + 'message',
      data = {
        from_user: my.user_id,
        for_user: to,
        msg: msg
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.roomMessage = function (to, msg, room_id) {
    var url =  MyContacts.getServerFor(to) + 'roommessage',
      data = {
        from_user: my.user_id,
        for_user: to,
        msg: msg,
        room_id: room_id
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };  
  
  // indicate you wish to get paired with a random user
  that.requestRandomRoom = function () {
    var url = my.home_url + 'requestrandomroom',
      data = {
        user_id: my.user_id
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.imTypingInRoom = function (to, text, room_id) {
    var url =  MyContacts.getServerFor(to) + 'imtyping',
      data = {
        for_user: to,
        user_id: my.user_id,
        room_id: room_id,
        text: text
      };
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  that.leaveRoom = function (room_id) {
    var url = my.home_url + 'leaveroom',
      data = {
        user_id: my.user_id,
        room_id: room_id
      };
    Util.removeFromArray(my.room_ids, room_id);
    that.send(url, "GET", data, function (JSON) {
    });
  };
  
  // send out a heartbeat to let the server know "I'm still here"
  my.heartbeat = function () {
    var url = my.heartbeatServer + "heartbeat",
      data = {
        for_user: my.user_id,
        room_ids: my.room_ids
      };
    console.debug(my.room_ids);
    that.send(url, "GET", data, function (JSON) {
      setTimeout(my.heartbeat, my.HEARTBEAT_FREQUENCY);
    });
  };
  
  my.init = function () {
    my.checkInputsForTyping();
    return that;
  };
  
  return my.init();
}());

        for_user: my.user_id,
        room_ids: my.room_ids
      };
    console.debug(my.room_ids);
    that.send(url, "GET", data, function (JSON) {
      setTimeout(my.heartbeat, my.HEARTBEAT_FREQUENCY);
    });
  };
  
  my.init = function () {
    my.checkInputsForTyping();
    return that;
  };
  
  return my.init();
}());

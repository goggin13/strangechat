/*global document: false, Util, $: false, base_url: false, alert: false, sign_up_in_prompt:false, AjaxLoader:false, SignUp:false, oApp, jQuery */
/*jslint white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */

window.isActive = true;
// set whether the window is active so we can
// display new messages gmail style
$(window).focus(function() {
  MyContacts.resetPageTitle();
  this.isActive = true; 
});
$(window).blur(function() { 
  this.isActive = false; 
});

// this is kind of cheap, but lets the first wave of old messages and such go by
// without trying to hit the page title for all of them
var startChecking = false;
setTimeout(function () {
  startChecking = true;
}, 3000);



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
  my.original_title = document.title;
  my.title_timer = null;
  
  // set the page title to reflect new chat with id
  that.setPageTitle = function (id, isJoin) {
    var toggle = false,
      newTitle = isJoin 
                 ? that.getAliasFor(id) + " enters"
                 : "new message from " + that.getAliasFor(id);
    
    if (!window.isActive && startChecking) { 
      
      document.title = newTitle;
      
      clearTimeout(my.title_timer);
      my.title_timer = setInterval(function () {
        if (toggle) {
          document.title = newTitle;
        } else {
          document.title = my.original_title;
        }
        toggle = !toggle;
      }, 2000);
    }
  };
  
  that.resetPageTitle = function () {
    document.title = my.original_title;
    clearTimeout(my.title_timer);
  };
  
  that.getIf = function (id, f) {
    if (!that.has(id)) {
      return "";
    } 
    return f();
  };
  
  that.getObj = function (id) {
    return that.getIf(id, function () { 
      return my.contacts[id];
    });
  };
  
  that.getSessionId = function (id) {
    return that.getIf(id, function () {
      return my.contacts[id].session;
    });
  };
  
  that.get = function (id) {
    return that.getIf(id, function () { 
      return my.contacts[id].name;
    });
  };
  
  that.getAliasFor = function (id) {
    return that.getIf(id, function () { 
      return my.contacts[id].alias;
    });  
  };

  that.getAvatarFor = function (id) {
    return that.getIf(id, function () { 
      return my.contacts[id].avatar;
    });
  };  
  
  that.has = function (id) {
    return my.contacts.hasOwnProperty(id);
  };
    
  that.remove = function (id) {
    delete my.contacts[id];
  };
  
  that.put = function (user) {
    my.contacts[user.user_id] = user;
  };
  
  return that;
}());
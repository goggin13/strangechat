/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, Event, types, console, HTTP, RoomChannel, MatchMaker, APusher, UserChannel, Channel, MyUtil, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

window.isActive = true;
// set whether the window is active so we can
// display new messages gmail style
$(window).focus(function() {
  "use strict";
  MyContacts.resetPageTitle();
  this.isActive = true; 
});
$(window).blur(function() { 
  "use strict";
  this.isActive = false; 
});

// address book, maintains map of ids to names and servers
var MyContacts = (function () {
  "use strict";
  var my = {},
    that = {};
  my.contacts = {};
  my.original_title = document.title;
  my.title_timer = null;
  
  // set the page title to reflect new chat with id
  that.setPageTitle = function (name, isJoin) {
    var toggle = false,
      newTitle = isJoin 
                 ? name + " enters"
                 : "new message from " + name;
    
    if (!window.isActive) { 
      
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
  
  return that;
}());
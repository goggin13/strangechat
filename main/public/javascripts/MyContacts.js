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
  
  return that;
}());
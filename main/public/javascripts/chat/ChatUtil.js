/*jslint eqeq: true, newcap: true, white: true, onevar: true, undef: true, nomen: true, regexp: true, plusplus: true, bitwise: true, maxerr: 50, indent: 2, browser: true */
/*global document, console, RoomChannel, MatchMaker, APusher, UserChannel, Channel, $, User, base_url, alert, sign_up_in_prompt, oApp, jQuery */

var MyUtil = (function (user_id, avatar, alias, callback) {
  "use strict";
  var that = {};

  that.debug = function (msg) {
    if (window && window.console) {
      console.log(msg);      
    }
  };

  that.toTitleCase = function (str) {
    return str.replace(/\w\S*/g, function (txt) {
      return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
    });
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

var HTTP = (function () {
  "use strict";
  var that = {};
  
  that.send = function (url, method, data, callback, errCallback) {
    var sendFunc = $.ajax,
      dataType = 'json',
      hash = url + "?" + MyUtil.serialize(data),
      success = false;
          
    // add callback if this is cross domain
    if (url.indexOf(window.location.host) === -1) {
      url += "?callback=?";
    }
    
    // cross domain will not fire error callback, but we'll set a timeout
    // to notify callers after 5 seconds
    setTimeout(function () {
      if (!success && errCallback) {  
        errCallback();
      }
    }, 5000); 
    
    MyUtil.debug("GET " + hash);
      
    // required for IE to not cache AJAX requests    
    $.ajaxSetup({
      cache: false
    });  
    
    sendFunc({
      type: "GET",
      url: url,
      data: data,
      dataType: dataType,
      success: function (JSON) {
        success = true;
        if (JSON.status === "error") {
          MyUtil.debug("ERROR!!!");
          MyUtil.debug(JSON);
        }
        if (callback) {
          callback(JSON, hash);
        }
      }
    });   
    return hash; 
  };
  
  return that;
}());


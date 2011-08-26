
var APusher = function (spec) {
  var that = {},
    my = {};
  my.home_url = spec.home_url;
  if (my.home_url.indexOf(window.location.host) === -1) {
    Pusher.channel_auth_endpoint = my.home_url + 'pusher/auth';
    Pusher.channel_auth_transport = 'jsonp';
  }
  // Pusher.log = function(message) {
  //   if (window.console && window.console.log) window.console.log(message);
  // };  
  my.pusher = new Pusher('c6c59a2e80e51c248a47');
  my.session = "";
  my.user_id = -1;
  
  that.isLocal = function () {
    return my.home_url.indexOf("localhost") > -1
           || my.home_url.indexOf("10.0.1.50") > -1;
  };
  
  that.setUserInfo = function (session, user_id) {
    my.session = session;
    my.user_id = user_id;
  };
  
  that.subscribe = function (channel) {
    return my.pusher.subscribe(channel);
  };
  
  that.unsubscribe = function (channel) {
    return my.pusher.unsubscribe(channel);
  };

  that.disconnect = function () {
    return my.pusher.disconnect();
  };

  that.bindConnected = function (f) {
    my.pusher.connection.bind('connected', f);
  };
  
  that.getSocketID = function () {
    return my.pusher.connection.socket_id;
  };

  that.push = function (channel, event, data) {
    var data = {
        user_id: my.user_id,
        event: event,
        channel: channel,
        message: data,
        session: my.session,
        socket_id: that.getSocketID()
      };   
    that.send("push", data); 
  };
  
  that.send = function (path, data) {
    var url = my.home_url + path;
    if (url.indexOf(window.location.host) === -1) {
      url += "?callback=?";
    }      
    
    // MyUtil.debug("GET " + url);
    // MyUtil.debug(data);
    
    $.ajaxSetup({cache: false});  // required for IE to not cache AJAX requests       
    $.getJSON(url, data, function (JSON) {
      if (JSON.status == "error") {
        MyUtil.debug("ERROR!!!");
        MyUtil.debug(JSON);
      }
    });    
  };
  
  return that;
};
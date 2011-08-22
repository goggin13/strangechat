
var JoinRequest = function (spec) {
  var that = Event(spec),
    my = {};
  that.type = "JoinRequest";
  return that;
};
  
var AcceptRequest = function (spec) {
  var that = Event(spec),
    my = {};
  that.type = "AcceptRequest";
  that.avatar = spec.avatar;
  that.alias = spec.alias;
  that.session = spec.session;  
  return that;  
};


var MatchMaker = function (spec) {
  var that = {},
    my = {};
  spec.channel_name = "presence-random-chat";
  my.randomChannel = Channel(spec);
  my.types = {
    JOIN_REQUEST: "JoinRequest",
    ACCEPT_REQUEST: "AcceptRequest",    
  };
  my.user = spec.user;
  my.pusher = spec.pusher;
  my.callback = spec.callback;
  
  that.membersToChannelName = function (user_id, user_id2) {
    var list = [user_id, user_id2];
    list.sort();
    return "presence-" + list[0] + "_" + list[1];
  };

  my.propsalFailed = function () {
    my.pendingResponse = false;
    my.waitingForChat = true;
  };

  my.canIPairWithMember = function (user_id) {
    return user_id 
           && my.user.user_id 
           && user_id != my.user.user_id;
  };
  
  my.proposeMeetUp = function (user_id) {
    my.pendingResponse = user_id;
    var join = JoinRequest({
      from: my.user.user_id,
      to: user_id
    });
    setTimeout(function () {
      my.propsalFailed();
    }, 1000);    
    my.randomChannel.push(my.types.JOIN_REQUEST, join.toJson());
  };
  
  my.acceptMeetUp = function (with_user_id) {
    var accept = AcceptRequest({
      from: my.user.user_id,
      to: with_user_id,
      session: my.user.session,
      avatar: my.user.avatar,
      alias: my.user.alias
    });
    my.randomChannel.push(my.types.ACCEPT_REQUEST, accept.toJson());
  };    
  
  that.matchMe = function () {
    var waitingForChat = true,
      sentAcceptTo = false;
    my.randomChannel.subscribe();
    
    my.randomChannel.bind(my.types.JOIN_REQUEST, function(data) {
      if (data.to == my.user.user_id
          && (waitingForChat || my.pendingResponse == data.from)) {
        my.acceptMeetUp(data.from); 
        sentAcceptTo = data.from;
      }
    });

    my.randomChannel.bind(my.types.ACCEPT_REQUEST, function (data) {
      if (data.to == my.user.user_id && waitingForChat) {
  			var userData = User({
  			    user_id: data.from,
  			    avatar: data.avatar,
  			    session: data.session,
  			    alias: data.alias
  			  }),
  			  channel_name = that.membersToChannelName(data.from, my.user.user_id),
  			  channel = RoomChannel({
  			    channel_name: channel_name,
  			    pusher: my.pusher
  			  });
  			
  			userData["channel"] = channel;
  			my.callback(userData);
  			waitingForChat = false;
  			if (sentAcceptTo != data.from) {
  			  my.acceptMeetUp(data.from);
  			  sentAcceptTo = data.from;
  			}
  			my.randomChannel.disconnect();
      }
    });

    my.randomChannel.bindLogin(function (members) {    
      var matched = false;
      members.each(function(member) {
        if (!matched && my.canIPairWithMember(member.info.user_id)) {
          matched = true;
          my.proposeMeetUp(member.info.user_id);
        }
      });
    });  

    my.randomChannel.bindLogon(function (user_id) {      
      if (my.waitingForChat && my.canIPairWithMember(user_id)) {
        my.proposeMeetUp(user_id);        
      }
    }); 

    my.randomChannel.bindLogoff(function (user_id) {    
      if (my.pendingResponse == user_id) {
        my.propsalFailed();
      }
    });    
  };
  
  return that;
};
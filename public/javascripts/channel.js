

var Channel = function () {
	var that = {},
		my = {};
	
	my.init = function () {
		
	};
	
	return my.init();
};

  label: "no-label",
  to_user: -1,
  from_user: -1,
  my = {},
  subscribe: function () {
		my.randomChatChannel
  },
  toString: function () {
    return "Channel - " + this.label;
  },
	
		my.randomChatChannel = my.pusher.subscribe(channels.RANDOM_CHANNEL);
		
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
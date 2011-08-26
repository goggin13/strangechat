
var AdminPage = function (API) {
  var that = {},
    my = {};
  my.API = API;
  my.broadcastChannel = my.API.getBroadcastChannel();
  my.userList = $("#user_list");
  
  my.banUser = function (user_id) {
    my.API.send('application/banUser', "GET", {ban_user_id: user_id});
  };
  
  my.updateHeader = function () {
    $("#user_count").text($(".online_user").length + " users");
  };
  
  my.removeUser = function (user) {
    $("#" + user.user_id).remove();
    my.updateHeader();
  };
  
  my.displayUser = function (user) {
    if (user.user_id == my.API.user.user_id 
        || $("#" + user.user_id).length != 0) {
      return;
    }
    var li = "<li class='online_user' id='" + user.user_id + "'>" + user.alias + "</li>";
    li = $(li).prepend("<img src='" + user.avatar + "' />");
    li.append(" -- <a class='ban_user' href='#'>ban</a>");
    my.userList.append(li);
    my.updateHeader();
  };
  
  my.handleLogon = function (users) {
    var any = false;
    $.each(users, function(i, user) {
      my.displayUser(user);
      any = true;
    });
    if (!any) my.updateHeader();
  };
  
  my.init = function () {
    my.broadcastChannel.bindLogin(my.handleLogon);
    my.broadcastChannel.bindLogon(my.displayUser);    
    my.broadcastChannel.bindLogoff(my.removeUser);    
    $(".ban_user").live("click", function(e) {
      var id = $(e.target).parent("li").attr("id"),
        answer = confirm("Really ban " + id + "?");
      if (answer){
      	my.banUser(id);
      }
      
      return false;
    });    
    return that;
  };
  
  return my.init();
};


$(document).ready(function () {
  if (!oApp) {
    return;
  }
  var API = ChatAPI(oApp.user_id, oApp.avatar, oApp.alias, function () {
    AdminPage(API);
  });
});



			
// Enable pusher logging - don't include this in production
Pusher.log = function (message) {
  if (window.console && window.console.log) {
    window.console.log(message);
  }
}
WEB_SOCKET_DEBUG = true;		
var API = ChatAPI(Math.round(Math.random() * 10000), "http://www.supermanhomepage.com/images/superman-returns7/sr-poster2.jpg", "superMan");
		
		
// var presence = pusher.subscribe('presence-in-random-chat');
// presence.bind('pusher:subscription_succeeded', function(members) {
//   alert(members.count + " members online");
// 
//   members.each(function(member) {
//   });
// });

    // var channel = pusher.subscribe('test_channel');
    // channel.bind('message', function(data) {
    //   alert(data);
    // });

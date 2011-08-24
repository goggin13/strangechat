


$.ajaxSetup({cache: false});  // required for IE to not cache AJAX requests    
$.ajax({
    type: "GET",
    url: "http://bnter.com/api/v1/user/conversations.json",
    data: {user_screen_name: "goggin13"},
    dataType: 'json',
    success: function(JSON) {
      print(JSON);
    },
    error: function (jqXHR, textStatus, errorThrown) {
      print("BAD RESPONSE");
      print(textStatus);
      print(errorThrown);
    }
});
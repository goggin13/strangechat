
$(document).ready(function () {
	
	$(".login_user").click(function () {
		var fb_id = $(this).attr('id'),
			fb_tok = $(this).attr('token')
			name = $(this).find(".name").text();
		
		ChatAPI.registerMessageHandler(function (JSON) {
			$.each(JSON, function (k, val) {
				$("#msg_list").append("<li>" + val.data.type + "</li>");
			});
		});
		
		ChatAPI.login(fb_id, name, "", "", true, fb_tok, function (JSON) {
			
			$(".login_user").remove();
			
			$.each(JSON, function (key, val) {
				if (val.user_id != fb_id) {
					$("#user_list").append("<li class='a_user' >" + val.name + "</li>");
					var input = $("<input type='text' id='" + val.user_id + "'>"),
						li = $("<li><input type='submit' value='send' /></li>").append(input);
					$("#user_list").append(li);
					ChatAPI.watchInput(input, val.user_id);
				}
			});
			
			$('input[type=submit]').click(function () {
				var input = $(this).siblings("input[type=text]"),
					to_id = input.attr('id'),
					text = input.val();
				ChatAPI.directMessage(to_id, text);
				input.val("");
			});
		});
		
		
	});

});
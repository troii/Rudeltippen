function load(item, url) {
	$('.nav-list > li').each(function(index) {
		if (item-1 == index) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.footer').hide();
	$('.span9').html('<p><img src="/public/images/ajax-loader.gif" border="0"/></p>');
	$('.span9').load(url, function() {
		$('.span9').fadeIn('slow');
	});
	$('.footer').delay(800).fadeIn('slow');
}

function loadJS(item, url) {
	$('.nav-list > li').each(function(index) {
		if (item-1 == index) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.footer').hide();
	$('.span9').html('<p><img src="/public/images/ajax-loader.gif" border="0"/></p>');
	$('.span9').load(url, function() {
		$('.span9').fadeIn('slow');
	});
	$('.footer').delay(800).fadeIn('slow');
}

function helpify() {
	$('.btn-primary').click(function() {
		var btn = $(this)
		btn.button('loading')
	});
	$('.stadium').on().tooltip();
	$('img').on().tooltip();
}

function showCredits() {
	$('#modalCredits').modal('show');
}

$(document).on('DOMNodeInserted', function(e) {
	helpify();
});

$(document).ready(function(){
	$('.alert-info').delay(5000).slideToggle();
	$('.alert-success').delay(5000).slideToggle();
	helpify();
});

function showEditUser() {
	$('#editUserModal').load("/admin/edituser/1");
	$('#editUserModal').modal('show');
}
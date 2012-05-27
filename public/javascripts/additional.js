function load(item, url) {
	$('.nav-list > li').each(function(index) {
		if (item-1 == index) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.span9').load(url).hide().fadeIn('slow');
}

function loadJS(item, url) {
	$('.nav-list > li').each(function(index) {
		if (item-1 == index) {
			$(this).addClass('active');
		} else {
			$(this).removeClass('active');
		}
	});
	$('.span9').load(url).hide().fadeIn('slow');
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
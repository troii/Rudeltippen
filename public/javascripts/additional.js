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

function showCredits() {
	$('#modalCredits').modal('show');
}

$(document).ready(function(){
	$(".alert-info").delay(5000).slideToggle();
	$(".alert-success").delay(5000).slideToggle();
	$('#fat-btn').click(function() {
		var btn = $(this)
		btn.button('loading')
	});
	$('.stadium').tooltip();
	$('img').tooltip();
});
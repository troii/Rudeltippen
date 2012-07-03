function load(item, url) {
	$('.nav-list > li').each(function(index) {
		var navItem = $(this)[0].attributes[0].nodeValue;
		$(this).removeClass('active');
		if (item == 0) {
			$('#nav-first').addClass('active');
		} else if (item == 1) {
			$('#nav-1').addClass('active');
		} else if (navItem == "nav-" + item) {
			$(this).addClass('active');
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
		var navItem = $(this)[0].attributes[0].nodeValue;
		$(this).removeClass('active');
		if (item == 0) {
			$('#nav-first').addClass('active');
		} else if (item == 1) {
			$('#nav-1').addClass('active');
		} else if (navItem == "nav-" + item) {
			$(this).addClass('active');
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
	$('.admintooltip').on().tooltip();
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
	window.addEventListener("load", function() {
		setTimeout(function() {
			window.scrollTo(0, 1);
		}, 0);
	});
	if ($('.datatable').length != 0) {
	    $('.datatable').dataTable( {
	    	"bPaginate": false,
	    	"bInfo": false,
	    	"aaSorting": [[1,'asc']],
	    	"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0,4,5,6 ] } ],
	    	"oLanguage": {
	            "sSearch": "<i class=\"icon-search\"></i>"
	        }
	    } );		
	}
});
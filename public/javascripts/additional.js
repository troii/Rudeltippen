function showCredits() {
        $('#modalCredits').modal('show');
}

function helpify() {
	$('.btn-success').click(function() {
		var btn = $(this)
		btn.button('loading')
	});
	$('img').on().tooltip();
	$('.admintooltip').on().tooltip();
}

$(document).on('DOMNodeInserted', function(e) {
	helpify();
});

$(document).ready(function(){
	$('.alert-warning').delay(4000).slideToggle();
	$('.alert-success').delay(4000).slideToggle();
	$('.alert-danger').delay(4000).slideToggle();
	helpify();
	window.addEventListener("load", function() {
		setTimeout(function() {
			window.scrollTo(0, 1);
		}, 0);
	});
	if ($('.usermanagement').length != 0) {
	    $('.usermanagement').dataTable( {
	    	"bPaginate": false,
	    	"bInfo": false,
	    	"aaSorting": [[1,'asc']],
	    	"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0,4,5,6 ] } ],
	    	"oLanguage": {
	            "sSearch": "<i class=\"icon-search\"></i>"
	        }
	    } );		
	}
    $('#fat-btn-loading').click(function() { 
    	$.blockUI({ message: '<h2>Calculating points...<br/>This may take a moment!<h2><img src="/public/img/ajax-loader.gif" />' });
        setTimeout($.unblockUI, 60000); 
    }); 
});
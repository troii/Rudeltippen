$('#ajaxplayday').hide();
$('#ajaxloader').hide();
$('#ajaxusers').hide();

function helpify() {
	$('.btn-primary').click(function() {
		var btn = $(this)
		btn.button('loading')
	});
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
	$('.alert-info').delay(4000).slideToggle();
	$('.alert-success').delay(4000).slideToggle();
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
	if ($('.standings').length != 0) {
	    $('.standings').dataTable( {
	    	"bPaginate": false,
	    	"bInfo": false,
	    	"aaSorting": [[0,'asc']],
	    	"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 1 ] } ],
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

function lazy() {
	 var start = $('#lazyTable tr').length;
     var users = $('#ajaxusers').text();
     
     if (start < users) {
    	$('#lazybutton').hide();
     	$('#ajaxloader').show();
     	var playday = $('#ajaxplayday').text();
         $.ajax({
             cache: false,
             url: '/overview/playday/' + playday + '/' + start,
             success: function(html) {
                 if (html) {
                     $('#lazyTable tr:last').after(html);
                 }
                 $('#ajaxloader').hide();
                 $('#lazybutton').show();
             }
         });
     }	
}

var url = $.url(); 
var tab = url.attr('fragment');
if (tab) {
    $('ul.nav-tabs').children().removeClass('active');
    $('a[href=#'+ tab +']').parents('li:first').addClass('active');
    $('div.tab-content').children().removeClass('active');
    $('#' + tab).addClass('active');
    $('#' + tab).show();
}
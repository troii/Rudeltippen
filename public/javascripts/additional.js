function showCredits() {
	$('#modalCredits').modal('show');
}

$(document).ready(function(){
	$('.alert-info').delay(4000).slideToggle();
	$('.alert-success').delay(4000).slideToggle();
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

$(window).data('ajaxready', true).scroll(function(e) {
	if ($(window).data('ajaxready') == false) return;
    if ($(window).scrollTop() >= ($(document).height() - $(window).height())) {
        $('#ajaxloader').show();
        $(window).data('ajaxready', false);
        var start =	$('#lazyTable tr').length;
        $.ajax({
            cache: false,
            url: '/standings/lazy/' + start,
            success: function(html) {
                if (html) {
                    $('#lazyTable tr:last').after(html);
                }
                $('#ajaxloader').hide();
                $(window).data('ajaxready', true);
            }
        });
    }
});
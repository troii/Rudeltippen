$(document).ready(function(){
	$(".pagination").rPage();
	$('.credits-modal').click(function() {
		$('#credits').modal();
	}); 
	$('.alert-warning').delay(20000).slideToggle();
	$('.alert-success').delay(5000).slideToggle();
	$('.alert-danger').delay(10000).slideToggle();
	$('.btn-success').click(function() {
		var btn = $(this);
		btn.button('loading');
	});   	
	$('img').on().tooltip();
	$('.admintooltip').on().tooltip();
	$('.jobtooltip').on().tooltip();
	window.addEventListener("load", function() {
		setTimeout(function() {
			window.scrollTo(0, 1);
		}, 0);
	});
    $('#calculations').click(function() {
    	$.blockUI({ message: "<h2>Calculating...<br/>This may take a minute!</h2>"});
        setTimeout($.unblockUI, 100000); 
    }); 
    if ($('.editable').length > 0){
    	$('.editable').editable(
        		{
        			success: function(response, newValue) {
       					iosOverlay({
		        			text: "Saved!",
		        			duration: 2e3,
		        			icon: "/public/img/check.png"
		        		});
        			}	
        		}
        	);
        	$(".updateable").click(function() {
        		var url = $(this).attr("data-url");
        		if (url != null) {
        			$.get(url)
        			.done(function() {
        				iosOverlay({
			        		text: "Saved!",
			        		duration: 2e3,
			        		icon: "/public/img/check.png"
			        	});
        			})
        			.fail(function() {
        				iosOverlay({
			        		text: "Error!",
			        		duration: 2e3,
			        		icon: "/public/img/cross.png"
			        	});
        			});
        		}
        });	        
    }
    $('#extratips').on('hidden.bs.collapse', function () {
    	$('#collapseExtra').text("&{'app.show'}");
    });
    $('#extratips').on('show.bs.collapse', function () {
    	$('#collapseExtra').text("&{'app.hide'}");
    })
});
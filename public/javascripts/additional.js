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

//http://stackoverflow.com/questions/6549223/javascript-code-to-display-twitter-created-at-as-xxxx-ago
function prettyDate(time){
    var system_date = new Date(Date.parse(time));
    var user_date = new Date();

    var diff = Math.floor((user_date - system_date) / 1000);
    if (diff <= 1) {return "Gerade eben";}
    if (diff < 60) {return "Vor " + diff + " Sekunden";}
    if (diff <= 90) {return "Vor einer Minute";}
    if (diff <= 3540) {return "Vor " + Math.round(diff / 60) + " Minuten";}
    if (diff <= 5400) {return "Vor 1 Stunde";}
    if (diff <= 86400) {return "Vor " + Math.round(diff / 3600) + " Stunden";}
    if (diff <= 129600) {return "Vor 1 Tag";}
    if (diff < 604800) {return "Vor " + Math.round(diff / 86400) + " Tagen";}
    if (diff <= 777600) {return "Vor 1 Jahr";}
    return "am " + system_date;

}

function linkify(text) {
    text = text.replace(/(https?:\/\/\S+)/gi, function (s) {
        return '<a target="_blank" href="' + s + '">' + s + '</a>';
    });

    text = text.replace(/(^|)@(\w+)/gi, function (s) {
        return '<a target="_blank" href="http://twitter.com/' + s + '">' + s + '</a>';
    });

    text = text.replace(/(^|)#(\w+)/gi, function (s) {
        return '<a target="_blank" href="http://search.twitter.com/search?q=' + s.replace(/#/,'%23') + '">' + s + '</a>';
     });
    return text;
}

$(document).ready(function(){
	$(".alert-info").delay(4000).slideToggle();
	$(".alert-success").delay(4000).slideToggle();

	var username = $('body').data('key');
	if (username != "") {
		$.ajax({
		    type:'GET',
		    dataType:'jsonp',
		    url:'http://api.twitter.com/1/statuses/user_timeline.json?count=5',
		    data:{screen_name:username, include_rts:0},
		    success:function(data, textStatus, XMLHttpRequest) {
		        var tmp = false;
		        var results = $('.span6 > .twitter');
		        for(i in data) {
		        	tmp = $('<h5>'+prettyDate(data[i].created_at)+'</h5><p>'+linkify(data[i].text)+'</p>');
		            results.append(tmp);
		        }
		    },
		    error:function(req, status, error) {
		        alert('error: '+status);
		    }
		});
	}
});
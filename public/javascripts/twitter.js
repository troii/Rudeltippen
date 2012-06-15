function prettyDate(time){
    var system_date = new Date(Date.parse(time));
    var user_date = new Date();

    var diff = Math.floor((user_date - system_date) / 1000);
    if (diff <= 1) {return i18n('js.justnow');}
    if (diff < 60) {return i18n('js.secondsago', diff);}
    if (diff <= 90) {return i18n('js.oneminuteago');}
    if (diff <= 3540) {return i18n('js.secondsago', Math.round(diff / 60));}
    if (diff <= 5400) {return i18n('js.onehourago');}
    if (diff <= 86400) {return i18n('js.hoursago', Math.round(diff / 3600));}
    if (diff <= 129600) {return i18n('js.onedayago');}
    if (diff < 604800) {return i18n('js.daysago', Math.round(diff / 86400));}
    if (diff <= 777600) {return i18n('js.oneyearago');}

    return i18n('js.at') + " " + system_date;
}

function linkify(text) {
    text = text.replace(/(https?:\/\/\S+)/gi, function (s) {
        return '<br /><a target="_blank" href="' + s + '">' + s + '</a>';
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
	var username = $('body').data('key');
	if (username != "") {
		$.ajax({
		    type:'GET',
		    dataType:'jsonp',
		    url:'https://api.twitter.com/1/statuses/user_timeline.json?count=4',
		    data:{screen_name:username, include_rts:0},
		    success:function(data, textStatus, XMLHttpRequest) {
		    	$('#ajaxloader').hide();
		        var tmp = false;
		        var results = $('.span5 > .twitter');
		        for(i in data) {
		        	tmp = $('<h4><a href="https://twitter.com/'+username+'/status/'+data[i].id_str+'" target="_blank">'+prettyDate(data[i].created_at)+'</a></h4><p>'+linkify(data[i].text)+'</p>');
		            results.append(tmp);
		        }
		    },
		    error:function(req, status, error) {
		        alert('error: '+status);
		    }
		});
	}
});
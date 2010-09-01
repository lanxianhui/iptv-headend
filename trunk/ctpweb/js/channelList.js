function initChannels() {
	var channels = $$('.channel');
	$each(channels, function(channel) {
		var links = channel.getElementsByTagName('a');
		var href = links[0].href;
		channel.addEvent('click', function() {
			playAnything(href);
		});
		links[0].href = "#";
	});
}

function bootScripts() {
	initChannels();
}

window.addEvent('domready', bootScripts);
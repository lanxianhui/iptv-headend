var mediaplayer;
var lastUrl = "";

var localChannels = null;
var channelPointer = 0;
var channelId = 0;

function Settings(volume) {
	this.volume = volume;
}

function loadProgram(program) {
	var totalTime = (program.end - program.begin) * 1000;
	var mediabar = mediaplayer.getMediabar();
	mediabar.setTotalTime(totalTime);
	mediabar.setLive(false);
	
	var vlc = mediaplayer.getPlayer();
	var id = vlc.playlist.add("http://vegas.cm.p.lodz.pl/pvr/" + program.recording.fileName);
	vlc.playlist.playItem(id);
	
	vlc.audio.volume = mediabar.getVolume() * 100;
}

function findChannel(id, channels) {
	for (var i=0; i<channels.length; i++) {
		if (channels[i].id == id) {
			return i;
		}
	}
	return -1;
}

function loadChannels(channels) {
	localChannels = channels;
	channelPointer = findChannel(channelId, localChannels);
	
	var mediabar = mediaplayer.getMediabar();
	mediabar.setLive(true);
	/* var totalTime = (program.end - program.begin) * 1000;
	mediabar.setTotalTime(totalTime); */
	
	var vlc = mediaplayer.getPlayer();
	var id = vlc.playlist.add(localChannels[channelPointer].unicastUrl);
	vlc.playlist.playItem(id);
	
	vlc.audio.volume = mediabar.getVolume() * 100;
}

function loadResult(result) {
	if (result.recording != null) {
		loadProgram(result);
	} else if (result[0].lcn) {
		loadChannels(result);
	}
}

function downloadData(url) {
    var jsonRequest = new Request.JSON({
        method: "get",
        url: url,
        onSuccess: loadResult,
        headers: {
            Accept: "application/json"
        }
    });
    jsonRequest.send();
}

function beginPlaybackFromUrl() {
	var urlQuery = location.href.split("#");
	var urlParams = urlQuery[1].split(",");
	var type = urlParams[0]; 
	var id = urlParams[1];
	if (type == "program") {
		downloadData("mediaplayer.php?id=" + id + "&type=" + type);
	} else if (type == "channel") {
		channelId = id;
		downloadData("mediaplayer.php?type=channels");
	}
}

function hasUrlChanged() {
	var currentUrl = location.href;
	
	if (currentUrl != lastUrl) {
		beginPlaybackFromUrl();
		lastUrl = currentUrl;
	}
	
	setTimeout(hasUrlChanged, 200);
}

function loadSettings() {
	while (typeof mediaplayer.getPlayer() == "undefined") {
		
	}
	
	var settingsCookie = Cookie.read('settings');
	if (settingsCookie != null) {
		var settingsObject = JSON.decode(settingsCookie);
		
		var mediabar = mediaplayer.getMediabar();
		mediabar.setVolume(settingsObject.volume);
		if (settingsObject.volume == 0) {
			mediabar.setMute(true);
		}
		
		var vlc = mediaplayer.getPlayer();
		vlc.audio.volume = settingsObject.volume * 100;
	} 
}

function bootscripts() {
	mediaplayer = new Media($('media'), "");
	hasUrlChanged();
	loadSettings();
}

function handleUnload(){
	var vlc = mediaplayer.getPlayer();
	var volume = (vlc.audio.volume / 100);
	settings = new Settings(volume);
	
	var settingsCookie = Cookie.write('settings', JSON.encode(settings));
}

window.addEvent('domready', bootscripts);
window.addEvent('beforeunload', handleUnload);


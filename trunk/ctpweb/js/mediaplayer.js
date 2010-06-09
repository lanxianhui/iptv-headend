var mediaplayer;
var lastUrl = "";

var localChannels = null;
var localProgram = null;
var channelPointer = 0;
var channelId = 0;
var isLive = false;

function Settings(volume) {
	this.volume = volume;
}

function loadProgram(program) {
	isLive = false;
	localProgram = program;
	document.title = program.title;
	
	if (localChannels == null) {
		setTimeout(loadProgram, 300, program);
		return;
	}
	
	programChannelPointer = findChannel(program.tvChannelId, localChannels);
	if (programChannelPointer != -1) {
		preRoll = localChannels[programChannelPointer].preRoll;
		postRoll = localChannels[programChannelPointer].postRoll;
	}
	
	begin = program.begin - preRoll;
	end = program.end + postRoll;
	var totalTime = (end - begin) * 1000;
	
	var mediabar = mediaplayer.getMediabar();
	mediabar.reset();
	mediabar.setTotalTime(totalTime);
	if (program.recording.mode == "PROCESSING") {
		var currentDate = new Date().getTime();
		var primaryTime = currentDate - (begin * 1000);
		mediabar.setPrimaryTime(primaryTime);
		mediabar.setTrickplay(true);
	} else {
		mediabar.setPrimaryTime(0);
		mediabar.setTrickplay(false);
	}
	mediabar.setLive(false);
	
	var vlc = mediaplayer.getPlayer();
	var id = vlc.playlist.add("http://vegas.cm.p.lodz.pl/pvr/" + program.recording.fileName);
	vlc.playlist.playItem(id);
	
	vlc.audio.volume = mediabar.getVolume() * 100;
}

function findChannel(id, channels) {
	if (channels != null) {
		for (var i=0; i<channels.length; i++) {
			if (channels[i].id == id) {
				return i;
			}
		}
	}
	return -1;
}

function storeChannels(channels) {
	localChannels = channels;
}

function playChannels() {
	isLive = true;
	if (localChannels == null) {
		setTimeout(playChannels, 300);
		return;
	}
	
	channelPointer = findChannel(channelId, localChannels);
	
	if (channelPointer == -1)
		channelPointer = 0;
	
	document.title = localChannels[channelPointer].name;
	
	var mediabar = mediaplayer.getMediabar();
	mediabar.reset();
	mediabar.setLive(true);
	mediabar.setTrickplay(false);
	mediabar.setTotalTime(0);
	mediabar.setPrimaryTime(0);
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
		storeChannels(result);
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
		playChannels();
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
	if (typeof mediaplayer.getPlayer() == "undefined") {
		setTimeout(loadSettings, 300);
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

function channelPlus() {
	if (isLive) {
		var nextChannelPointer = channelPointer + 1;
		if (nextChannelPointer < 0)
			nextChannelPointer = localChannels.length - 1;
		if (nextChannelPointer >= localChannels.length)
			nextChannelPointer = 0;
		var nextChannel = localChannels[nextChannelPointer].id;
		
		var urlParams = location.href.split("#");
		location.href = urlParams[0] + "#" + 'channel' + ',' + nextChannel;
	}
}

function channelMinus() {
	if (isLive) {
		var previousChannelPointer = channelPointer - 1;
		if (previousChannelPointer < 0)
			previousChannelPointer = localChannels.length - 1;
		if (previousChannelPointer >= localChannels.length)
			previousChannelPointer = 0;
		var previousChannel = localChannels[previousChannelPointer].id;
		
		var urlParams = location.href.split("#");
		location.href = urlParams[0] + "#" + 'channel' + ',' + previousChannel;
	}
}

function playbackPlayPause() {
	if (!isLive) {
		var vlc = mediaplayer.getPlayer();
		vlc.playlist.togglePause();
	}
}

function fullScreen() {
	var vlc = mediaplayer.getPlayer();
	vlc.video.fullscreen = true;
}

function soundPlus() {
	var mediabar = mediaplayer.getMediabar();
	var volume = mediabar.getVolume();
	
	volume = volume + 0.05;
	
	if (volume < 0) {
		volume = 0;
	} else if (volume > 1) {
		volume = 1;
	}
	
	mediabar.setVolume(volume);
	if (volume == 0) {
		mediabar.setMute(true);
	} else {
		mediabar.setMute(false);
	}
	
	var vlc = mediaplayer.getPlayer();
	vlc.audio.volume = volume * 100;
}

function soundMinus() {
	var mediabar = mediaplayer.getMediabar();
	var volume = mediabar.getVolume();
	
	volume = volume - 0.05;
	
	if (volume < 0) {
		volume = 0;
	} else if (volume > 1) {
		volume = 1;
	}
	
	mediabar.setVolume(volume);
	if (volume == 0) {
		mediabar.setMute(true);
	} else {
		mediabar.setMute(false);
	}
	
	var vlc = mediaplayer.getPlayer();
	vlc.audio.volume = volume * 100;
}

function bootscripts() {
	// Bootstrap our list of channels
	downloadData("mediaplayer.php?type=channels");
	
	mediaplayer = new Media($('media'), "");
	
	var mediabar = mediaplayer.getMediabar();
	mediabar.addEvent('liveclick', function(event) {
		var urlParams = location.href.split("#");
		location.href = urlParams[0] + "#" + 'channel' + ',' + localProgram.tvChannelId;
	});
	mediabar.addEvent('chplusclick', function(event) {
		channelPlus();
	});
	mediabar.addEvent('chminusclick', function(event) {
		channelMinus();
	});
	window.addEvent('keyup', function(event) {
		if ((event.code == 33) | (event.code == 176)) {
			channelPlus();
		} else if ((event.code == 34) | (event.code == 177)) {
			channelMinus();
		} else if ((event.code == 32) | (event.code == 179)) {
			playbackPlayPause();
		} else if (event.code == 38) {
			soundPlus();
		} else if (event.code == 40) {
			soundMinus();
		} else if ((event.code == 13) && (event.alt == true)) {
			fullScreen();
		}
	});
	// Support for mouse wheel sound changing
	window.addEvent('mousewheel', function(event) {
		var rolled;
		if (event.wheelDelta === undefined) {   // Firefox
			rolled = -40 * event.detail;
	    }
	    else {
	        rolled = event.wheelDelta;
	    }
		if (rolled < 0) {
			soundMinus();
		} else if (rolled > 0) {
			soundPlus();
		}
	});
	
	hasUrlChanged();
	loadSettings();
}

function handleUnload(){
	var vlc = mediaplayer.getPlayer();
	var volume = (vlc.audio.volume / 100);
	settings = new Settings(volume);
	
	var settingsCookie = Cookie.write('settings', JSON.encode(settings), {
		duration: 366
	});
}

window.addEvent('domready', bootscripts);
window.addEvent('beforeunload', handleUnload);


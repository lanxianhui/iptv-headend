var mediaplayer;
var lastUrl = "";

function Settings(volume) {
	this.volume = volume;
}

function loadProgram(program) {
	var totalTime = (program.end - program.begin) * 1000;
	var mediabar = mediaplayer.getMediabar();
	mediabar.setTotalTime(totalTime);
	
	var vlc = mediaplayer.getPlayer();
	var id = vlc.playlist.add("http://vegas.cm.p.lodz.pl/pvr/" + program.recording.fileName);
	vlc.playlist.playItem(id);
	
	vlc.audio.volume = mediabar.getVolume() * 100;
}

function downloadData(url, callback){
    var jsonRequest = new Request.JSON({
        method: "get",
        url: url,
        onSuccess: loadProgram,
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
	downloadData("mediaplayer.php?id=" + id + "&type=" + type);
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
		
		var vlc = mediaplayer.getPlayer();
		vlc.audio.volume = settingsObject.volume * 100;
		
		var mediabar = mediaplayer.getMediabar();
		mediabar.setVolume(settingsObject.volume);
	} 
}

function bootscripts() {
	mediaplayer = new Media($('media'), "");
	loadSettings();
	hasUrlChanged();
}

function handleUnload(){
	var vlc = mediaplayer.getPlayer();
	var volume = (vlc.audio.volume / 100);
	settings = new Settings(volume);
	
	var settingsCookie = Cookie.write('settings', JSON.encode(settings));
}

window.addEvent('domready', bootscripts);
window.addEvent('beforeunload', handleUnload);


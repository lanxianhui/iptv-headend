function play(){
	var vlc = document.getElementById("vlcplugin");
	var itemId = vlc.playlist.add("http://vegas.cm.p.lodz.pl/pvr/d63f5a627ef95afbdf2556efdb83cc4e.ts");
	vlc.playlist.playItem(itemId);
}

function stop(){
	var vlc = document.getElementById("vlcplugin");
	vlc.playlist.stop();
}

function bootScripts(){
	var vlcObject = new VLCObject("vlcplugin", "640", "360", "0.8.6");
	vlcObject.write("mediaplayer");
}

window.addEvent('domready', bootScripts);

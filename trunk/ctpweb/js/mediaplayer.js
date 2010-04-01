function registerVLCEvent(event, handler)
{
   var vlc = getVLC("vlc");
   if (vlc) {
       if (vlc.attachEvent) {
           // Microsoft
           vlc.attachEvent (event, handler);
       } else if (vlc.addEventListener) {
           // Mozilla: DOM level 2
           vlc.addEventListener (event, handler, false);
       } else {
           // DOM level 0
           eval("vlc.on" + event + " = handler");
       }
   }
}

function unregisterVLCEvent(event, handler)
{
   var vlc = getVLC("vlc");
   if (vlc) {
       if (vlc.detachEvent) {
           // Microsoft
           vlc.detachEvent (event, handler);
       } else if (vlc.removeEventListener) {
           // Mozilla: DOM level 2
           vlc.removeEventListener (event, handler, false);
       } else {
           // DOM level 0
           eval("vlc.on" + event + " = null");
       }
   }
}


function getVLC(name)
{
	return document.getElementById(name);
}

function bootScripts(){

}

function play(){
	//var vlc = $('vlc');
	var vlc = getVLC('vlc');
	var id = vlc.playlist.add("http://vegas.cm.p.lodz.pl/pvr/4fb4bb05cae5a43fcf8c29a323a13712.ts");
	vlc.playlist.playItem(id);
	//alert(vlc.VersionInfo);
}

function stop(){
	//var vlc = $('vlc');
	var vlc = getVLC('vlc');
	vlc.playlist.stop();
}

window.addEvent('domready', bootScripts);

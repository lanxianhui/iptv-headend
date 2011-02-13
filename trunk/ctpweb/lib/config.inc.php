<?php
	/*
		This is the config file for the CTPweb component
		
		Please, before any other setup, see the "salt.inc.php" to set up the password salt.
	*/

	$config["database"]["host"] = "localhost";
	$config["database"]["name"] = "cm_iptv";
	$config["database"]["username"] = "iptv-test";
	$config["database"]["password"] = "iptv-test";
	
	// The root url of the appliaction
	$config["app"]["url"] = "http://vegas.cm.p.lodz.pl/ctpweb/";
	
	// The root url of the folder in which nPVRd stores all the recordings: can be HTTP
	// or RTSP, if you have a RTSP server installed.
	$config["pvr"]["url"] = "http://vegas.cm.p.lodz.pl/pvr/"; 
	// The root url of the folder in which nPVRd recordings are stored: HTTP only.
	$config["pvr"]["downloadUrl"] = "http://vegas.cm.p.lodz.pl/pvr/";
	// 10h of recording by default, will be used for every new user
	$config["pvr"]["userQuota"] = 600; 
	// 30 days of holding possible, has to be equal to what nPVRd has in it's config file.
	$config["pvr"]["maxHold"] = 60 * 24 * 30; 
?>
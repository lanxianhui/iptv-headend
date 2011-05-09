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
	
	
	// Multicast default policy: ALLOW (true) or DENY (false)
	$config["multicast"]["default"] = false;
	// These are stored in the following configuration: network address, network mask
	// Multicast whitelist (ALLOW)
	$config["multicast"]["allow"] = array(
		array("192.168.0.0", "255.255.0.0")
	);
	// Multicast blacklist (DENY)
	$config["multicast"]["deny"] = array(
	);
?>
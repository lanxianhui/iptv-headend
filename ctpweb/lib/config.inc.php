<?php
	$config["database"]["host"] = "localhost";
	$config["database"]["name"] = "cm_iptv";
	$config["database"]["username"] = "iptv-test";
	$config["database"]["password"] = "iptv-test";
	
	$config["app"]["url"] = "http://vegas.cm.p.lodz.pl/ctpweb/";
	
	$config["pvr"]["url"] = "http://vegas.cm.p.lodz.pl/pvr/";
	$config["pvr"]["downloadUrl"] = "http://vegas.cm.p.lodz.pl/pvr/";
	$config["pvr"]["userQuota"] = 600; // 10h of recording by default
	$config["pvr"]["maxHold"] = 60 * 24 * 30; // 30 days of holding possible 
?>
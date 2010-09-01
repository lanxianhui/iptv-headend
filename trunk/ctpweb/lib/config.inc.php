<?php
	$config["database"]["host"] = "localhost";
	$config["database"]["name"] = "cm_iptv";
	$config["database"]["username"] = "iptv-test";
	$config["database"]["password"] = "iptv-test";
	
	$config["app"]["url"] = "http://vegas.cm.p.lodz.pl/ctpweb/";
	
	$config["app"]["passwordSalt"] = "Centrum Multimedialne CTP";
	
	$config["pvr"]["url"] = "rtsp://localhost/pvr/";
	$config["pvr"]["downloadUrl"] = "http://localhost:81/";
	$config["pvr"]["userQuota"] = 600; // 10h of recording by default
?>
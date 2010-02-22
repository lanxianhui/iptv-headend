<?php
	require_once 'lib/base.inc.php';
	
	$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
	
	$channels = EpgLogic::getChannels(&$conn);
	
	print_r($channels);
?>
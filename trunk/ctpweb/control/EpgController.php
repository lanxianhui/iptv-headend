<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');

class EpgController {
	
	var $channels;
	
	function EpgController($config, &$smarty) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
	
		$channels = EpgLogic::getChannels(&$conn);
		foreach ($channels as &$channel) {
			$date = time();
			$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date);
			$channel->setPrograms($programs);
		}
		
		$smarty->assign('channels', $channels);
		$smarty->display('epg.html');
	}
	
}

?>
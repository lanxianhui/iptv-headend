<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');

class MediaPlayerController {
	
	var $channels;
	
	function MediaPlayerController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
	
		$channels = EpgLogic::getChannels(&$conn);
		foreach ($channels as &$channel) {
			$date = time();
			$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date);
			$channel->setPrograms($programs);
		}
		
		if (($_SERVER["HTTP_ACCEPT"] == "application/json") | ($_GET["format"] == "json")) {
			print(json_encode($channels));
			exit;
		} else {
			$smarty = new Smarty();
			$smarty->template_dir = "./tpl";
			$smarty->compile_dir = "./tpl_c";
			$smarty->cache_dir = "./cache";
					
			$smarty->assign('channels', $channels);
			$smarty->display('mediaplayer.html');
		}
	}
	
}

?>
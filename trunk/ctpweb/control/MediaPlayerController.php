<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');

class MediaPlayerController {
	
	var $program;
	var $channel;
	var $mode;
	
	function MediaPlayerController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
	
		/* $channels = EpgLogic::getChannels(&$conn);
		foreach ($channels as &$channel) {
			$date = time();
			$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date);
			$channel->setPrograms($programs);
		} */
		
		$program = null;
		
		if (isset($_GET['id'])) {
			if ($_GET['type'] == 'program') {
				$programId = $_GET['id'];
				$program = EpgLogic::getProgramRecordingByProgramId(&$conn, $programId);
			} else if ($_GET['type'] == 'channel') {
				
			}
		}
		
		if (($_SERVER["HTTP_ACCEPT"] == "application/json") | ($_GET["format"] == "json")) {
			header('Content-type: application/json');
			print(json_encode($program));
			exit;
		} else {
			$smarty = new Smarty();
			$smarty->template_dir = "./tpl";
			$smarty->compile_dir = "./tpl_c";
			$smarty->cache_dir = "./cache";

			$smarty->display('mediaplayer.html');
		}
	}
	
}

?>
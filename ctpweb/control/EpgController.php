<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');
require_once('lib/logic/AccountLogic.php');

class EpgController {
	
	var $channels;
	
	public function EpgController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
	
		if (AccountLogic::isAuthorised(&$conn)) {
			$channels = EpgLogic::getChannels(&$conn);
			
			$date = time();
			
			if (isset($_GET["day"])) {
				$date = strtotime($_GET["day"]);
			}
			
			foreach ($channels as &$channel) {
				$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date, AccountLogic::getCurrentUser());
				$channel->setPrograms($programs);
			}
			
			if (($_SERVER["HTTP_ACCEPT"] == "application/json") | ($_GET["format"] == "json")) {
				header('Content-type: application/json');
				print(json_encode($channels));
				exit;
			} else {
				$smarty = new Smarty();
				$smarty->template_dir = "./tpl";
				$smarty->compile_dir = "./tpl_c";
				$smarty->cache_dir = "./cache";
				
				$smarty->assign('currentUser', AccountLogic::GetCurrentUser());
				$smarty->assign('channels', $channels);
				$smarty->display('epg.html');
			}
		} else {
			header('Location: login.php');
		}
	}
	
}

?>
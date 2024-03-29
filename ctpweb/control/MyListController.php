<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');
require_once('lib/logic/MyListLogic.php');
require_once('lib/logic/AccountLogic.php');

class MyListController {
	
	var $programs;
	var $channels;
	
	public function MyListController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
		
	if (AccountLogic::isAuthorised(&$conn)) {
			$tempChannels = EpgLogic::getChannels(&$conn);
			foreach ($tempChannels as $tempChannel) {
				$channels[$tempChannel->getId()] = $tempChannel;
			}
			
			$programs = MyListLogic::getMyList(&$conn, AccountLogic::getCurrentUser());
			
			foreach ($programs as $tempGrab) {
				$tempGrab->left = ($tempGrab->getCreatedOn() + ($config["pvr"]["maxHold"] * 60)) - time();
				$tempGrab->tvChannelName = $channels[$tempGrab->getProgram()->getTvChannelId()]->name;
				//$tempGrab->setRecording(EpgLogic::getProgramRecordingByProgramId(&$conn, $tempGrab->getProgram()->getId())->getRecording());
				if ($tempGrab->getRecording() != null) {
					$tempGrab->getRecording()->setFileName($config["pvr"]["downloadUrl"] . $tempGrab->getRecording()->getFileName());
				}
			}
			
			$smarty = new Smarty();
			$smarty->template_dir = "./tpl";
			$smarty->compile_dir = "./tpl_c";
			$smarty->cache_dir = "./cache";
			
			$smarty->assign('currentUser', AccountLogic::GetCurrentUser());
			$smarty->assign('channels', $channels);
			$smarty->assign('programs', $programs);
			$smarty->display('myList.html');
		} else {
			header('Location: login.php');
		}
	}
	
}

?>
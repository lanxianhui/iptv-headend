<?php

require_once('../lib/base.inc.php');
require_once('../lib/logic/EpgLogic.php');
require_once('../lib/logic/AccountLogic.php');

class IndexController {
	
	var $channels;
	
	public function IndexController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
		
		//if (AccountLogic::isAuthorised(&$conn)) {
			$channels = EpgLogic::getChannels(&$conn);
			
			$smarty = new Smarty();
			$smarty->template_dir = "./tpl";
			$smarty->compile_dir = "./tpl_c";
			$smarty->cache_dir = "./cache";
			
			$smarty->assign('currentUser', AccountLogic::GetCurrentUser());
			$smarty->assign('channels', $channels);
			$smarty->display('index.html');
		//} else {
			//header('Location: login.php');
		//}
	}
	
}

?>
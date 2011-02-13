<?php

require_once('lib/base.inc.php');
require_once('lib/logic/AccountLogic.php');

class LoginController {
	
	private function redirectToApp() {
		header('Location: epg.php');
		die();
	}
	
	public function LoginController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
		
		if (AccountLogic::isAuthorised(&$conn)) {
			if (isset($_GET["logout"])) {
				AccountLogic::logoutUser();
			} else {
				$this->redirectToApp();
			}
		}
		
		$smarty = new Smarty();
		$smarty->template_dir = "./tpl";
		$smarty->compile_dir = "./tpl_c";
		$smarty->cache_dir = "./cache";
		
		if (isset($_POST["userName"])) {
			$userName = $_POST["userName"];
			$password = $_POST["password"];
			$remember = ($_POST["remember"] == "true") ? TRUE : FALSE;
			
			if (AccountLogic::loginUser(&$conn, $userName, $password, $remember, true)) {
				$this->redirectToApp();
			} else {
				$smarty->assign('error', TRUE);
				$smarty->assign('userName', $userName);
				$smarty->assign('remember', $remember);
			}
		} else {
			$lastUserName = AccountLogic::getLastUser();
			if ($lastUserName != "") {
				$smarty->assign('userName', $lastUserName);
			}
		}
		
		$smarty->assign('formAction', 'login.php');
					
		$smarty->display('login.html');
	}
}

?>
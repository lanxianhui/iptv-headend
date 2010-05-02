<?php

class AccountLogic {
	
	function AccountLogic() {
		
	}
	
	/**
	 * Check if the current user is authorized to use the system. As this function
	 * will setup the session cookie, it should be called before any output is made.
	 * @param $conn The Datasource object containing the database connection
	 */
	function IsAuthorised(&$conn) {
		session_set_cookie_params(1 * 3600, '/');
		session_start();
		
		$userName = "";
		$passwordMD5 = "";
		$usedPersistent = false;
		
		if (isset($_SESSION["userName"]) && isset($_SESSION["password"])) {
			$userName = $_SESSION["userName"];
			$passwordMD5 = $_SESSION["password"];	
		} else if (isset($_COOKIE["CTPA"])) {
			$persistentLoginData = json_decode(base64_decode($_COOKIE["CTPA"]));
			$userName = $persistentLoginData["userName"];
			$passwordMD5 = $persistentLoginData["password"];
			$usedPersistent = true;
		} else if (isset($_SERVER['PHP_AUTH_USER']) && isset($_SERVER['PHP_AUTH_PW'])) {
			$userName = $_SERVER['PHP_AUTH_USER'];
			$passwordMD5 = md5($_SERVER['PHP_AUTH_PW']);
		} else {
			return false;
		}
		
		$userDAO = new UserDao();
		$userToBeFound = new User();
		$userToBeFound->setUserName($userName);
		$userToBeFound->setPassword($passwordMD5);
		$userList = $userDAO->searchMatching(&$conn, $userToBeFound);
		
		if (count($userList) == 1) {
			if ($usedPersistent) {
				// TODO Make the system save a last login information if the session has been created from the persistent login cookie 
			}
			
			$_SESSION["userObject"] = $userList[0];
			$_SESSION["userName"] = $userName;
			$_SESSION["password"] = $passwordMD5;
			return true;
		} else {
			unset($_SESSION["userName"]);
			unset($_SESSION["password"]);
			
			if (isset($_COOKIE["userData"])) {
				setcookie("userData", null, 1, '/');
			}
			
			return false;
		}
	}
	
	/**
	 * Login a specific user. Will return true if login was successful, false otherwise.
	 * Depending on $keepLoggedIn, it may setup the persistent login cookie, so it
	 * should be called before any output is made.
	 * @param $conn The Datasource object containing the database connection
	 * @param $userName User's username
	 * @param $password User's password (plaintext)
	 * @param $keepLoggedIn Should the system keep the user logged in for an indefinite amount of time.
	 */
	function LoginUser(&$conn, $userName, $password, $keepLoggedIn) {
		$passwordMD5 = md5($password);
		$userDAO = new UserDao();
		$userToBeFound = new User();
		$userToBeFound->setUserName($userName);
		$userToBeFound->setPassword($passwordMD5);
		$userList = $userDAO->searchMatching(&$conn, $userToBeFound);
		
		if (count($userList) == 1) {
			// TODO The user has just been logged in, we should save the lastLogin information in the database
			
			$_SESSION["userObject"] = $userList[0];
			$_SESSION["userName"] = $userName;
			$_SESSION["password"] = $passwordMD5;
			
			if ($keepLoggedIn) {
				$persistentLoginData["userName"] = $userName;
				$persistentLoginData["password"] = $passwordMD5;
				setcookie("userData", base64_encode(json_encode($persistentLoginData)), time()+60*60*24*365, '/');
			}
			
			return true;
		} else {
			$_SESSION = array();
			return false;
		}
	}
	
	/**
	 * Logout the currently logged in user. If the persistent login cookie is set,
	 * this will clear it. Because of that it should be executed before any output
	 * is made.
	 * @param $conn The Datasource object containing the database connection
	 */
	function LogoutUser() {
			$_SESSION = array();
			session_destroy();
			
			if (isset($_COOKIE["CTPA"])) {
				setcookie("userData", null, 1, '/');
			}
	}
	
}
	
?>
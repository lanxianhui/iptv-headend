<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');
require_once('lib/logic/PvrLogic.php');
require_once('lib/logic/AccountLogic.php');

class ApiController {
	
	// ---------- GUIDE OPERATIONS -------------
	
	private function getGuide(&$conn, $id = null, $date = null, $withRecordings = false) {
		if ($date == null) {
			$date = time();
		} else {
			$date = strtotime($date);
		}
		
		if ($id == null)
			$channels = EpgLogic::getChannels(&$conn);
		else
			$channels = array(EpgLogic::getChannel(&$conn, $id));
		
		if ($withRecordings) {
			foreach ($channels as &$channel) {
				$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date);
				$channel->setPrograms($programs);
			}
		} else {
		foreach ($channels as &$channel) {
				$programs = EpgLogic::getPrograms(&$conn, &$channel, $date);
				$channel->setPrograms($programs);
			}
		}
	}
	
	private function guideOperations(&$conn, $method, $args) {
		switch ($method) {
			case 'GET':
				$withRecordings = false;
				$date = null;
				for ($i = 0; $i < count($args); $i++) {
					switch($args[$i]) {
						case 'withRecordings':
							$withRecordings = true;
							break;
						case 'day':
							$date = $args[++$i];
							break;
					}
				}
				return getGuide(&$conn, null, $date, $withRecordings);
				break;
			default:
				header('HTTP/1.1 405 Method Not Allowed');
				header('Allow: GET');
				return("This method is not allowed for this resource");
				break;
		}
	}
	
	// ---------- CHANNEL OPERATIONS -----------
	
	private function listChannels(&$conn) {
		return EpgLogic::getChannels(&$conn);
	}
	
	private function getChannelById(&$conn, $id) {
		return EpgLogic::getChannel(&$conn, $id);
	}
	
	private function channelOperations(&$conn, $method, $args) {
		switch ($method) {
			case 'GET':
				if (count($args) == 0) {
					return listChannels(&$conn);
				} else if (count($args) == 1) {
					return getChannelById(&$conn, (int)$args[0]);
				} else if ((count($args) >= 2) && ($args[1] == "guide")) {
					$id = (int)$args[0];
					$withRecordings = false;
					$date = null;
					for ($i = 2; $i < count($args); $i++) {
						switch($args[$i]) {
							case 'withRecordings':
								$withRecordings = true;
								break;
							case 'day':
								$date = $args[++$i];
								break;
						}
					}
					return getGuide(&$conn, $id, $date, $withRecordings);
				}
				break;
			default:
				header('HTTP/1.1 405 Method Not Allowed');
				header('Allow: GET');
				return("This method is not allowed for this resource");
				break;
		}
	}
	
	// ---------- PROGRAM OPERATIONS -------------
	
	private function programOperations(&$conn, $method, $args) {
		switch ($method) {
			case 'GET':
				if (count($args) == 1) {
					$id = (int)$args[0];
					return EpgLogic::getProgramById(&$conn, $id);
				} else if ((count($args) == 2) && ($args[1] == 'withRecordings')) {
					$id = (int)$args[0];
					return EpgLogic::getProgramRecordingByProgramId(&$conn, $id);
				}
				break;
			default:
				header('HTTP/1.1 405 Method Not Allowed');
				header('Allow: GET');
				return("This method is not allowed for this resource");
				break;
		}
	}
	
	// ---------- FAVPROG OPERATIONS -------------
	
	private function favProgsOperations(&$conn, $method, $args) {
		switch ($method) {
			/* case 'GET':
				break; */
			default:
				header('HTTP/1.1 405 Method Not Allowed');
				header('Allow: GET');
				return("This method is not allowed for this resource");
				break;
		}
	}
	
	public function ApiController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
		// TODO Reenable CTP API testing
		//$isCTPApiSet = ($_SERVER['HTTP_X_CTP_API'] == "0.01");
		$isCTPApiSet = true;
	
		if (AccountLogic::isAuthorised(&$conn) && $isCTPApiSet) {
			
			// REST-ful way to give data
			if (isset($_SERVER["PATH_INFO"])) {
				// We skip over the first char because it's a slash
				$args = split('/', substr($_SERVER["PATH_INFO"], 1));
				// Args now contain args, and object contains the first argument
				$object = array_shift($args);
				// Method gets taken from server array, unless it's a GET parameter
				if (!isset($_POST["_method"])) {
					$method = $_SERVER["REQUEST_METHOD"];
				} else {
					$method = $_POST["_method"];
				}
			}
			
			switch ($object) {
				case "guide":
					$result = guideOperations(&$conn, $method, $args);
					break;
				case "channels":
					$result = channelOperations(&$conn, $method, $args);
					break;
				case "program":
					$result = programOperations(&$conn, $method, $args);
					break;
				case "favProgs":
					$result = favProgsOperations(&$conn, $method, $args);
					break;
			}
			
			if (($_SERVER["HTTP_ACCEPT"] == "application/json") | ($_GET["format"] == "json")) {
				header('Content-type: application/json');
				print(json_encode($result));
				exit;
			} else {
				header('HTTP/1.1 406 Not Acceptable');
				header('Content-type: application/json');
				print(json_encode("JSON is currently the only supported format for API interaction."));
				exit;
			}
		} else {
			header('HTTP/1.1 401 Unauthorized');
			header('Location: login.php');
			print(json_encode("You need to authorise yourself properly for the interaction with the API."));
			exit;
		}
	}
	
}

?>
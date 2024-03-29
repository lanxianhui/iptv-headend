<?php

require_once('lib/base.inc.php');
require_once('lib/logic/EpgLogic.php');
require_once('lib/logic/PvrLogic.php');
require_once('lib/logic/AccountLogic.php');
//require_once('lib/logic/MyListLogic.php');

class ApiController {
	
	// ---------- HELPER FUNCTIONS -------------
	
	private function sendStatusCode($code, $message, $headers = null) {
		$codeDesc = array(// Successful action
			  201 => "Created",
			  202 => "Accepted",
			  203 => "Non-Authoritative Information",
			  204 => "No Content",
			  205 => "Reset Content",
			  206 => "Partial Content",
			  // Redirection
			  300 => "Multiple Choices",
			  301 => "Moved Permanently",
			  302 => "Found",
			  303 => "See Other",
			  304 => "Not Modified",
			  305 => "Use Proxy",
			  307 => "Temporary Redirect",
			  // Client Error
			  400 => "Bad Request",
			  401 => "Unauthorized",
			  402 => "Payment Required",
			  403 => "Forbidden",
			  404 => "Not Found",
			  405 => "Method Not Allowed",
			  406 => "Not Acceptable",
			  407 => "Proxy Authentication Required",
			  408 => "Request Timeout",
			  409 => "Conflict",
			  410 => "Gone",
			  411 => "Length Required",
			  412 => "Precondition Failed",
			  413 => "Request Entity Too Large",
			  414 => "Request-URI Too Long",
			  415 => "Unsupported Media Type",
			  416 => "Requested Range Not Satisfiable",
			  417 => "Expectation Failed",
			  // Server Error
			  500 => "Internal Server Error",
			  501 => "Not Implemented",
			  502 => "Bad Gateway",
			  503 => "Service Unavailable",
			  504 => "Gateway Timeout"
		);
		header('HTTP/1.1 ' + $code + ' ' + $codeDesc[$code]);
		header('Status: ' + $code + ' ' + $codeDesc[$code]);
		if (count($headers) > 0) {
			foreach ($headers as &$header) {
				header($header, true, $code);
			}
		}
		$errorObj = array('code' => $code,
						  'description' => $codeDesc[$code],
						  'message' => $message);
		return $errorObj;
	}
	
	private function checkMulticast($multicastConfig, $ipAddress) {
		$multicastEnabled = $multicastConfig["default"];
		
		$ip = ip2long($ipAddress);
		
		foreach ($multicastConfig["allow"] as $network) {
			$net = ip2long($network[0]);
			$mask = ip2long($network[1]);
			
			if ($net == ($ip & $mask)) {
				$multicastEnabled = true;
			}
		}
		
		foreach ($multicastConfig["deny"] as $network) {
			$net = ip2long($network[0]);
			$mask = ip2long($network[1]);
			
			if ($net == ($ip & $mask)) {
				$multicastEnabled = false;
			}
		}
	}
	
	// ---------- GUIDE OPERATIONS -------------
	
	private function getGuide(&$conn, $id = null, $date = null, $withRecordings = false, $user = null) {
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
				$programs = EpgLogic::getProgramsWithRecordings(&$conn, &$channel, $date, $user);
				$channel->setPrograms($programs);
			}
		} else {
			foreach ($channels as &$channel) {
				$programs = EpgLogic::getPrograms(&$conn, &$channel, $date);
				$channel->setPrograms($programs);
			}
		}
		
		return $channels;
	}
	
	private function guideOperations(&$conn, $method, $args, $user = null) {
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
				return $this->getGuide(&$conn, null, $date, $withRecordings, $user);
				break;
			default:
				return $this->sendStatusCode(405, "Guide supports only GET method.", array('Allow: GET'));
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
					return $this->listChannels(&$conn);
				} else if (count($args) == 1) {
					return $this->getChannelById(&$conn, (int)$args[0]);
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
					return $this->getGuide(&$conn, $id, $date, $withRecordings);
				}
				break;
			default:
				return $this->sendStatusCode(405, "Channels support only GET method.", array('Allow: GET'));
				break;
		}
	}
	
	// ---------- PROGRAM OPERATIONS -------------
	
	private function getProgramById(&$conn, $id) {
		return EpgLogic::getProgramById(&$conn, $id);
	}
	
	private function getProgramRecordingByProgramId(&$conn, $id) {
		return EpgLogic::getProgramRecordingByProgramId(&$conn, $id);
	}
	
	private function programOperations(&$conn, $method, $args) {
		switch ($method) {
			case 'GET':
				if (count($args) == 1) {
					$id = (int)$args[0];
					return $this->getProgramById(&$conn, $id);
				} else if ((count($args) == 2) && ($args[1] == 'withRecordings')) {
					$id = (int)$args[0];
					return $this->getProgramRecordingByProgramId(&$conn, $id);
				}
				break;
			default:
				return $this->sendStatusCode(405, "Programs supports only GET method.", array('Allow: GET'));
				break;
		}
	}
	
	// ---------- MYLIST OPERATIONS -------------
	
	private function getMyList(&$conn, &$user) {
		//return MyListLogic::getMyList(&$conn, &$user);
		return PvrLogic::listRecordings(&$conn, &$user);
	}
	
	private function addToMyList(&$conn, &$user, $recordingId) {
		//return MyListLogic::addToMyList(&$conn, &$user, $recordingId);
		return PvrLogic::recordRecording(&$conn, &$user, $recordingId);
	}
	
	private function removeFromMyList(&$conn, &$user, $recordingId) {
		//return MyListLogic::removeFromMyList(&$conn, &$user, $recordingId);
		$recording = new Recording();
		$recording->setId($recordingId);
		return PvrLogic::deleteRecording(&$conn, &$recording, &$user);
	}
	
	private function myListOperations(&$conn, $method, $args) {
		switch ($method) {
			case 'GET':
				return $this->getMyList(&$conn, AccountLogic::getCurrentUser());
				break;
			case 'DELETE':
				if ((count($args) == 1) && is_numeric($args[0])) {
					if ($this->removeFromMyList(&$conn, AccountLogic::getCurrentUser(), (int)$args[0]))
						return $this->sendStatusCode(202, "Recording let go.");
					else 
						return $this->sendStatusCode(500, "An error occured while letting go.");
				} else {
					return $this->sendStatusCode(400, "Only recordings can be let go.");
				}
				break;
			case 'POST':
				if ((count($args) == 1) && is_numeric($args[0])) {
					if ($this->addToMyList(&$conn, AccountLogic::getCurrentUser(), (int)$args[0]))
						return $this->sendStatusCode(201, "Recording grabbed.");
					else 
						return $this->sendStatusCode(500, "An error occured while grabbing.");
				} else {
					return $this->sendStatusCode(400, "Only recordings can be grabbed.");
				}
				break;
			default:
				return $this->sendStatusCode(405, "MyList supports only GET, POST and DELETE methods.", array('Allow: GET POST DELETE'));
				break;
		}
	}
	
	public function ApiController($config) {
		$conn = new Datasource($config["database"]["host"], $config["database"]["name"], $config["database"]["username"], $config["database"]["password"]);
		$isCTPApiSet = ($_SERVER['HTTP_X_CTP_API'] == "0.01");
		//$isCTPApiSet = true;
	
		if (AccountLogic::isAuthorised(&$conn) && $isCTPApiSet) {
			
			// REST-ful way to give data
			if (isset($_SERVER["PATH_INFO"])) {
				// We skip over the first char because it's a slash
				$args = split('/', substr($_SERVER["PATH_INFO"], 1));
				// Args now contain args, and object contains the first argument
				$object = array_shift($args);
				// Method gets taken from server array, unless it's a POST parameter
				if (!isset($_POST["_method"])) {
					$method = $_SERVER["REQUEST_METHOD"];
				} else {
					$method = strtoupper($_POST["_method"]);
				}
			}
			
			switch ($object) {
				case "guide":
					$result = $this->guideOperations(&$conn, $method, $args, AccountLogic::getCurrentUser());
					break;
				case "channels":
					$result = $this->channelOperations(&$conn, $method, $args);
					break;
				case "program":
					$result = $this->programOperations(&$conn, $method, $args);
					break;
				case "myList":
					$result = $this->myListOperations(&$conn, $method, $args);
					break;
			}
			
			if (($_SERVER["HTTP_ACCEPT"] == "application/json") | ($_GET["format"] == "json")) {
				header('Content-type: application/json');
				
				if (checkMulticast($config["multicast"], $_SERVER["REMOTE_ADDR"])) {
					header("X-CTP-Allow-Method: Multicast, Unicast");
				} else {
					header("X-CTP-Allow-Method: Unicast");
				}
				
				print(json_encode($result));
				exit;
			} else {
				header('HTTP/1.1 406 Not Acceptable');
				header('Content-type: text/plain');
				print("JSON is currently the only supported format for API interaction.");
				exit;
			}
		} else {
			header('Content-type: application/json');
			print(json_encode($this->sendStatusCode(401, "You need to authorise yourself.")));
			exit;
		}
	}
	
}

?>
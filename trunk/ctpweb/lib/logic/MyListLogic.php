<?php

class MyListLogic {
	
	public function MyListLogic() {
		
	}
	
	public function getMyList(&$conn, &$user) {
		$userRecordingDAO = new UserRecordingDao();
		return $userRecordingDAO->loadAllProgramRecordings(&$conn, &$user);
	}
	
	public function addToMyList(&$conn, &$user, $recordingId) {
		$userRecordingDAO = new UserRecordingDao();
		$newEntry = new UserRecording();
		$newEntry->setRecordingId($recordingId);
		$newEntry->setUserId($user->getId());
		return $userRecordingDAO->create(&$conn, $newEntry);
	}
	
	public function removeFromMyList(&$conn, &$user, &$recordingId) {
		$userRecordingDAO = new UserRecordingDao();
		$newEntry = new UserRecording();
		$newEntry->setRecordingId($recordingId);
		$newEntry->setUserId($user->getId());
		return $userRecordingDAO->delete(&$conn, $newEntry);
	}
	
}
?>
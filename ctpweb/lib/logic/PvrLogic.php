<?php 

class PvrLogic {
	
	function PvrLogic() {
		
	}
	
	/**
	 * Add a new recording to the user's recordings list
	 * @param $conn The Datasource object containing the database connection
	 * @param $program The program to be recorded and placed in the user's recordings list
	 * @param User $user The User, whose recordings list is to be modified
	 * @return True if the recording was successfully added, false if it failed
	 */
	function recordProgram(&$conn, &$program, &$user) {
		$recordingDAO = new RecordingDao();
		$recording = $recordingDAO->getObject($conn, null, $program->getId());
		if (!$recording->getId()) {
			$recording = new Recording();
			$recording->setAll(null, $program->getId(), Recording::WAITING, null);
			if (!$recordingDAO->create($conn, $recording))
				return false;
		}
		
		$userRecordingDAO = new UserRecordingDao();
		$userRecording = $userRecordingDAO->getObject($conn, $recording->getId(), $user->getId());
		if (!$userRecording->getRecordingId()) {
			$userRecording = new UserRecording();
			$userRecording->setAll($recording->getId(), $user->getId());
			if (!$userRecordingDAO->create($conn, $userRecording))
				return false;
		}
		return true;
	}
	
	/**
	 * Delete a recording
	 * @param $conn The Datasource object containing the database connection
	 * @param $recording The Recording object, which is to be unlinked from the User's recordings list
	 * @param $user The User, whose recordings list is to be modified
	 * @return True if it succeded in deleting the recording, false otherwise
	 */
	function deleteRecording(&$conn, &$recording, &$user) {
		$deleteUserRecording = new UserRecording();
		$deleteUserRecording->setAll($recording->getId(), $user->getId());
		$userRecordingDAO = new UserRecordingDao();
		return $userRecordingDAO->delete($conn, $deleteUserRecording);
	}
	
	function listRecordings(&$conn) {
		$recordingDao = new RecordingDao();
		return $recordingDao->loadAll();
	}
	
	function listRecordings(&$conn, &$user) {
		
	}
	
}

?>
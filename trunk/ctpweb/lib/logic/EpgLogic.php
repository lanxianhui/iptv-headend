<?php

class EpgLogic {
	
	function EpgLogic() {
		
	}
	
	/**
	 * Gets all channels within the system
	 * @param $conn The Datasource connection to be used
	 * @return An array of all the channels in the system
	 */
	function getChannels(&$conn, $start = null, $number = null) {
		$channelDAO = new TvChannelDAO();
		if (($start === null) && ($number === null))
			return $channelDAO->loadAll(&$conn);
		else
			return false;
	}
	
	/**
	 * Gets the number of all the channels within the system
	 * @param $conn The Datasource connection to be used
	 * @return A number of all the channels
	 */
	function getNumberOfChannels(&$conn) {
		$channelDAO = new TvChannelDAO();
		return $channelDAO->countAll(&$conn);
	}
	
	/**
	 * Gets all the programs for a given channel on a given day
	 * @param $conn The Datasource connection to be used
	 * @param $channel The channel for which to return all the programs
	 * @param $date The date in Unix timestamp format
	 * @return An array of Program
	 */
	function getPrograms(&$conn, &$channel, $date) {
		$programDAO = new ProgramDAO();
		return $programDAO->loadDay(&$conn, &$channel, $date);
	}
	
	function getProgramsWithRecordings(&$conn, &$channel, $date) {
		$programRecordingDAO = new ProgramDAO();
		return $programRecordingDAO->loadDayWithRecordings(&$conn, &$channel, $date);
	}
}

?>
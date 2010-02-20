<?php

class EpgLogic {
	
	function EpgLogic() {
		
	}
	
	/**
	 * Gets all channels within the system
	 * @param $conn The Datasource connection to be used
	 * @return An array of all the channels in the system
	 */
	function getChannels(&$conn) {
		$channelDAO = new TvChannelDAO();
		return $channelDAO->loadAll($conn);
	}
	
	/**
	 * Gets the number of all the channels within the system
	 * @param $conn The Datasource connection to be used
	 * @return A number of all the channels
	 */
	function getNumberOfChannels(&$conn) {
		$channelDAO = new TvChannelDAO();
		return $channelDAO->countAll($conn);
	}
	
	/**
	 * Gets only the specified number of channels starting from the given channel number (a utility function for paging)
	 * @param $conn The Datasource connection to be used
	 * @param $start The number of the channel to start from
	 * @param $number Number of the channels to be returned
	 * @return An array of TvChannel
	 */
	function getChannels(&$conn, $start, $number) {
		
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
		return $programDAO->loadDay($conn, $channel, $date);
	}
	
}

?>
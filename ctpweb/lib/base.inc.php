<?php
	$_LIBDIR = dirname(__FILE__);
	
	// Common libraries
	require_once($_LIBDIR."/common/Datasource.php");
	require_once($_LIBDIR."/common/Smarty.class.php");
	
	// Config file
	require_once($_LIBDIR."/config.inc.php");
	
	// Models
	require_once($_LIBDIR."/model/TvChannel.php");
	require_once($_LIBDIR."/model/User.php");
	require_once($_LIBDIR."/model/Recording.php");
	require_once($_LIBDIR."/model/Program.php");
	require_once($_LIBDIR."/model/UserRecording.php");
	
	// DAOs
	require_once($_LIBDIR."/dao/TvChannelDao.php");
	require_once($_LIBDIR."/dao/UserDao.php");
	require_once($_LIBDIR."/dao/RecordingDao.php");
	require_once($_LIBDIR."/dao/ProgramDao.php");
	require_once($_LIBDIR."/dao/UserRecordingDao.php");
?>
<?php

	require_once('lib/base.inc.php');
	require_once("control/EpgController.php");
	
	$smarty = new Smarty();
	$controller = new EpgController($config, $smarty);

?>
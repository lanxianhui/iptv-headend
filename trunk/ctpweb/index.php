<?php

	require_once('lib/base.inc.php');
	require_once("control/EpgController.php");
	
	$smarty = new Smarty();
	$smarty->template_dir = "./tpl";
	$smarty->compile_dir = "./tpl_c";
	$smarty->cache_dir = "./cache";
	
	$controller = new EpgController($config, $smarty);

?>
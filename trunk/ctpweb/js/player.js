function openMiniWindow(url, name, width, height) {
	var popupWindow = window.open(url, name, "width=" + width + ",height=" + height + ",menubar=no,location=no,resizable=yes,scrollbars=no,status=no,centerscreen=yes,directories=no");
	popupWindow.focus();
}

function playAnything(url) {
	openMiniWindow(url, 'playerWindow', 640, 500);
};

function playChannel(id) {
	openMiniWindow('mediaplayer.php#channel,'+id, 'playerWindow', 640, 500);
};

function playProgram(id) {
	openMiniWindow('mediaplayer.php#program,'+id, 'playerWindow', 640, 500);
};

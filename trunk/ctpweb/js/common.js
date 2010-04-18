var notificationBox;
var notification;

function getPageY(oElement) {
	var iReturnValue = 0;
	while(oElement != null) {
		iReturnValue += oElement.offsetTop;
		oElement = oElement.offsetParent;
	}
	return iReturnValue;
}

function getPageX(oElement) {
	var iReturnValue = 0;
	while(oElement != null) {
		iReturnValue += oElement.offsetLeft;
		oElement = oElement.offsetParent;
	}
	return iReturnValue;
}

function showNotification(text) {
	if (notificationBox.hasClass('hidden'))
		notificationBox.removeClass('hidden');
	
	notification.innerHTML = text;
}

function hideNotification() {
	if (!notificationBox.hasClass('hidden'))
		notificationBox.addClass('hidden');
}

function bootScripts() {
	var bodyElement = $(document.body);
	notificationBox = new Element('div', {
		'class': 'notifyBox hidden'
	});
	notification = new Element('div', {
		'class': 'notify'
	});
	notificationBox.adopt(notification);
	bodyElement.adopt(notificationBox);
}

window.addEvent('domready', bootScripts);
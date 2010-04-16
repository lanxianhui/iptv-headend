var notificationBox;
var notification;

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
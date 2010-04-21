var notificationBox;
var notificationText;

var openContextMenu = null;

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

function notificationShow(text) {
	notificationText.innerHTML = text;
	
	if (notificationBox.hasClass('hidden'))
		notificationBox.removeClass('hidden');
}

function notificationHide() {
	if (!notificationBox.hasClass('hidden'))
		notificationBox.addClass('hidden');
}


function ContextMenu(element, elementWidth, elementHeight) {
	this.menuElement = null;
	
	this.attachmentElement = typeof(element) != 'undefined' ? element : null;
	this.elementWidth = typeof(elementWidth) != 'undefined' ? elementWidth : 18;
	this.elementHeight = typeof(elementHeight) != 'undefined' ? elementHeight : 23;
	
	this.show = function(x, y) {
		if (this.attachmentElement != null) {
			this.menuElement.style.left = (getPageX(this.attachmentElement) - (150 - this.elementWidth)) + "px";
			this.menuElement.style.top = (getPageY(this.attachmentElement) + this.elementHeight) + "px";
		}
		else {
			this.menuElement.style.left = x + "px";
			this.menuElement.style.top = y + "px";
		}
		
		if (this.menuElement.hasClass('hidden')) {
			this.menuElement.removeClass('hidden');
		}
		
		openContextMenu = this;
		
		var bodyElement = $(document.body);
		bodyElement.addEvent('click', function(event) {
			if (openContextMenu != null) {
				openContextMenu.hide();
				event.stop();
			}
		});
	};
	
	this.hide = function() {
		bodyElement.removeEvent('click', openContextMenu.hide);
		
		if (!openContextMenu.menuElement.hasClass('hidden')) {
			openContextMenu.menuElement.addClass('hidden');
		}
		
		openContextMenu.dispose();
		openContextMenu = null;
	};
	
	this.addMenuItem = function(caption, callback, isDefault) {
		isDefault = typeof(isDefault) != 'undefined' ? isDefault : false;
		
		if (isDefault) {
			extraClass = ' default';
		}
		else {
			extraClass = '';
		}
		
		newMenuItem = new Element('div', {
			'class': 'menuItem' + extraClass
		});
		
		newMenuItem.innerHTML = caption;
		
		newMenuItem.addEvent('click', callback);
		
		this.menuElement.adopt(newMenuItem);
	};
	
	this.addBreak = function() {
		newMenuItem = new Element('hr', {
			'class': 'break'
		});
		
		this.menuElement.adopt(newMenuItem);
	};
	
	this.dispose = function() {
		this.menuElement.dispose();
	};
	
	this.menuElement = new Element('div', {
		'class': 'dropMenu hidden',
		'styles': {
			'top': '0',
			'left': '0'
		}
	});
	
	var bodyElement = $(document.body);
	bodyElement.adopt(this.menuElement);
};

function contextMenuHide() {
	if (openContextMenu != null) {
        openContextMenu.hide();
    }
}

function Notification(root) {
	this.bodyElement = root;
	notificationBox = new Element('div', {
		'class': 'notifyBox hidden'
	});
	notificationText = new Element('div', {
		'class': 'notify'
	});
	notificationBox.adopt(notificationText);
	this.bodyElement.adopt(notificationBox);
}

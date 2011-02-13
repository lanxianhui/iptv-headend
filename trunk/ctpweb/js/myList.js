var notification;

var currentDate;

function restCall(url, method, callback, callbackFailure) {
	var jsonRequest = new Request.JSON({
        method: method,
        url: url,
        onSuccess: callback,
        onFailure: callbackFailure,
        headers: {
            Accept: "application/json",
            'X-CTP-Api': "0.01"
        }
    });
    jsonRequest.send();
}

function letGoRecording(id, title, callback) {
	extUrl = "/myList/" + id;
	
	var callback = function(result) {
		if (result.code == 202) {
			callback(id, title);
		} else {
			notificationShow("Unable to let go '" + title + "'.");
			delayedNotificationHide(10);
		}
	};
	var callbackFailure = function() {
		notificationShow("Unable to let go '" + title + "'.");
		delayedNotificationHide(10);
	};
	
	restCall("api.php" + extUrl, 'delete', callback, callbackFailure);
}

function delayedNotificationHide(seconds) {
	if (seconds) {
		setTimeout(delayedNotificationHide, 1000 * seconds);
	} else {
		notificationHide();
	}
}

function attachActions() {
	$$('.myList .programRecording').each(function(itm) {
		//alert(itm.id);
		var splitId = itm.id.split('-');
		var programId = splitId[0];
		var recordingId = splitId[1];
		
		var playButton = itm.getElement('button.play');
		playButton.addEvent('click', function(event) {
			//alert('Play ' + programId);
			playProgram(programId);
		});
		
		var letGoButton = itm.getElement('button.letgo');
		letGoButton.addEvent('click', function(event) {
			//alert('Let go ' + recordingId);
			var title = itm.getElement('h3.title').innerHTML;
			var callback = function(id, title) {
				alert(title + ' let go (id: ' + id + ')');
			};
			if (confirm("Do you want to let go of '" + title + "'?")) {
				letGoRecording(recordingId, title, callback);
			}
		});
	});
}

function bootScripts() {
	currentDate = new Date();
	currentDate.clearTime();
	
	notification = new Notification($(document.body));
	
	attachActions();
}

window.addEvent('domready', bootScripts);

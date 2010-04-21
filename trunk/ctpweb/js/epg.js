var lastUrl = "";
var currentDate = null;
var openProgram = null;
var timeFormat = "%I:%M%p";
var timefallUpdater = null;
var timefallPrecision = 60;
var epgUpdater = null;
var dayscrubElement = null;
var epgPrecision = 60 * 10;

var notification;

var fits = 4;

function openMiniWindow(url, name, width, height) {
	var popupWindow = window.open(url, name, "width=" + width + ",height=" + height + ",menubar=no,location=no,resizable=yes,scrollbars=no,status=no,centerscreen=yes,directories=no");
	popupWindow.focus();
}

function downloadData(url, callback){
    var jsonRequest = new Request.JSON({
        method: "get",
        url: url,
        onSuccess: loadGuide,
        headers: {
            Accept: "application/json"
        }
    });
    jsonRequest.send();
}

function recalculateTimefall() {
	timefallUpdater = setTimeout(recalculateTimefall, 1000 * timefallPrecision);
	$each($$(".channel"), function(channel){
		var currentTimefall = channel.retrieve("timefall");
		var totalHeight = 0;
		var currentTime = new Date();
		$each(channel.getElements(".program"), function(program){
			endTime = program.retrieve("end");
			if (endTime < currentTime) {
				totalHeight += program.getSize().y;
				if (!program.hasClass("past"))
					program.addClass("past");
				if (program.hasClass("current"))
					program.removeClass("current");
				return;
			} else {
				startTime = program.retrieve("begin");
				if (startTime < currentTime) {
					totalTime = endTime.getTime() - startTime.getTime();
					progressTime = currentTime.getTime() - startTime.getTime();
					progress = progressTime / totalTime;
					totalHeight += progress * program.getSize().y;
					if (!program.hasClass("current"))
						program.addClass("current");
				}
				return;
			}
		});
		currentTimefall.style.height = totalHeight + "px";
	});
}

function createImageButton(element, classList, image, caption){
	var newButton = createButton(element, classList, "<img src=\"" + image + "\" alt=\"" + caption + "\"/>");
	return newButton;
}

function createButton(element, classList, caption){
	var newButton = new Element('button', {
		'class': 'btn ' + classList,
		'html': '<span><span>' + caption + '</span></span>'
	});
	element.adopt(newButton);
	return newButton;
}

function addProgram(schedule, program){
	var newProgram = new Element('div', {
		'class': 'program'
	});
	newProgram.store("id", program.id);
	
	var begin = new Date(program.begin * 1000);
	var end = new Date(program.end * 1000);
	newProgram.store("begin", begin);
	newProgram.store("end", end);
	
	newProgram.store("recording", program.recording);
	
	// Basic data
	var newData = new Element('div', {
		'class': 'data'
	});
	var newLeftColumn = new Element('div', {
		'class': 'left',
		'html': '<div class="begin">' + begin.format(timeFormat) + '</div>'
	});
	var newRightColumn = new Element('div', {
		'class': 'right'
	});
	var newTitle = new Element('h3', {
		'html': program.title
	});
	if (end < new Date()) {
		newProgram.addClass('past');
	}
	newRightColumn.adopt(newTitle);
	newData.adopt(newLeftColumn);
	newData.adopt(newRightColumn);
	
	// The flyout
	var newFlyout = new Element('div', {
		'class': 'flyout'
	});
	var newDescription = new Element('div', {
		'class': 'description',
		'html': program.description
	});
	var newActions = new Element('div', {
		'class' : 'actions'
	});
	
	var playProgram = function(id) {
		openMiniWindow('mediaplayer.php#program,'+id, 'playerWindow', 640, 500);
	};
	
	// TODO Make Star'ing programs, and all actions user-aware. We need logins.
	
	if ((program.recording == null) && (end > new Date())) {
		monitorButton = createButton(newActions, "pill primary grip", "Grip");
		
		monitorButton.addEvent('click', function(event){
			alert('record!');
			
			event.stop();
		});
	}
	else if (program.recording != null) {
		if (program.recording.mode == "WAITING") {
			removeButton = createButton(newActions, "pill letgo", "Let go");
			
			removeButton.addEvent('click', function(event){
				alert('cancel!');
				
				event.stop();
			});
		}
		else if ((program.recording.mode == "AVAILABLE") | (program.recording.mode == "PROCESSING")) {
			var deleteButton = createButton(newActions, "pill letgo space", "Let go");
			var playButton = createButton(newActions, "pill-l primary play", "Play");
			var playMoreButton = createImageButton(newActions, "pill-r playMore narrow", "img/arrow.png", "v");
			
			playButton.addEvent('click', function(event){
				playProgram(program.id);
				
				contextMenuHide();
				
				event.stop();
			});
			deleteButton.addEvent('click', function(event){
				alet('delete!');
				
				contextMenuHide();
				
				event.stop();
			});
			playMoreButton.addEvent('click', function(event){
				if (openContextMenu == null) {
					this.contextMenu = new ContextMenu(playMoreButton);
					this.contextMenu.addMenuItem("Play timeshifted", function() {
						playProgram(program.id);
						
						contextMenuHide();
					}, true);
					this.contextMenu.addBreak();
					this.contextMenu.addMenuItem("Play live", function() {
						contextMenuHide();
					});
					
					this.contextMenu.show();
					
					event.stop();
				}
				else {
					openContextMenu.hide();
					openContextMenu = null;
					
					event.stop();
				}
			});
		}
	}
	
	newFlyout.adopt(newDescription);
	newFlyout.adopt(newActions);
	
	newProgram.adopt(newData);
	newProgram.adopt(newFlyout);
	
	// Events
	newProgram.addEvent('click', function(event){
	    if (newProgram.hasClass("active")) {
	        newProgram.removeClass("active");
	        openProgram = null;
	        
	        contextMenuHide();
	    }
	    else {
	        if (openProgram != null) {
	            openProgram.removeClass("active");
	            openProgram = null;
	            
	            contextMenuHide();
	        }
	        newProgram.addClass("active");
	        openProgram = newProgram;
	    }
		recalculateTimefall();
	    event.stop();
	});
		
	schedule.adopt(newProgram);
}

function addChannel(guide, channel){
	var newTimefall = new Element('div', {
		'class': 'timefall'
	});
    var newChannel = new Element('div', {
        'class': 'channel'
    });
    var newHeader = new Element('div', {
        'class': 'header',
        'html': '<h2>' + channel.name + '</h2>'
    });
	var newSchedule = new Element('div', {
		'class': 'schedule'
	});
    newChannel.adopt(newHeader);
	newChannel.adopt(newSchedule);
	newChannel.store("id", channel.id);
	newChannel.store("timefall", newTimefall);
	newSchedule.adopt(newTimefall);
	
	$each(channel.programs, function(program) {
		addProgram(newSchedule, program);
	});
	
	guide.adopt(newChannel);
}

function loadGuide(guide){
	if (timefallUpdater != null)
		clearTimeout(timefallUpdater);
	if (epgUpdater != null)
		clearTimeout(epgUpdater);
	
	$("guide").empty();
    $each(guide, function(channel){
        addChannel($("guide"), channel);
    });
	recalculateTimefall();
	
	epgUpdater = setTimeout(updateGuide, 1000 * epgPrecision);
	
	notificationHide();
}

function padZeros(number) {
	if ((number < 10) && (number >= 0))
		return "0" + number;
	else
		return number;
}

function updateGuide(){
	notificationShow("Loading...");
	
	var urlExploded = location.href.split('#');
	var extUrl = "";
	
	if (urlExploded.length > 1) {
		var explodeDate = urlExploded[urlExploded.length - 1].split('-');
		var year = explodeDate[0];
		var month = explodeDate[1];
		var day = explodeDate[2];
	}
	
	currentDate = new Date();
	if ((urlExploded.length > 1) && (day > 0)) {
		currentDate.set('Date', day);
		currentDate.set('Month', month - 1);
		currentDate.set('Year', year);
	}
	currentDate.clearTime();
	
	dayscrubElement.refreshCurrent();
	
	var yearForS = currentDate.get('Year', day);
	var monthForS = padZeros(currentDate.get('Month', month) + 1);
	var dayForS = padZeros(currentDate.get('Date', day));
	
	var date = yearForS + "-" + monthForS + "-" + dayForS; 
	extUrl = "?day=" + date;
	
    downloadData("index.php" + extUrl, loadGuide);
}

function hasUrlChanged() {
	var currentUrl = location.href;
	
	if (currentUrl != lastUrl) {
		updateGuide();
		lastUrl = currentUrl;
	}
	
	setTimeout(hasUrlChanged, 200);
}

function clearFits(element) {
	element.removeClass("fits5");
	element.removeClass("fits3");
	element.removeClass("fits2");
}

function windowResize() {
	var rootElement = $("guide");
	elementWidth = rootElement.clientWidth;
	
	if ((elementWidth >= 980) && (fits != 4)) {
		clearFits(rootElement);
		fits = 4;
	}
	else if ((elementWidth >= 750) && (elementWidth < 980) && (fits != 3)) {
		clearFits(rootElement);
		rootElement.addClass("fits3");
		fits = 3;
	}
	else if ((elementWidth < 750) && (fits != 2)) {
		clearFits(rootElement);
		rootElement.addClass("fits2");
		fits = 2;
	}
}

function bootScripts() {
	currentDate = new Date();
	currentDate.clearTime();
	
	dayscrubElement = new Dayscrub($('daylist'), new Date().decrement('day', 7), 14);
	dayscrubElement.refreshCurrent();
	
	notification = new Notification($(document.body));
	
	hasUrlChanged();
	windowResize();
}

window.addEvent('domready', bootScripts);
window.addEvent('resize', windowResize);


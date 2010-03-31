var openProgram = null;
var timeFormat = "%I:%M%p";
var timefallUpdater = null;
var timefallPrecision = 60;

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
		'html': '<span><span>' + caption + '</span></span>', 
	})
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
		newTitle.addClass('past');
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
	})
	var newActions = new Element('div', {
		'class' : 'actions'
	})
	
	// TODO Make Star'ing programs, and all actions user-aware. We need logins.
	
	if ((program.recording == null) && (end > new Date())) {
		monitorButton = createButton(newActions, "pill primary monitor", "Grip");
		
		monitorButton.addEvent('click', function(event) {
			alert('record!');
			
			event.stop();
		});
	} else if (program.recording.mode == "WAITING") {
		removeButton = createButton(newActions, "pill remove", "Let go");
		
		removeButton.addEvent('click', function(event) {
			alert('cancel!');
			
			event.stop();
		});
	} else if ((program.recording.mode == "AVAILABLE") | (program.recording.mode == "PROCESSING")) {
		playButton = createButton(newActions, "pill-l primary play", "Play");
		deleteButton = createButton(newActions, "pill-r delete", "Let go");
		
		playButton.addEvent('click', function(event) {
			alert('test!');
			
			event.stop();
		});
		deleteButton.addEvent('click', function(event) {
			alet('delete!');
			
			event.stop();
		});
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
	    }
	    else {
	        if (openProgram != null) {
	            openProgram.removeClass("active");
	            openProgram = null;
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
		clearInterval(timefallUpdater);
	$("guide").empty();
    $each(guide, function(channel){
        addChannel($("guide"), channel);
    });
	recalculateTimefall();
	timefallUpdater = setInterval(recalculateTimefall, 1000 * timefallPrecision);
}

function updateGuide(){
    downloadData("index.php", loadGuide);
}

function bootScripts(){
    updateGuide();
}

window.addEvent('domready', bootScripts);

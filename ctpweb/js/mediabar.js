var rootElement;

var mediabarElement;

var oldMouseX = 0;
var oldMouseY = 0;

var scrubbarWidth = 600;
var scrubbarKnobLeft = -5;

var primaryWidth = 0;
var secondaryWidth = 0;

var volumeKnobWidth = 0;

var knob;
var volumeKnob;

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

function onDownDefault(element) {
	if (!element.hasClass('down'))
		element.addClass('down');
}

function onUpDefault(element) {
	if (element.hasClass('down'))
		element.removeClass('down');
}

function setUpDefaultUpDown(element) {
	element.addEvent('mousedown', function(){
		onDownDefault(element);
	});
	
	element.addEvent('mouseup', function(){
		onUpDefault(element);
	});
}

function createButtonsLeft(element) {
	var newButtonsLeft = new Element('div', {
		'class': 'buttons left'
	});
	
	element.playButton = new Element('div', {
		'class': 'b play'
	});
	
	element.liveButton = new Element('div', {
		'class': 'b live'
	});
	
	element.pauseButton = new Element('div', {
		'class': 'b pause hide'
	});
	
	element.chPlusButton = new Element('div', {
		'class': 'b chplus hide'
	});
	
	element.chMinusButton = new Element('div', {
		'class': 'b chminus hide'
	});
	
	setUpDefaultUpDown(element.playButton);
	setUpDefaultUpDown(element.liveButton);
	setUpDefaultUpDown(element.pauseButton);
	setUpDefaultUpDown(element.chPlusButton);
	setUpDefaultUpDown(element.chMinusButton);	
	
	element.playButton.addEvent('click', function() {
		mediabarElement.onplayclick();
	});
	element.liveButton.addEvent('click', function() {
		mediabarElement.onliveclick();
	});
	element.pauseButton.addEvent('click', function() {
		mediabarElement.onpauseclick();
	});
	element.chPlusButton.addEvent('click', function() {
		mediabarElement.onchplusclick();
	});
	element.chMinusButton.addEvent('click', function() {
		mediabarElement.onchminusclick();
	});
	
	newButtonsLeft.adopt(element.playButton);
	newButtonsLeft.adopt(element.pauseButton);
	newButtonsLeft.adopt(element.liveButton);
	newButtonsLeft.adopt(element.chPlusButton);
	newButtonsLeft.adopt(element.chMinusButton);
	
	return newButtonsLeft;
}

function scrubbarMove(event) {
	difX = event.page.x - oldMouseX;
	scrubbarKnobLeft = scrubbarKnobLeft + difX;
	
	if (scrubbarKnobLeft < -5)
		scrubbarKnobLeft = -5;
	else if (scrubbarKnobLeft > (scrubbarWidth - 20))
		scrubbarKnobLeft = (scrubbarWidth - 20);
	else
		oldMouseX = event.page.x;
	
	knob.style.left = scrubbarKnobLeft + "px";
	
	var position = (scrubbarKnobLeft + 5) / (scrubbarWidth - 20 + 5);
	mediabarElement.currentTime = position * mediabarElement.getTotalTime();
	mediabarElement.updateTimeText();
	
	mediabarElement.inSeek = true;
	
	event.stop();
	
	return false;
}

function scrubbarEnd(event) {
	window.removeEvent('mousemove', this.scrubbarMove);
	window.removeEvent('mouseup', this.scrubbarEnd);
	
	event.stop();
	
	mediabarElement.onseek(mediabarElement.getCurrentTime());
	
	mediabarElement.inSeek = false;
	
	return false;
}

function createScrubbar(element) {
	var newScrubbar = new Element('div', {
		'class': 'scrubbar',
		'styles': {
			'width': scrubbarWidth + 'px'
		}
	});
	
	element.secondary = new Element('div', {
		'class': 'secondary',
		'styles': {
			'width': secondaryWidth + 'px'
		}
	});
	
	element.primary = new Element('div', {
		'class': 'primary',
		'styles': {
			'width': primaryWidth + 'px'
		}
	});
	
	element.knob = new Element('div', {
		'class': 'b knob',
		'styles': {
			'left': scrubbarKnobLeft + 'px'
		}
	});
	
	var mediabarObject = element;
	
	element.knob.addEvent('mousedown', function(event) {
		mediabarObject.inSeek = true;
		oldMouseX = event.page.x;
		oldMouseY = event.page.y;
		
		window.addEvent('mousemove', mediabarObject.scrubbarMove);
		window.addEvent('mouseup', mediabarObject.scrubbarEnd);
		
		event.stop();
		
		return false;
	});
	
	element.knob.addEvent('click', function(event) {
		event.stop();
		
		return false;
	});
	
	newScrubbar.addEvent('click', function(event) {
		if (mediabarObject.knobVisible) {
			mediabarObject.inSeek = true;
			var pageXPosition = getPageX(mediabarObject.knob);
			
			difX = event.page.x - pageXPosition;
			scrubbarKnobLeft = scrubbarKnobLeft + difX - 11;
			if (scrubbarKnobLeft < -5) 
				scrubbarKnobLeft = -5;
			else 
				if (scrubbarKnobLeft > (scrubbarWidth - 20)) 
					scrubbarKnobLeft = (scrubbarWidth - 20);
			
			mediabarObject.knob.style.left = scrubbarKnobLeft + "px";
			
			event.stop();
			
			var position = (scrubbarKnobLeft + 5) / (scrubbarWidth - 20 + 5);
			mediabarObject.currentTime = position * mediabarObject.getTotalTime();
			mediabarObject.updateTimeText();
			
			mediabarObject.onseek(mediabarObject.getCurrentTime());
			mediabarObject.inSeek = false;
		}
		
		return false;
	});
	
	/* newKnob.addEvent('mouseup', function() {
		newScrubbar.removeEvent('mousemove');
	}); */
	
	newScrubbar.adopt(element.secondary);
	newScrubbar.adopt(element.primary);
	newScrubbar.adopt(element.knob);
	
	return newScrubbar;
}

function createTime() {
	newTime = new Element('div', {
		'class': 'time'
	});
	
	newTime.addEvent('mousedown', function(event) {
		return false;
	});
	
	newTime.innerHTML = "00:00 / 00:00";
	
	return newTime;
}

function volumeMove(event) {
	difX = event.page.x - oldMouseX;
	
	volumeKnobWidth = volumeKnobWidth + difX;
	if (volumeKnobWidth < 5)
		volumeKnobWidth = 5;
	else if (this.volumeKnobWidth > 60)
		volumeKnobWidth = 60;
	else
		oldMouseX = event.page.x;
	
	volumeKnob.style.width = volumeKnobWidth + "px";
	
	mediabarElement.volume = (volumeKnobWidth - 5) / 55;
	
	mediabarElement.onvolumechange(mediabarElement.volume);
	
	event.stop();
	
	return false;
}

function volumeEnd(event) {
	window.removeEvent('mousemove', volumeMove);
	window.removeEvent('mouseup', volumeEnd);
	
	event.stop();
	
	return false;
}

function createVolume(element) {
	var newVolume = new Element('div', {
		'class': 'b volume'
	});
	
	var newVolumeBkg = new Element('div', {
		'class': 'b volume-bkg'
	});
	
	element.volumeKnob = new Element('div', {
		'class': 'b volume-knob',
		'styles': {
			'width': volumeKnobWidth + 'px'
		}
	});
	
	var mediabarObject = element;
	
	newVolumeBkg.addEvent('mousedown', function(event) {
		oldMouseX = event.page.x;
		oldMouseY = event.page.y;
		
		var pageXPosition = getPageX(newVolumeBkg);
		
		difX = oldMouseX - pageXPosition;
		volumeKnobWidth = difX;
		if (volumeKnobWidth < 5)
			volumeKnobWidth = 5;
		else if (volumeKnobWidth > 60)
			volumeKnobWidth = 60;
		else
			oldMouseX = event.page.x;
		
		mediabarObject.volumeKnob.style.width = volumeKnobWidth + "px";
		
		mediabarElement.volume = (volumeKnobWidth - 5) / 55;
		
		window.addEvent('mousemove', mediabarObject.volumeMove);
		window.addEvent('mouseup', mediabarObject.volumeEnd);
		
		event.stop();
		
		mediabarElement.onvolumechange(mediabarElement.volume);
		
		return false;
	});
	
	newVolumeBkg.addEvent('click', function(event) {
		event.stop();
		
		return false;
	});
	
	newVolume.addEvent('click', function(event) {
		mediabarObject.volumeMuted = !mediabarObject.volumeMuted;
		if (mediabarObject.volumeMuted == true) {
			if (!newVolume.hasClass('muted'))
				newVolume.addClass('muted');
		} else {
			if (newVolume.hasClass('muted'))
				newVolume.removeClass('muted');
		}
		
		mediabarElement.onmute();
		
		return false;
	});
	
	newVolumeBkg.adopt(element.volumeKnob);
	newVolume.adopt(newVolumeBkg);
	
	return newVolume;
}

function createButtonsRight(element) {
	var newButtonsRight = new Element('div', {
		'class': 'buttons right'
	});
	
	element.fullscreenButton = new Element('div', {
		'class': 'b fullscreen'
	});
	
	element.contextButton = new Element('div', {
		'class': 'b context'
	});
	
	element.fullscreenButton.addEvent('click', function() {
		mediabarElement.onfullscreenclick();
	});
	element.contextButton.addEvent('click', function() {
		mediabarElement.oncontextclick();
	});
	
	setUpDefaultUpDown(element.fullscreenButton);
	setUpDefaultUpDown(element.contextButton);
	
	newButtonsRight.adopt(element.fullscreenButton);
	newButtonsRight.adopt(element.contextButton);
	
	return newButtonsRight;
}

function padZeros(number) {
	if ((number < 10) && (number >= 0))
		return "0" + number;
	else
		return number;
}

function formatTime(timeMili) {
	var seconds = Math.floor(timeMili / 1000);
	var minutes = Math.floor(seconds / 60);
	seconds = seconds - (minutes * 60);
	var hours = Math.floor(minutes / 60);
	minutes = minutes - (hours * 60);
	
	var returnText = padZeros(minutes) + ":" + padZeros(seconds);
	if (hours > 0)
		returnText = hours + ":" + returnText;
		
	return returnText;
}

function updateTimeText() {
	this.time.innerHTML = formatTime(this.currentTime) + " / " + formatTime(this.totalTime);
}

function timeChanged() {
	if (!this.inSeek) {
		this.updateTimeText();
		
		var totalWidth = scrubbarWidth + 5 - 20;
		var ratioPosition = this.currentTime / this.totalTime;
		scrubbarKnobLeft = (ratioPosition * totalWidth) - 5;
		
		this.knob.style.left = scrubbarKnobLeft + "px";
	}
	
	ratioPosition = this.primaryTime / this.totalTime;
	primaryWidth = (ratioPosition * (scrubbarWidth - 2));
	this.primary.style.width = primaryWidth + "px";
	
	ratioPosition = this.secondaryTime / this.totalTime;
	secondaryWidth = (ratioPosition * (scrubbarWidth - 2));
	this.secondary.style.width = secondaryWidth + "px";
}

function Mediabar(element) {
	this.target = element;
	
	this.target.addClass('controler');
	this.target.empty();
	
	emptyFunction = function() {};
	
	this.onplayclick = emptyFunction;
	this.onpauseclick = emptyFunction;
	this.onliveclick = emptyFunction;
	this.onchplusclick = emptyFunction;
	this.onchminusclick = emptyFunction;
	this.onfullscreenclick = emptyFunction;
	this.oncontextclick = emptyFunction;
	this.onseek = emptyFunction;
	this.onvolumechange = emptyFunction;
	this.onmute = emptyFunction;
	
	this.timeChanged = timeChanged;
	this.updateTimeText = updateTimeText;
	
	this.totalTime = 0.0;
	this.currentTime = 0.0;
	this.secondaryTime = 0.0;
	this.primaryTime = 0.0;
	
	this.inSeek = false;
	
	this.knobVisible = true;
	
	this.volume = 0;
	
	this.width = 288 + scrubbarWidth;
	
	this.setTotalTime = function(newTotalTime) {
		this.totalTime = newTotalTime;
		this.timeChanged();
	};
	this.getTotalTime = function() {
		return this.totalTime;
	};
	
	this.getCurrentTime = function() {
		return this.currentTime;
	};
	this.setCurrentTime = function(newCurrentTime) {
		this.currentTime = newCurrentTime;
		this.timeChanged();
	};
	this.setCurrentPosition = function(newCurrentPosition) {
		this.currentTime = newCurrentPosition * this.totalTime;
		this.timeChanged();
	};
	
	this.getKnobVisible = function() {
		return this.knobVisible;
	};
	this.setKnobVisible = function(visible) {
		this.knobVisible = visible;
		if (this.knobVisible) {
			if (this.knob.hasClass('hide')) 
				this.knob.removeClass('hide');
		}
		else {
			if (!this.knob.hasClass('hide')) 
				this.knob.addClass('hide');
		}
	};
	
	this.getSecondaryTime = function() {
		return this.secondaryTime;
	};
	this.setSecondaryTime = function(newSecondaryTime) {
		this.secondaryTime = newSecondaryTime;
		this.timeChanged();
	};
	
	this.getPrimaryTime = function() {
		return this.primaryTime;
	};
	this.setPrimaryTime = function(newPrimaryTime) {
		this.primaryTime = newPrimaryTime;
		this.timeChanged();
	};
	
	this.getVolume = function() {
		return this.volume;
	};
	this.setVolume = function(newVolume) {
		this.volume = newVolume;
		this.volumeKnob.style.width = ((this.volume) * 55) + 5 + "px";
	};
	
	this.isMute = function() {
		return this.volumeMuted;
	};
	this.setMute = function(newMute) {
		this.volumeMuted = newMute;
		if (newMute) {
			if (!this.volumebar.hasClass('muted')) {
				this.volumebar.addClass('muted');
			}
		} else {
			if (this.volumebar.hasClass('muted')) {
				this.volumebar.removeClass('muted');
			}
		}
	};
	
	this.setWidth = function(width) {
		scrubbarWidth = width - 288;
		if (scrubbarWidth >= 100) {
			if (this.scrubbar.hasClass('hide'))
				this.scrubbar.removeClass('hide');
			this.scrubbar.style.width = scrubbarWidth + "px";
			this.timeChanged();
			this.width = width;
		}
		else {
			scrubbarWidth = 100;
			if (!this.scrubbar.hasClass('hide'))
				this.scrubbar.addClass('hide');
			this.width = 288;
		}
	};
	this.getWidth = function() {
		return this.width;
	};
	
	this.setPlayVisible = function(visible) {
		if (visible) {
			if (this.playButton.hasClass('hide')) this.playButton.removeClass('hide');
		}
		else {
			if (!this.playButton.hasClass('hide')) this.playButton.addClass('hide');
		}
	};
	this.getPlayVisible = function() {
		return (!this.playButton.hasClass('hide'));
	};
	
	this.setPauseVisible = function(visible) {
		if (visible) {
			if (this.pauseButton.hasClass('hide')) this.pauseButton.removeClass('hide');
		}
		else {
			if (!this.pauseButton.hasClass('hide')) this.pauseButton.addClass('hide');
		}
	};
	this.getPauseVisible = function() {
		return (!this.pauseButton.hasClass('hide'));
	};
	
	this.setLiveVisible = function(visible) {
		if (visible) {
			if (this.liveButton.hasClass('hide')) this.liveButton.removeClass('hide');
		}
		else {
			if (!this.liveButton.hasClass('hide')) this.liveButton.addClass('hide');
		}
	};
	this.getliveVisible = function() {
		return (!this.liveButton.hasClass('hide'));
	};
	
	this.setChPlusVisible = function(visible) {
		if (visible) {
			if (this.chPlusButton.hasClass('hide')) this.chPlusButton.removeClass('hide');
		}
		else {
			if (!this.chPlusButton.hasClass('hide')) this.chPlusButton.addClass('hide');
		}
	};
	this.getChPlusVisible = function() {
		return (!this.chPlusButton.hasClass('hide'));
	};
	
	this.setChMinusVisible = function(visible) {
		if (visible) {
			if (this.chMinusButton.hasClass('hide')) this.chMinusButton.removeClass('hide');
		}
		else {
			if (!this.chMinusButton.hasClass('hide')) this.chMinusButton.addClass('hide');
		}
	};
	this.getChMinusVisible = function() {
		return (!this.chMinusButton.hasClass('hide'));
	};
	
	this.addEvent = function (eventId, functionObject) {
		switch(eventId) {
			case 'playclick':
				this.onplayclick = functionObject;
				break;
			case 'pauseclick':
				this.onpauseclick = functionObject;
				break;
			case 'liveclick':
				this.onliveclick = functionObject;
				break;
			case 'chplusclick':
				this.onchplusclick = functionObject;
				break;
			case 'chminusclick':
				this.onchminusclick = functionObject;
				break;
			case 'fullscreenclick':
				this.onfullscreenclick = functionObject;
				break;
			case 'contextclick':
				this.oncontextclick = functionObject;
				break;
			case 'seek':
				this.onseek = functionObject;
				break;
			case 'volumechange':
				this.onvolumechange = functionObject;
				break;
			case 'mute':
				this.onmute = functionObject;
				break;
		}
	};
	
	this.playButton = null;
	this.liveButton = null;
	this.pauseButton = null;
	this.chPlusButton = null;
	this.chMinusButton = null;	
	
	this.fullscreenButton = null;
	this.contextButton = null;
	
	this.second = null;
	this.primary = null;
	this.knob = null;
	
	this.volumeKnob = null;
	
	this.scrubbarMove = scrubbarMove;
	this.scrubbarEnd = scrubbarEnd;
	this.volumeMove = volumeMove;
	this.volumeEnd = volumeEnd;
	
	this.buttonsLeft = createButtonsLeft(this);
	this.scrubbar = createScrubbar(this);
	this.time = createTime(this);
	this.volumebar = createVolume(this);
	this.buttonsRight = createButtonsRight(this);
	
	this.playButton.addEvent('click', this.onplayclick);
	this.pauseButton.addEvent('click', this.onpauseclick);
	this.liveButton.addEvent('click', this.onliveclick);
	this.chPlusButton.addEvent('click', this.onchplusclick);
	this.chMinusButton.addEvent('click', this.onchminusclick);
	
	this.target.adopt(this.buttonsLeft);
	this.target.adopt(this.scrubbar);
	this.target.adopt(this.time);
	this.target.adopt(this.volumebar);
	this.target.adopt(this.buttonsRight);
	
	volumeKnob = this.volumeKnob;
	knob = this.knob;
	
	this.setWidth(this.target.clientWidth - 10);
	
	window.addEvent('resize', function() {
		mediabarElement.setWidth(mediabarElement.target.clientWidth - 10);
	});
}

function handleSeek(newTimePosition) {
	//console.log(newTimePosition);
	var vlc = $('myVlc');
	var newPosition = newTimePosition / mediabarElement.getTotalTime();
	if (newPosition < 0.00001)
		vlc.input.position = 0.00001;
	else
		vlc.input.position = newPosition;
	//mediabarElement.setSecondaryTime(newTimePosition);
}

function handleVolume(newVolume) {
	//console.log(newVolume);
	var vlc = $('myVlc');
	vlc.audio.volume = newVolume * 100;	
	mediabarElement.setMute(vlc.audio.mute);
}

function handleMuted() {
	//console.log(mediabarElement.getMute());
	var vlc = $('myVlc');
	vlc.audio.mute = mediabarElement.isMute();
	mediabarElement.setVolume(vlc.audio.volume / 100);
}

function handlePlay() {
	mediabarElement.setPlayVisible(false);
	mediabarElement.setPauseVisible(true);
	
	var vlc = $('myVlc');
	vlc.playlist.togglePause();
}

function handlePause() {
	mediabarElement.setPlayVisible(true);
	mediabarElement.setPauseVisible(false);
	
	var vlc = $('myVlc');
	vlc.playlist.togglePause();
}

function handleLive() {
	mediabarElement.setPlayVisible(false);
	mediabarElement.setPauseVisible(false);
	mediabarElement.setLiveVisible(false);
	mediabarElement.setChPlusVisible(true);
	mediabarElement.setChMinusVisible(true);
	mediabarElement.setKnobVisible(false);
}

function handleFullscreen() {
	var vlc = $('myVlc');
	vlc.video.fullscreen = true;
}

function vlcResizeHandler() {
	var vlc = $('myVlc');
	vlc.width = rootElement.clientWidth;
	vlc.height = (typeof window.innerHeight != 'undefined' ? window.innerHeight : document.body.offsetHeight) - 40;
}

function bootVlc(volume) {
	var vlc = $('myVlc');
	if (typeof vlc.audio != 'undefined') {
		vlc.audio.volume = volume * 100;
		window.setInterval(this.refreshVlc, 500);	
	}
	else {
		window.setTimeout(this.bootVlc, 500, volume);
	}
}

function refreshVlc() {
	var vlc = $('myVlc');
	
	var position = vlc.input.position;
	var state = vlc.input.state;
	
	if (state == 3) {
		mediabarElement.setPlayVisible(false);
		mediabarElement.setPauseVisible(true);
	}
	else if (state == 4) {
		mediabarElement.setPauseVisible(false);
		mediabarElement.setPlayVisible(true);	
	}
	
	mediabarElement.setTotalTime(20 * 60 * 1000);
	mediabarElement.setCurrentPosition(position);
}

function Media(element, startMrl) {
	
	this.rootElement = element;
	rootElement = this.rootElement;
	
	vlcPlaceholder = new Element('div', {
		'id': 'vlcPlaceholder'
	});
	
	mediabarPlaceholder = new Element('div', {
		'id': 'mediabarPlaceholder'
	});
	
	this.getPlayer = function() {
		return $('myVlc');
	}
	
	element.adopt(vlcPlaceholder);
	element.adopt(mediabarPlaceholder);
	
	var vlcWidth = rootElement.clientWidth;
	var vlcHeight = (typeof window.innerHeight != 'undefined' ? window.innerHeight : document.body.offsetHeight) - 40;
	
	var myVlc = new VLCObject("myVlc", vlcWidth, vlcHeight, "0.9.0");
    //myVlc.addParam("MRL","udp://@239.100.0.3:1233");
	//myVlc.addParam("MRL", "http://vegas.cm.p.lodz.pl/pvr/0a15f0b09d8046457ac159fd2ad8d47b.ts");
	//myVlc.addParam("MRL", "http://212.51.216.109:8081/bynumber/1");
	myVlc.addParam("MRL", startMrl);
    myVlc.write(vlcPlaceholder);
	
	window.addEvent('resize', vlcResizeHandler);
	
	mediabarElement = new Mediabar(mediabarPlaceholder);
	
	this.mediabar = mediabarElement;
	
	this.bootVlc = bootVlc;
	this.refreshVlc = refreshVlc;
	
	/* mediabarElement.setTotalTime((1 * 60 + 20) * 60 * 1000);
	mediabarElement.setCurrentPosition(0.5);
	mediabarElement.setPrimaryTime(20 * 60 * 1000);
	mediabarElement.setSecondaryTime(30 * 60 * 1000); */
	mediabarElement.setKnobVisible(true);
	mediabarElement.setVolume(1.0);
	mediabarElement.setMute(false);
	mediabarElement.addEvent('seek', handleSeek);
	mediabarElement.addEvent('playclick', handlePlay);
	mediabarElement.addEvent('pauseclick', handlePause);
	mediabarElement.addEvent('liveclick', handleLive);
	mediabarElement.addEvent('fullscreenclick', handleFullscreen);
	mediabarElement.addEvent('volumechange', handleVolume);
	mediabarElement.addEvent('mute', handleMuted);
	
	window.setTimeout(this.bootVlc, 500, 1.0);
}

var throbber;

function padZeros(number) {
	if ((number < 10) && (number >= 0))
		return "0" + number;
	else
		return number;
}

function toRomanNumerals(number) {
	switch (number) {
		case 1:
			return "I";
		case 2:
			return "II";
		case 3:
			return "III";
		case 4:
			return "IV";
		case 5:
			return "V";
		case 6:
			return "VI";
		case 7:
			return "VII";
		case 8:
			return "VIII";
		case 9:
			return "IX";
		case 10:
			return "X";
		case 11:
			return "XI";
		case 12:
			return "XII";
	}
}

function toDayName(number) {
	switch (number) {
		case 0:
			return "Sun";
		case 1:
			return "Mon";
		case 2:
			return "Tue";
		case 3:
			return "Wed";
		case 4:
			return "Thu";
		case 5:
			return "Fri";
		case 6:
			return "Sat";
	}
}

function compareDates(dateA, dateB) {
	if (dateA.get('Year') == dateB.get('Year'))
		if (dateA.get('Month') == dateB.get('Month'))
			if (dateA.get('Date') == dateB.get('Date'))
				return true;
	return false;
}

function Dayscrub(element, beginDate, span) {
	var target = element;
	var days = [];
	this.rootElement = element;
	
	target.addClass('dayscrub');
	
	this.refreshCurrent = function() {
		days.forEach(function(dayItem) {
			elementDate = dayItem.retrieve('date');
			if (compareDates(elementDate, currentDate)) {
				if (!dayItem.hasClass('current')) {
					dayItem.addClass('current');
				}
			}
			else {
				if (dayItem.hasClass('current')) {
					dayItem.removeClass('current');
				}
			}
		});
	};
	
	this.addDay = function(date) {
		var newDay = new Element('div', {
			'class': 'day'
		});
		
		var today = Date();
		if (today == date) {
			newDay.addClass('today');
		}
		newDay.innerHTML = date.get('Date');
		
		var newDayOfWeek = new Element('div', {
			'class': 'dow'
		});
		newDayOfWeek.innerHTML = toDayName(date.get('Day'));
		newDay.adopt(newDayOfWeek);
		
		var onClick = function() {
			var urlParams = location.href.split("#");
			location.href = urlParams[0] + "#" + date.get('Year') + "-" + padZeros(date.get('Month') + 1) + "-" + padZeros(date.get('Date'));
		};
		
		newDay.addEvent('click', onClick);
		
		date.clearTime();
		newDay.store('date', date);
		
		days.include(newDay);
		
		return newDay;
	};
	
	this.addMonth = function(date) {
		var newMonth = new Element('div', {
			'class': 'month'
		});
		var newMonthDesc = new Element('div', {
			'class': 'desc'
		});
		newMonthDesc.innerHTML = toRomanNumerals(date.get('Month') + 1);
		newMonth.adopt(newMonthDesc);
		return newMonth;
	};
	
	target.empty();
	
	target.adopt(throbber);
	
	var monthNumber = 0;
	var monthElement = null;
	
	for (i = 0; i < span; i++) {
		var iDate = new Date(beginDate).increment('day', i);
		if ((iDate.get('month') + 1) != monthNumber) {
			monthNumber = iDate.get('month') + 1;
			monthElement = this.addMonth(iDate);
			target.adopt(monthElement);
		}
		var newDayElement = this.addDay(iDate);
		monthElement.adopt(newDayElement);
	}
};

function Channelscrub(element) {
	var target = element;
	
	var oldMouseX = 0;
	var oldMouseY = 0;
	
	this.rootElement = element;
	var knobMoving = false;
	var knobBaseOffset = 0;
	var knobTempOffset = 0;
	
	target.empty();
	
	target.addClass("channelscrub");
	
	var channelKnobWidth = 0;
	var channelKnobOffset = 0;
	var allChannelsWidth = 0;
	
	// Even in JavaScript some double entandre is nice ;)
	var knobExtended = false;
	var knobMouseIn = false;
	
	var totalChannels = 0;
	
	var emptyFunction = function() { return; };
	
	var changeCallback = emptyFunction;
	
	var newChannelKnobRail = new Element('div', {
		'class': 'channelKnobRail',
		'styles': {
			'width' : allChannelsWidth + 'px',
			'height': '5px',
			'margin-top': '-10px'
		}
	});
	
	newChannelKnobRail.addEvent('click', function(event) {
		if (knobMoving == false) {
			knobTempOffset = (event.page.x - getPageX(newChannelKnobRail)) - (channelKnobWidth/2);
			
			if (knobTempOffset > (39 * totalChannels - 5 - channelKnobWidth)) {
				knobTempOffset = (39 * totalChannels - 5 - channelKnobWidth);
			}
			
			if (knobTempOffset < 0) {
				knobTempOffset = 0;
			}
			
			var newFitsStarts = Math.round(knobTempOffset / 39);
			if (lastFitsStarts != newFitsStarts) {
				lastFitsStarts = newFitsStarts;
				changeCallback(newFitsStarts);
			}
			
			moveKnobToPixel(knobTempOffset);
		}
	});
	
	var newChannelKnob = new Element('div', {
		'class': 'channelKnob',
		'styles': {
			'width': channelKnobWidth + 'px',
			'margin-left': channelKnobOffset + 'px'
		}
	});
	
	var knobExtend = function() {
		knobExtended = true;
		var knobMarginTween = new Fx.Tween(newChannelKnobRail, {
			'property': 'margin-top',
			'unit': 'px',
			'duration': 100,
			'transition': 'sine:in:out'
		});
		var knobRailHeightTween = new Fx.Tween(newChannelKnobRail, {
			'property': 'height',
			'unit': 'px',
			'duration': 100,
			'transition': 'sine:in:out'
		});
		knobMarginTween.start(-16);
		knobRailHeightTween.start(11);
	};
	
	var knobCollapse = function() {
		knobExtended = false;
		var knobMarginTween = new Fx.Tween(newChannelKnobRail, {
			'property': 'margin-top',
			'unit': 'px',
			'duration': 100,
			'transition': 'sine:in:out'
		});
		var knobRailHeightTween = new Fx.Tween(newChannelKnobRail, {
			'property': 'height',
			'unit': 'px',
			'duration': 100,
			'transition': 'sine:in:out'
		});
		knobMarginTween.start(-10);
		knobRailHeightTween.start(5);
	};
	
	target.parentNode.addEvent('mouseenter', function(event) {
		knobMouseIn = true;
		knobExtend();
	});
	target.parentNode.addEvent('mouseleave', function(event) {
		knobMouseIn = false;
		if (!knobMoving) {
			knobCollapse();
		}
	});
	
	var lastFitsStarts = 0;
	
	var knobMove = function(event) {
		knobTempOffset = (knobBaseOffset + event.page.x - oldMouseX);
		
		if (knobTempOffset > (39 * totalChannels - 5 - channelKnobWidth)) {
			knobTempOffset = (39 * totalChannels - 5 - channelKnobWidth);
		}
		
		if (knobTempOffset < 0) {
			knobTempOffset = 0;
		}
		
		var newFitsStarts = Math.round(knobTempOffset / 39);
		if (lastFitsStarts != newFitsStarts) {
			lastFitsStarts = newFitsStarts;
			changeCallback(newFitsStarts);
		}
		
		newChannelKnob.style.marginLeft = knobTempOffset + 'px';
	};
	
	var moveKnobToPixel = function(newChannelKnobOffset) {
		channelKnobModulo = newChannelKnobOffset % 39;
		
		var targetKnobOffset = newChannelKnobOffset;
		if (channelKnobModulo < 19) {
			targetKnobOffset = newChannelKnobOffset - channelKnobModulo;
		} else if (channelKnobModulo >= 19) {
			targetKnobOffset = newChannelKnobOffset + (39 - channelKnobModulo);
		}
		
		var knobTween = new Fx.Tween(newChannelKnob, {
			'property': 'margin-left',
			'unit': 'px',
			'duration': 'short',
			'transition': 'sine:out'
		});
		knobTween.start(targetKnobOffset);
		
		channelKnobOffset = targetKnobOffset;
	};
	
	var knobEnd = function(event) {
		window.removeEvent('mousemove', knobMove);
		window.removeEvent('mouseup', knobEnd);
		knobMoving = false;
		
		channelKnobOffset = knobTempOffset;
		
		channelKnobModulo = channelKnobOffset % 39;
		if (channelKnobModulo != 0) {
			moveKnobToPixel(channelKnobOffset);
		}
		
		if (knobExtended && (!knobMouseIn)) {
			knobCollapse();
		}
		
		var newFitsStarts = Math.round(channelKnobOffset / 39);
		if (lastFitsStarts != newFitsStarts) {
			lastFitsStarts = newFitsStarts;
			changeCallback(newFitsStarts);
		}
		
		event.stop();
		event.stopPropagation();
		
		return false;
	};
	
	newChannelKnob.addEvent('click', function(event) {
		event.stopPropagation();
	});
	
	newChannelKnob.addEvent('mousedown', function(event) {
		knobMoving = true;
		oldMouseX = event.page.x;
		oldMouseY = event.page.y;
		
		knobBaseOffset = channelKnobOffset;
		
		lastFitsStarts = channelKnobOffset / 39;
		
		window.addEvent('mousemove', knobMove);
		window.addEvent('mouseup', knobEnd);
		
		event.stop();
		
		return false;
	});
	
	var newChannelList = new Element('div', {
		'class': 'channelButtons'
	});
	
	this.refreshKnobPosition =  function(fits, startsForFit) {
		channelKnobWidth = 39 * fits - 5;
		channelKnobOffset = 39 * startsForFit;
		
		newChannelKnob.style.width = channelKnobWidth + 'px';
		newChannelKnob.style.marginLeft = channelKnobOffset + 'px';
	};
	
	this.addEvent = function(eventName, newCallback) {
		switch(eventName) {
			case 'change':
				changeCallback = newCallback;
				break;
		}
	};
	
	this.refreshChannels = function(guide) {
		newChannelList.empty();
		
		$each(guide, function(channel) {
			channelIcon = iconSize(channel.icon, 'pico');
			var newChannel = new Element('div', {
				'class': 'channelButton',
				'html': '<img src="img/logos/' + channelIcon + '" width="30" height="30" alt="' + channel.name + '"/>'
			}); 
			newChannel.addEvent('click', function(event) {
				playChannel(channel.id);
			});
			this.adopt(newChannel);
		},
		newChannelList);
		
		totalChannels = guide.length;
		allChannelsWidth = (totalChannels * 39) - 5;
		newChannelKnobRail.style.width = allChannelsWidth + 'px';
	};
	
	target.adopt(newChannelList);
	target.adopt(newChannelKnobRail);
	newChannelKnobRail.adopt(newChannelKnob);
}

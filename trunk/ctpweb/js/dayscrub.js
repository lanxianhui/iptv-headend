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

function Dayscrub(element, beginDate, span) {
	var target = element;
	this.rootElement = element;
	
	target.addClass('dayscrub');
	
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
		
		newDay.store(date);
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
	
	var monthNumber = 0;
	var monthElement = null;
	
	for (i = 0; i < span; i++) {
		var currentDate = new Date(beginDate).increment('day', i);
		if ((currentDate.get('month') + 1) != monthNumber) {
			monthNumber = currentDate.get('month') + 1;
			monthElement = this.addMonth(currentDate);
			target.adopt(monthElement);
		}
		var newDayElement = this.addDay(currentDate);
		monthElement.adopt(newDayElement);
	}
};

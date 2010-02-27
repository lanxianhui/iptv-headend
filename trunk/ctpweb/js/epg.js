function showActions(event) {
	$$(".actions").removeClass("hidden");
}

function attachEvents() {
	$$(".program").addEvent('click', showActions);
}

window.addEvent('domready', attachEvents);

function attachEvents() {
	$each($$(".program"), function(program) {
		program.addEvent('click',
			function() {
				program.toggleClass("active");
			}
		);
	});
}

window.addEvent('domready', attachEvents);

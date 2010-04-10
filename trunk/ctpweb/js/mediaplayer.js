function bootscripts() {
	var media = new Media($('media'), "http://vegas.cm.p.lodz.pl/pvr/0a15f0b09d8046457ac159fd2ad8d47b.ts");
}

window.addEvent('domready', bootscripts);

/* global QUnit */
QUnit.config.autostart = false;

sap.ui.getCore().attachInit(function() {
	"use strict";

	sap.ui.require([
		"sap/ui/s4hana/extends4/print/test/integration/AllJourneys"
	], function() {
		QUnit.start();
	});
});

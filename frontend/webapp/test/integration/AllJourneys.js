sap.ui.define([
	"sap/ui/test/Opa5",
	"./arrangements/Startup",
	"./NavigationJourney",
	"./HomeJourney",
	"./PackagesJourney",
	"./PrintJourney",
	"./ErrorJourney"
], function (Opa5, Startup) {
	"use strict";

	Opa5.extendConfig({
		arrangements: new Startup(),
		viewNamespace: "sap.ui.s4hana.extends4.print.view.",
		autoWait: true
	});
});

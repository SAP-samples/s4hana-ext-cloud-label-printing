sap.ui.define([
	"sap/ui/s4hana/extends4/print/localService/mockserver",
	"sap/m/MessageBox"
], function (mockserver, MessageBox) {
	"use strict";

	var aMockservers = mockserver.initAll();

	Promise.all(aMockservers).catch(function (oError) {
		MessageBox.error(oError.message);
	}).finally(function () {
		// initialize the embedded component on the HTML page
		sap.ui.require(["sap/ui/core/ComponentSupport"]);
	});
}); 
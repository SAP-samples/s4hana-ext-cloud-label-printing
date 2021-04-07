/*global QUnit*/

sap.ui.define([
	"sap/ui/test/opaQunit",
	"./pages/Home"
], function (opaTest) {
	"use strict";

	QUnit.module("Home Journey: Given the configuration are correct for ST & SF");

	// I see items test; to see the first binding is there otherwise we get an error
	opaTest("Should see the items on the table", function (Given, When, Then) {
		Given.iStartMyApp();

		Then.onTheHomePage.iShouldSeeTheItemsInTable();
	});

	// filter bar test, MINIMUM test here as I dont wanna test the ST and SFB func.
	// test just for the binding, e.g., Filters button is clickable
	opaTest("Should be able to click 'Filters' button", function (Given, When, Then) {
		When.onTheHomePage.iPressOnFilters();

		Then.onTheHomePage.iSeeADialog()
			.and.iCloseTheDialog();

		//Cleanup
		Then.iTeardownMyApp();
	});

});

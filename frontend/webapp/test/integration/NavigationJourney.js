/*global QUnit*/

sap.ui.define([
	"sap/ui/test/opaQunit",
	"./pages/App",
	"./pages/PrintLabel",
	"./pages/Home"
], function (opaTest) {
	"use strict";

	QUnit.module("Navigation Journey");

	opaTest("Should see the initial page of the app", function (Given, When, Then) {
		// Arrangements
		Given.iStartMyApp();

		// Assertions
		Then.onTheAppPage.iShouldSeeTheApp();
	});

	opaTest("Should see the table in the home page", function (Given, When, Then) {
		// Assertions
		Then.onTheHomePage.iShouldSeeTheMainTable();
	});

	opaTest("Should be able to click on an item", function (Given, When, Then) {
		When.onTheHomePage.iPressOnTheItem105();

		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();

		//Cleanup
		Then.iTeardownMyApp();
	});
});

/*global QUnit*/

sap.ui.define([
	"sap/ui/test/opaQunit",
	"./pages/App",
	"./pages/Home"
], function (opaTest) {
	"use strict";

	QUnit.module("Entering #Packages Journey");

	opaTest("Given delivery page opens, should see the delivery id in the title.", function (Given, When, Then) {
		Given.iStartMyApp();
		When.onTheHomePage.iPressOnTheItem105(); // to navigate to start the journey

		Then.onThePrintLabelPage.iShouldSeeTheDeliveryNumber();
	});

	opaTest("Given delivery has items, should see all items listed in the table", function (Given, When, Then) {
		Then.onThePrintLabelPage.iSeeAllDeliveryItemsInTable();
	});

	opaTest("Given delivery page is just opened, should see that the print button is disabled", function (Given, When, Then) {
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsDisabled();
	});

	opaTest("Given an item has a quantity equals to zero, should see #packages field is disabled", function (Given, When, Then) {
		Then.onThePrintLabelPage.iSeePackageFieldIsDisabledForItemWithZeroQuantity();
	});

	opaTest("Given an item has a quantity bigger than zero, should be able to enter #packages", function (Given, When, Then) {
		When.onThePrintLabelPage.iEnterNumberOfPackages("5");

		Then.onThePrintLabelPage.iShouldSeeInputFieldsStateIsNone();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsEnabled();
	});

	opaTest("When user inputs higher than quantity, Then state of #packages field is error and message is shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iEnterNumberOfPackages("42");

		Then.onThePrintLabelPage.iShouldSeeInputFieldsStateIsError();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsDisabled();
	});

	opaTest("When user inputs negative number, Then state of #packages field is error and message is shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iEnterNumberOfPackages("-42");

		Then.onThePrintLabelPage.iShouldSeeInputFieldsStateIsError();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsDisabled();
	});

	opaTest("When user inputs a text, Then state of #packages field is error and message is shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iEnterNumberOfPackages("some text");

		Then.onThePrintLabelPage.iShouldSeeInputFieldsStateIsError();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsDisabled();
	});

	opaTest("When corrects the input, Then state of #packages field is normal and no message is shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iEnterNumberOfPackages("3");

		Then.onThePrintLabelPage.iShouldSeeInputFieldsStateIsNone();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsEnabled();
	});

	opaTest("When corrects the input, Then state of #packages field is normal and no message is shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iPressOnNavigateBack();

		Then.onTheHomePage.iShouldSeeTheItemsInTable();

		When.onTheHomePage.iPressOnTheItem105();

		Then.onThePrintLabelPage.iShouldSeeTheDeliveryNumber();
		Then.onThePrintLabelPage.iSeeAllDeliveryItemsInTable();
		Then.onThePrintLabelPage.iShouldSeePrintButtonIsDisabled();

		Then.iTeardownMyApp();
	});

});

/*global QUnit*/

sap.ui.define([
	"sap/ui/test/opaQunit",
	"./pages/TechnicalError",
	"./pages/Home",
	"./pages/PrintLabel",
	"./pages/PrintLabelDialog"
], function (opaTest) {
	"use strict";

	QUnit.module("Technical Error Journey");

	opaTest("Given that the app starts for the first time", function (Given, When, Then) {
		Given.iStartTheAppWithBackendErrors();
		Then.onTheTechnicalErrorPage.iShouldSeeTheErrorDialog();
		When.onTheTechnicalErrorPage.iPressOnTheLink();
		When.onTheTechnicalErrorPage.iCloseTheDialog();
		Then.onTheTechnicalErrorPage.iShouldSeeTechnicalErrorPage();
        Then.iTeardownMyApp();
	});

 	opaTest("Given that the app starts and user changes the url to a print page", function (Given, When, Then) {
		Given.iStartMyApp();
		When.onTheTechnicalErrorPage.iChangeTheHashToNonExistingDelivery();
		Then.onTheTechnicalErrorPage.iShouldSeeTheNotFoundPage();
	}); 

	opaTest("Given that the not found page is shown", function (Given, When, Then) {
		When.onTheTechnicalErrorPage.iPressOnNavigateBack();
		Then.onTheHomePage.iShouldSeeTheItemsInTable();
	});

	opaTest("Given that there is an error in the backend and get print queues and/or get templates fails", function (Given, When, Then) {
		When.onTheHomePage.iPressOnTheItem105();
		When.onThePrintLabelPage.iEnterNumberOfPackages("5");
		When.onThePrintLabelPage.iPressOnPrintButton();
		Then.onThePrintLabelDialog.iSeeADialog();
		When.onThePrintLabelDialog.iChooseNonExistingTemplateFromTheDropdown()
			.and.iChooseAQueueFromTheDropdown()	
			.and.iPressOnDialogPreviewButton();
		Then.onTheTechnicalErrorPage.iShouldSeeTheErrorDialog();
		When.onTheTechnicalErrorPage.iCloseTheDialog();
		Then.onTheTechnicalErrorPage.iShouldSeeTechnicalErrorPage();
		When.onTheTechnicalErrorPage.iCallHistoryBack();
		Then.onTheHomePage.iShouldSeeTheItemsInTable();    
	});

	opaTest("Given that there is multiple delivery items & user selects only one", function (Given, When, Then) {
		When.onTheHomePage.iPressOnTheItem103();
		When.onThePrintLabelPage.iEnterNumberOfPackages("3");
		When.onThePrintLabelPage.iPressOnPrintButton();
		Then.onTheTechnicalErrorPage.iShouldSeeTheErrorDialog();
		When.onTheTechnicalErrorPage.iCloseTheDialog();
		When.onTheTechnicalErrorPage.iCallHistoryBack();
		Then.onTheHomePage.iShouldSeeTheItemsInTable();
	});

	opaTest("Given that user clicks print", function (Given, When, Then) {
		When.onTheHomePage.iPressOnTheItem105();
		When.onThePrintLabelPage.iEnterNumberOfPackages("5");
		When.onThePrintLabelPage.iPressOnPrintButton();
		Then.onThePrintLabelDialog.iSeeADialog();
		When.onThePrintLabelDialog.iChooseNonExistingTemplateFromTheDropdown()
			.and.iChooseAQueueFromTheDropdown()	
			.and.iPressOnDialogPrintButton();
		Then.onTheTechnicalErrorPage.iShouldSeeTheErrorDialog();
		When.onTheTechnicalErrorPage.iCloseTheDialog();
		Then.onTheTechnicalErrorPage.iShouldSeeTechnicalErrorPage();
		Then.iTeardownMyApp();
	});

});

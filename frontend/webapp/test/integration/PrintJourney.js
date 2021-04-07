/*global QUnit*/

sap.ui.define([
	"sap/ui/test/opaQunit",
	"./pages/Home",
	"./pages/PrintLabel",
	"./pages/PrintLabelDialog"
], function (opaTest) {
	"use strict";

	QUnit.module("Print Journey");

	opaTest("Given that the user provides valid input, When user clicks Print button", function (Given, When, Then) {
		Given.iStartMyApp();
		When.onTheHomePage.iPressOnTheItem105(); // to navigate to the Print page
		When.onThePrintLabelPage.iEnterNumberOfPackages("5");
		When.onThePrintLabelPage.iPressOnPrintButton(); // to navigate to the start of the journey

		Then.onThePrintLabelDialog.iSeeADialog();

		// sinon spy to _renderPdf function. This should happen before iPressOnDialogPreviewButton function called for the first time.
		// In the upcoming tests, spy will be used.
		When.onThePrintLabelDialog.iSpyRenderPdf();
		When.onThePrintLabelDialog.iSpyPrintExistingPdf();
	});

	opaTest("Given the print dialog is opened for the first time", function (Given, When, Then) {
		Then.onThePrintLabelDialog.iShouldSeeQueueComboBoxWithElements()
			.and.iShouldSeeTemplateComboBoxWithElements()
			.and.iShouldSeePrintButtonIsDisabled()
			.and.iShouldSeePreviewButtonIsDisabled();
	});

	opaTest("Given that there are templates provided by backend", function (Given, When, Then) {
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown(); 
		Then.onThePrintLabelDialog.iShouldSeePreviewButtonIsEnabled();
	});
	
	opaTest("Given that there are templates & queues provided by backend", function (Given, When, Then) {
		When.onThePrintLabelDialog.iChooseAQueueFromTheDropdown();
		Then.onThePrintLabelDialog.iShouldSeePrintButtonIsEnabled();	
	});
	
	opaTest("Given that the print button is enabled & the pdf was not shown (was not rendered yet)", function (Given, When, Then) {
		When.onThePrintLabelDialog.iPressOnDialogPrintButton();
		Then.onThePrintLabelDialog.iShouldSeeASuccessMessage();
	});

	opaTest("Given that the correct template and configuration is provided in the backend", function (Given, When, Then) {
		When.onThePrintLabelPage.iPressOnPrintButton();
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown()
			.and.iPressOnDialogPreviewButton();

		Then.onThePrintLabelDialog.iShouldSeePdf()
			.and.iCloseThePdfViewer();	
	});

	opaTest("Given the print dialog is opened", function (Given, When, Then) {
		When.onThePrintLabelDialog.iPressOnEscKey();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
	});

	opaTest("Given that the print button is enabled & the pdf was shown (was already rendered)", function (Given, When, Then) {
		When.onThePrintLabelPage.iPressOnPrintButton();
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown()
			.and.iChooseAQueueFromTheDropdown()
			.and.iPressOnDialogPrintButton();
		
		Then.onThePrintLabelDialog.iShouldSeeRenderPdfIsCalled(1) // ~/render api is called only once 
			.and.iShouldSeePrintExistingPdfIsCalled(1) // ~/multipart api is called only once 
			.and.iShouldSeeASuccessMessage();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();			
	});

	opaTest("Given that the pdf preview was shown", function (Given, When, Then) {
		When.onThePrintLabelPage.iPressOnNavigateBack();
		Then.onTheHomePage.iShouldSeeTheItemsInTable();
		When.onTheHomePage.iPressOnTheItem106();
		
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
		When.onThePrintLabelPage.iPressOnNavigateBack();
		When.onTheHomePage.iPressOnTheItem105();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
		When.onThePrintLabelPage.iEnterNumberOfPackages("5")
			.and.iPressOnPrintButton();
		Then.onThePrintLabelDialog.iSeeADialog();
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown()
			.and.iPressOnDialogPreviewButton();
		Then.onThePrintLabelDialog.iShouldSeeRenderPdfIsCalled(1)
			.and.iShouldSeePdf()
			.and.iCloseThePdfViewer();
	});

	opaTest("Given that the pdf preview was shown & user changes the Template", function (Given, When, Then) {
		When.onThePrintLabelDialog.iChooseAnotherTemplateFromTheDropdown()
			.and.iPressOnDialogPreviewButton();
		Then.onThePrintLabelDialog.iShouldSeeRenderPdfIsCalled(2)
			.and.iShouldSeePdf()
			.and.iCloseThePdfViewer();
	});

	opaTest("Given that the pdf preview was shown & user will change number of packages", function(Given, When, Then) {
		When.onThePrintLabelDialog.iPressOnEscKey();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
		When.onThePrintLabelPage.iEnterNumberOfPackages("6")
			.and.iPressOnPrintButton();
		Then.onThePrintLabelDialog.iSeeADialog();
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown()
			.and.iPressOnDialogPreviewButton();
		Then.onThePrintLabelDialog.iShouldSeeRenderPdfIsCalled(3)
			.and.iShouldSeePdf()
			.and.iCloseThePdfViewer();
	});

	opaTest("Given that the pdf preview was shown & user closed the Dialog", function (Given, When, Then) {
		When.onThePrintLabelDialog.iPressOnEscKey();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
		When.onThePrintLabelPage.iPressOnNavigateBack();
		When.onTheHomePage.iPressOnTheItem100();
		Then.onThePrintLabelPage.iShouldSeeTheDeliveryPage();
		When.onThePrintLabelPage.iEnterNumberOfPackages("1")
			.and.iPressOnPrintButton();
		Then.onThePrintLabelDialog.iSeeADialog();
		When.onThePrintLabelDialog.iChooseATemplateFromTheDropdown()
			.and.iPressOnDialogPreviewButton();
		Then.onThePrintLabelDialog.iShouldSeeRenderPdfIsCalled(4)
			.and.iShouldSeePdf()
			.and.iCloseThePdfViewer();
		Then.iTeardownMyApp();
	});
});

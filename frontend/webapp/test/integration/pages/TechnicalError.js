sap.ui.define([
	"sap/ui/test/Opa5",
	"sap/ui/test/matchers/I18NText",
	"sap/ui/test/matchers/PropertyStrictEquals",
	"sap/ui/test/actions/Press",
	"sap/ui/test/matchers/BindingPath",
	"sap/ui/test/matchers/Properties"
], function (Opa5, I18NText, PropertyStrictEquals, Press, BindingPath, Properties) {
	"use strict";
	var viewName = "NotFound";
	Opa5.createPageObjects({
		onTheTechnicalErrorPage: {
			actions: {
				iChangeTheHashToNonExistingDelivery: function () {
					return this.waitFor({
						actions: function () {
							Opa5.getHashChanger().setHash("/PrintLabel/90000104");
						},
						errorMessage: "Not able to change the hash"
					});
				},

				iPressOnTheLink: function () {
					return this.waitFor({
						controlType: "sap.m.Link",
						matchers: new Properties({
							text: "View Details"
						}),
						actions: new Press(),
						errorMessage: "Not able to find link"
					});
				},

				iPressOnNavigateBack: function () {
					return this.waitFor({
						viewName: viewName,
						controlType: "sap.m.Button",
						matchers: new Properties({
							icon: "", /*ui5 renders the icon as a 'sap.ui.core.Icon' and place is as an aggregation to the button*/
							type: sap.m.ButtonType.Back
						}),
						actions: new Press(),
						errorMessage: "Cannot find the nav back button"
					});
				},

				iCallHistoryBack: function () {
					return this.waitFor({
						actions: function () {
							Opa5.getWindow().history.back();
						},
						errorMessage: "Cannot use the history function of the window."
					});
				},

				iCloseTheDialog: function () {
					return this.waitFor({
						controlType: "sap.m.Button",
						matchers: new Properties({
							text: "Close"
						}),
						actions: new Press(),
						errorMessage: "Not able to find the Close Button"
					});
				}
			},

			assertions: {
				iShouldSeeTheErrorDialog: function () {
					return this.waitFor({
						controlType: "sap.m.Dialog",
						matchers: new Properties({
							icon: "sap-icon://message-error"
						}),
						success: function () {
							Opa5.assert.ok(true, "The Error Dialog is open");
						},
						errorMessage: "Not able to find the error dialog"
					});
				},

				iShouldSeeTechnicalErrorPage: function () {
					return this.waitFor({
						controlType: "sap.m.MessagePage",
						matchers: new I18NText({
							key: "genericErrorText",
							propertyName: "text"
						}),
						success: function () {
							Opa5.assert.ok(true, "Technical Error Page Shown");
						},
						errorMessage: "Did not see the Technical Error Page"
					});
				},

				iShouldSeeTheNotFoundPage: function () {
					return this.waitFor({
						controlType: "sap.m.MessagePage",
						matchers: new I18NText({
							key: "xtit.notFound",
							propertyName: "title"
						}),
						success: function () {
							Opa5.assert.ok(true, "MessagePage is shown");
						},
						errorMessage: "Did not see the MessagePage"
					});
				}
			}
		}
	});

});

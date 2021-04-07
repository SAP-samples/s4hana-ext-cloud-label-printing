sap.ui.define([
	"sap/ui/test/Opa5",
	"sap/ui/test/matchers/I18NText",
	"sap/ui/test/matchers/BindingPath",
	"sap/ui/test/matchers/Properties",
	"sap/ui/test/actions/EnterText",
	"sap/ui/test/actions/Press"
], function(Opa5, I18NText, BindingPath, Properties, EnterText, Press) {
	"use strict";
	var sViewName = "PrintLabel";
	Opa5.createPageObjects({
		onThePrintLabelPage: {

			actions: {
				iEnterNumberOfPackages: function (inputText) {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Input",
						matchers: new BindingPath({
							modelName: "itemsModel",
							path : "/2"
						}),
						actions: new EnterText({text : inputText}),
						errorMessage: "Cannot enter text to the input field."
					});
				},

				iPressOnNavigateBack: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Button",
						matchers: new Properties({
							icon: "", /*ui5 renders the icon as a 'sap.ui.core.Icon' and place is as an aggregation to the button*/
							type: sap.m.ButtonType.Back
						}),
						actions: new Press(),
						errorMessage: "Cannot find the nav back button"
					});
				},

				iPressOnPrintButton: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Button",
						matchers: new I18NText({
							key: "xbtn.Print",
							propertyName: "text"
						}),
						actions: new Press(),
						errorMessage: "Cannot find the Print button"
					});
				}
			},

			assertions: {

				iShouldSeeTheDeliveryPage: function () {
					return this.waitFor({
						viewName: sViewName,
						matchers : new I18NText({
							key : "xtit.PrintLabelPage",
							propertyName: "title"
						}),
						success: function () {
							Opa5.assert.ok(true, "The detail page is shown");
						},
						errorMessage: "Did not see the detail page"
					});
				},

				iShouldSeeTheDeliveryNumber: function () {
					return this.waitFor({
						viewName: sViewName,
						matchers : new I18NText({
							propertyName : "title",
							key: "xtit.PrintLabelObjectHeader",
							parameters: [80000105]
						}),
						success: function () {
							Opa5.assert.ok(true, "The detail page is shown");
						},
						errorMessage: "Did not see the detail page"
					});
				},

				iSeeAllDeliveryItemsInTable: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.ColumnListItem",
						success: function (items) {
							Opa5.assert.strictEqual(items.length, 3, "All three ColumnListItems are rendered in the table");
						},
						errorMessage: "Cannot find three ColumnListItems controller in the page."
					});
				},

				iShouldSeePrintButtonIsDisabled: function () {
					return this._createPrintButtonEnabledQueueItem(false);
				},

				iShouldSeePrintButtonIsEnabled: function () {
					return this._createPrintButtonEnabledQueueItem(true);
				},

				_createPrintButtonEnabledQueueItem :function(enabled){
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Button",
						enabled: false, /*This property is needed to search for disabled components as well.*/
						matchers: new I18NText({
							key: "xbtn.Print",
							propertyName : "text"
						}),
						success: function (buttons) {
							Opa5.assert.strictEqual(buttons.length, 1, "There is one print button");
							Opa5.assert.strictEqual(buttons[0].getEnabled(), enabled, `The print button is ${enabled? 'enabled' : 'disabled'}`);
						},
						errorMessage: "Cannot find button with a i18n key 'xbtn.Print'"
					});
				},

				iSeePackageFieldIsDisabledForItemWithZeroQuantity: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.ColumnListItem",
						matchers: new BindingPath({
							modelName: "itemsModel",
							path : "/1"
						}),
						success: function () {
							this.waitFor({
								viewName: sViewName,
								controlType : "sap.m.Input",
								enabled: false, /*This property is needed to search for disabled components as well.*/
								matchers: [
									new Properties({
										enabled: false
									}),
									new I18NText({
										key: "xtip.ZeroQuantityTooltip",
										propertyName: "tooltip"
									})
								],
								success: function () {
									Opa5.assert.ok(true, "Only input field inside the corresponding ColumnListItem is disabled");
								},
								errorMessage: "Only input field inside the corresponding ColumnListItem is not disabled"
							});
						},
						errorMessage: "Cannot find second ColumnListItem controller in the page, where quantity is zero."
					});
				},

				_createInputFieldHasStateQueueItem: function (expectedState) {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Input",
						matchers: [
							new BindingPath({
								modelName: "itemsModel",
								path : "/2"
							}),
							new Properties({
								valueState: expectedState
							})
						],
						errorMessage: `Cannot find the input component with state ${expectedState}`
					});
				},

				iShouldSeeInputFieldsStateIsNone: function () {
					return this._createInputFieldHasStateQueueItem(sap.ui.core.ValueState.None);
				},

				iShouldSeeInputFieldsStateIsError: function () {
					return this._createInputFieldHasStateQueueItem(sap.ui.core.ValueState.Error);
				}
			}
		}
	});

});

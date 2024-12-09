sap.ui.define([
	"sap/ui/test/Opa5",
	"sap/ui/test/matchers/I18NText",
	"sap/ui/test/matchers/PropertyStrictEquals",
	"sap/ui/test/actions/Press",
	"sap/ui/test/matchers/BindingPath"
], function(Opa5, I18NText, PropertyStrictEquals, Press, BindingPath) {
	"use strict";
	var sViewName = "Home";
	Opa5.createPageObjects({
		onTheHomePage: {

			actions: {
				iPressOnFilters: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.Button",
						matchers: new PropertyStrictEquals({
							name : "text",
							value: "Filters"
						}),
						actions: new Press(),
						errorMessage: "Cannot find or press Filters button"
					});
				},

				_createPressOnAnItemQueueItem: function(bindingPath){
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.ColumnListItem",
						matchers: new BindingPath({
							path : bindingPath
						}),
						actions: new Press(),
						errorMessage: "Cannot find or press the list item"
					});
				},
				
				iPressOnTheItem105: function () {
					return this._createPressOnAnItemQueueItem("/A_OutbDeliveryHeader('80000105')")
				},
				
				iPressOnTheItem106: function () {
					return this._createPressOnAnItemQueueItem("/A_OutbDeliveryHeader('80000106')");
				},

				iPressOnTheItem100: function () {
					return this._createPressOnAnItemQueueItem("/A_OutbDeliveryHeader('80000100')");
				}

			},

			assertions: {
				iShouldSeeTheMainTable: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.ui.comp.smarttable.SmartTable",
						matchers : new I18NText({
							key : "xtit.HomeTableTitle",
							propertyName: "header"
						}),
						success: function () {
							Opa5.assert.ok(true, "The main table is displayed");
						},
						errorMessage: "Did not find the home main table"
					});
				},

				iShouldSeeTheItemsInTable: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType : "sap.m.ColumnListItem",
						success: function () {
							Opa5.assert.ok(true, "The main table has items");
						},
						errorMessage: "The main table has no items."
					});
				},

				iSeeADialog: function () {
					return this.waitFor({
						controlType: "sap.m.Dialog",
						success: function () {
							Opa5.assert.ok(true, "a dialog was open");
						},
						errorMessage: "No dialog was open"
					});
				},

				// its actually an action but I just want to have it in the Then part.
				iCloseTheDialog: function () {
					return this.waitFor({
						controlType: "sap.m.Dialog",
						searchOpenDialogs: true,
						success: function (oControl) {
							oControl[0].close();
						},
						errorMessage: "No dialog was open"
					});
				}

			}
		}
	});

});

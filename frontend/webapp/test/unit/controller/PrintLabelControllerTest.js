/*global QUnit*/

sap.ui.define([
	"sap/ui/core/mvc/XMLView",
	"sap/ui/s4hana/extends4/print/controller/PrintLabel.controller",
	'sap/ui/model/json/JSONModel'
], function(XMLView, PrintLabelController, JSONModel) {
	"use strict";

	QUnit.module("PrintLabelController: _getAllInputComponentsRenderedInTable",{

		before: function () {
			this.printLabelController = new PrintLabelController();
			this.stubs = [];

			var createTableStubHelper = function(withInputComponent){
				var cellStubForInputComponent = {
					getMetadata : sinon.stub().returns({
						getElementName: sinon.stub().returns("sap.m.Input")
					})
				};

				var cellStubForOtherComponents = {
					getMetadata : sinon.stub().returns({
						getElementName: sinon.stub().returns("sap.m.OtherComponents")
					})
				};

				var columnListItemStub;
				if(withInputComponent){
					columnListItemStub = {
						getAggregation: sinon.stub().returns( new Array(3).fill(cellStubForOtherComponents, 0, 2).fill(cellStubForInputComponent, 2, 3))
					};
				} else {
					columnListItemStub = {
						getAggregation: sinon.stub().returns(new Array(3).fill(cellStubForOtherComponents))
					};
				}

				var tableStub = {
					getAggregation: sinon.stub().returns( new Array(5).fill(columnListItemStub))
				};

				this.stubs.push(sinon.stub(this.printLabelController, "byId").returns(tableStub));
			}.bind(this);

			this.createTableStubWithInputComponent = function() { createTableStubHelper(true) };

			this.createTableStubWithoutInputComponent = function() { createTableStubHelper(false) };
		},

		afterEach: function () {
			this.stubs.forEach( stub => {stub.restore()});
		}

	});

	QUnit.test("Given that there are 5 Items in the table, Then the result arrays length is 5", function (assert) {
		this.createTableStubWithInputComponent();
		var result =this.printLabelController._getAllInputComponentsRenderedInTable();
		assert.strictEqual(result.length, 5, "There were 5 input components");
	});

	QUnit.test("Given that there are 5 Items in the table but none of them has input component in their aggregation," +
		"Then the result arrays length is 0", function (assert) {

		this.createTableStubWithoutInputComponent();
		var result =this.printLabelController._getAllInputComponentsRenderedInTable();
		assert.strictEqual(result.length, 0, "There were 0 input components");
	});

	QUnit.module("PrintLabelController: _updateViewModelAfterInputEvent", {
		before: function () {
			this.printLabelController = new PrintLabelController();

			// mock _viewModel
			this.printLabelController._viewModel = new JSONModel({
				isOverallValueStateSuccess: true,
				atLeastOneInputHasNonEmptyValue: false,
			});


			this.createInputComponentStub = function(value, valueState){
				return {
					getValue : sinon.stub().returns(value),
					getValueState : sinon.stub().returns(valueState)
				};
			};

			this.stubInputComponentsWithInitialValues = function(){
				this.printLabelController._inputComponents = new Array(5).fill(
					this.createInputComponentStub("", sap.ui.core.ValueState.None)
				);
			}.bind(this);
		}
	});

	QUnit.test("Given that the 'this._inputComponents' are filled with empty initial input components," +
		"Then isOverallValueStateSuccess is true and atLeastOneInputHasNonEmptyValue is false", function (assert) {

		this.stubInputComponentsWithInitialValues();

		// CUT
		this.printLabelController._updateViewModelAfterInputEvent();

		var isOverallValueStateSuccess = this.printLabelController._viewModel.getProperty("/isOverallValueStateSuccess");
		var atLeastOneInputHasNonEmptyValue = this.printLabelController._viewModel.getProperty("/atLeastOneInputHasNonEmptyValue");

		assert.strictEqual(isOverallValueStateSuccess, true, "isOverallValueStateSuccess is not true");
		assert.strictEqual(atLeastOneInputHasNonEmptyValue, false, "atLeastOneInputHasNonEmptyValue is not false");
	});

	QUnit.test("isOverallValueStateSuccess false when there is a input component with invalid state", function (assert) {
		this.stubInputComponentsWithInitialValues();
		this.printLabelController._inputComponents.push(
			this.createInputComponentStub("", sap.ui.core.ValueState.Error)
		);

		// CUT
		this.printLabelController._updateViewModelAfterInputEvent();

		var isOverallValueStateSuccess = this.printLabelController._viewModel.getProperty("/isOverallValueStateSuccess");

		assert.strictEqual(isOverallValueStateSuccess, false, "isOverallValueStateSuccess is not false");
	});

	QUnit.test("atLeastOneInputHasNonEmptyValue false if input components have whitespace '  ' and '0' only values", function (assert) {
		this.printLabelController._inputComponents = new Array(5).fill(
			this.createInputComponentStub("    ", sap.ui.core.ValueState.None)
		);
		this.printLabelController._inputComponents.push(
			this.createInputComponentStub("0", sap.ui.core.ValueState.None)
		);
		this.printLabelController._inputComponents.push(
			this.createInputComponentStub("  0", sap.ui.core.ValueState.None)
		);
		this.printLabelController._inputComponents.push(
			this.createInputComponentStub("  0  ", sap.ui.core.ValueState.None)
		);

		// CUT
		this.printLabelController._updateViewModelAfterInputEvent();

		var atLeastOneInputHasNonEmptyValue = this.printLabelController._viewModel.getProperty("/atLeastOneInputHasNonEmptyValue");

		assert.strictEqual(atLeastOneInputHasNonEmptyValue, false, "atLeastOneInputHasNonEmptyValue is not false");
	});

	QUnit.test("atLeastOneInputHasNonEmptyValue true if there is one input component with a value", function (assert) {
		this.printLabelController._inputComponents = new Array(5).fill(
			this.createInputComponentStub("    ", sap.ui.core.ValueState.None)
		);
		this.printLabelController._inputComponents.push(
			this.createInputComponentStub("42", sap.ui.core.ValueState.None)
		);

		// CUT
		this.printLabelController._updateViewModelAfterInputEvent();

		var atLeastOneInputHasNonEmptyValue = this.printLabelController._viewModel.getProperty("/atLeastOneInputHasNonEmptyValue");

		assert.strictEqual(atLeastOneInputHasNonEmptyValue, true, "atLeastOneInputHasNonEmptyValue is not true");
	});

});

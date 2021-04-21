/*global QUnit*/

sap.ui.define([
	"sap/ui/s4hana/extends4/print/model/numberOfPackagesValidator",
	"sap/ui/model/ValidateException",
	"sap/ui/model/ParseException"
], function(numberOfPackagesValidator, ValidateException, ParseException) {
	"use strict";

	QUnit.module("#Packages Validator _parseValue()");

	QUnit.test("Given the value is a valid number represented as string, then no exception is thrown and value parsed correctly", function (assert) {
		var parsedValue = numberOfPackagesValidator._parseValue("42");
		assert.strictEqual(parsedValue, 42, "Parsed value was not correct for '42'.");

		parsedValue = numberOfPackagesValidator._parseValue("0");
		assert.strictEqual(parsedValue, 0, "Parsed value was not correct for '0'.");

		parsedValue = numberOfPackagesValidator._parseValue("1");
		assert.strictEqual(parsedValue, 1, "Parsed value was not correct for '1'.");
	});

	QUnit.test("Given the value has invalid characters, then ParseException is thrown", function (assert) {
		var input = "+42";
		assert.throws( ()=>{numberOfPackagesValidator._parseValue(input)}
			, ParseException, `ParseException was not thrown for value: ${input}.`);

		input = "-42";
		assert.throws( ()=>{numberOfPackagesValidator._parseValue(input)}
			, ParseException, `ParseException was not thrown for value: ${input}.`);

		input = "4.2";
		assert.throws( ()=>{numberOfPackagesValidator._parseValue(input)}
			, ParseException, `ParseException was not thrown for value: ${input}.`);

		input = "4,2";
		assert.throws( ()=>{numberOfPackagesValidator._parseValue(input)}
			, ParseException, `ParseException was not thrown for value: ${input}.`);

		input = "forty two";
		assert.throws( ()=>{numberOfPackagesValidator._parseValue(input)}
			, ParseException, `ParseException was not thrown for value: ${input}.`);
	});

	QUnit.test("Given the value is null or empty string, then no exception is thrown and empty string is returned", function (assert) {
		var parsedValue = numberOfPackagesValidator._parseValue(null);
		assert.strictEqual(parsedValue, "", "Parsed value was not correct for null");

		parsedValue = numberOfPackagesValidator._parseValue(undefined);
		assert.strictEqual(parsedValue, "", "Parsed value was not correct for undefined");

		parsedValue = numberOfPackagesValidator._parseValue("");
		assert.strictEqual(parsedValue, "", "Parsed value was not correct for ''");
	});


	QUnit.module("#Packages Validator _validateValueWithContext()", {
		beforeEach: function() {
			var getObjectFnStub = sinon.stub().returns({ActualDeliveryQuantity: 42});
			this.context = { getObject: getObjectFnStub };
		}
	});

	QUnit.test("Given the value empty string, then no exception is thrown", function (assert) {
		numberOfPackagesValidator._validateValueWithContext("", null);
		assert.ok(true, "No exception was thrown for the empty string.");
	});

	QUnit.test("Given the value and quantity bigger than value, then no exception is thrown", function (assert) {
		numberOfPackagesValidator._validateValueWithContext(41, this.context);
		assert.ok(true, "No exception was thrown for the #packages 41 and quantity 42.");
	});

	QUnit.test("Given the value and quantity is equal to value, then no exception is thrown", function (assert) {
		numberOfPackagesValidator._validateValueWithContext(42, this.context);
		assert.ok(true, "No exception was thrown for the #packages 42 and quantity 42.");
	});

	QUnit.test("Given the value and quantity smaller than value, then ValidateException is thrown", function (assert) {
		numberOfPackagesValidator._validateValueWithContext(42, this.context);

		assert.throws( ()=>{numberOfPackagesValidator._validateValueWithContext(43, this.context);}
			, ValidateException, `ValidateException was not thrown for value 43 and quantity 42.`);
	});

});

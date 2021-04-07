/*global QUnit*/

sap.ui.define([
	"sap/ui/s4hana/extends4/print/model/formatter",
	'sap/base/Log',
	"sap/ui/thirdparty/sinon",
	"sap/ui/thirdparty/sinon-qunit"
], function(formatter, Log) {
	"use strict";

	QUnit.module("formatDeliveryDateState", {
		before: function() {
			var fakeNowAsFebruary28 = new Date('2019-02-28T11:33:58');
			this.fakeTimer = sinon.useFakeTimers(fakeNowAsFebruary28.getTime());

			this.fakeToday = fakeNowAsFebruary28;
		},

		after: function() {
			this.fakeTimer.restore();
		},

		beforeEach(){
			var now = new Date();
			if (Math.abs(this.fakeToday - now) > 100 ){
				console.error("FAKE TIMER DOES NOT WORK. RE-RUN THE TESTS");
				console.error(`now: ${now} --- fakeToday: ${this.fakeToday}`);
			}
		}
	});

	QUnit.test("Given there is more than 3 days to the delivery date, Then the status is Success", function (assert) {
		var testDateNextMonth = new Date('2019-04-13T11:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateNextMonth),
			sap.ui.core.ValueState.Success,
			"Next month did not return Success");

		var testDateNextYear = new Date('2020-01-01T11:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateNextYear),
			sap.ui.core.ValueState.Success,
			"Next year did not return Success");

		var testDateMarch3Afteroon = new Date('2019-03-03T15:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateMarch3Afteroon),
			sap.ui.core.ValueState.Success,
			"Next year did not return Success");
	});

	QUnit.test("Given there is less than 3 days to the delivery date, Then the status is Warning", function (assert) {
		console.log(`IF NOW DOES NOT PRINT FEB 28, THEN SINON HAD A PROBLEM TO USE FAKETIMER! now: ${Date()}`);

		var testDateMarchOne = new Date('2019-03-01T11:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateMarchOne),
			sap.ui.core.ValueState.Warning,
			"Yesterday did not return Warning");


		var testDateMarch3BeforeNoon = new Date('2019-03-03T10:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateMarch3BeforeNoon),
			sap.ui.core.ValueState.Warning,
			"Three days before afternoon did not return Warning");
	});

	QUnit.test("Given the delivery date is in past, Then the status is Error", function (assert) {
		console.log(`IF NOW DOES NOT PRINT FEB 28, THEN SINON HAD A PROBLEM TO USE FAKETIMER! now: ${Date()}`);

		var todayEarlyMorning = new Date('2019-02-28T01:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(todayEarlyMorning),
			sap.ui.core.ValueState.Error,
			"Today early morning did not return Error");

		var testDateYesterday = new Date('2019-02-27T11:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateYesterday),
			sap.ui.core.ValueState.Error,
			"Yesterday did not return Error");

		var testDateLastYear = new Date('2018-01-01T10:33:58');
		assert.strictEqual(formatter.formatDeliveryDateState(testDateLastYear),
			sap.ui.core.ValueState.Error,
			"Last year did not return Error");
	});

});

/*global QUnit*/

sap.ui.define([
	"sap/ui/s4hana/extends4/print/controller/Home.controller"
], function(oController) {
	"use strict";

	QUnit.module("Home Controller");

	QUnit.test("I should test the controller", function (assert) {
		var oAppController = new oController();

		oAppController.onInit();
		assert.ok(oAppController);
	});

});

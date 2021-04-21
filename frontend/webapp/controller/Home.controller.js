sap.ui.define([
	"./BaseController",
	"../model/formatter"
], function(BaseController, formatter) {
	"use strict";

	return BaseController.extend("sap.ui.s4hana.extends4.print.controller.Home", {

		formatter: formatter,

		onInit: function () {

		},

		onNavigationPress: function (event) {
			var bindingPath = event.getSource().getBindingContext().getPath();
			var deliveryId = bindingPath.match(/\d+/)[0];

			this.getRouter().navTo("printLabel", {
				deliveryId : deliveryId
			});
		}
	});
});
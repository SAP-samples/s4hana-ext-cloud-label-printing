// to provide context aware validator for the NumberOfPackages field
sap.ui.define([
	"sap/base/Log",
	"sap/ui/model/ValidateException",
	"sap/ui/model/ParseException"
], function (log, ValidateException, ParseException) {
	"use strict";
	return {

		/**
		 * Validates the value to be a positive integer, and wrt context (e.g., #packages cannot be more than the #quantity)
		 *
		 * @public
		 * Context aware validation
		 * @throws {sap.ui.model.ValidateException}
		 * @throws {sap.ui.model.ParseException}
		 * @returns {undefined}
		 */
		validateValue: function (value, bindingContext) {
			value = this._parseValue(value);
			this._validateValueWithContext(value, bindingContext);
		},

		_parseValue: function (value) {
			if (!value)
				return "";
			value = value.trim();
			if (value === "")
				return "";

			var rexOnlyDigits = /^[0-9]*$/;
			if (!rexOnlyDigits.test(value)) {
				throw new ParseException("Please provide only positive integer values without any character such as '+', '-', '.' or ','");
			}

			return Number.parseInt(value);
		},

		_validateValueWithContext: function (value, context) {
			if (!value){
				return;
			}

			if (!context){
				log.error("Context was null! This should have not happened.");
				return;
			}

			var quantity = context.getObject().ActualDeliveryQuantity;

			if(value > quantity)
				throw new ValidateException("Number of packages cannot exceed the Delivery Quantity");
		},


	};
});
sap.ui.define([], function () {
	"use strict";
	return {

		formatOverallStatusState: function (status) {
			if (!status)
				return sap.ui.core.ValueState.None;

			if (status === "A")
				return sap.ui.core.ValueState.Error;
			if (status === "B")
				return sap.ui.core.ValueState.Warning;
			if (status === "C")
				return sap.ui.core.ValueState.None;

			return sap.ui.core.ValueState.None;
		},

		formatOverallStatus: function (status) {
			if (status === "A")
				return "Open";
			if (status === "B")
				return "In Process";
			if (status === "C")
				return "Completed";

			return status;
		},

		formatDeliveryDateState: function (date) {
			var now = new Date();

			// past, error
			if (date - now < 0)
				return sap.ui.core.ValueState.Error;

			// less than a three days, warning
			var threeDaysInMs = 259200000;
			if (date - now < threeDaysInMs)
				return sap.ui.core.ValueState.Warning;

			// o.w. normal
			return sap.ui.core.ValueState.Success;
		}

	};
});
sap.ui.define("PrintService", [
	"sap/ui/base/Object",
	"sap/ui/s4hana/extends4/print/print/RenderService",
	"sap/ui/s4hana/extends4/print/print/Helpers"
], function (Object, RenderService, helpers) {
	"use strict";

	return Object.extend("sap.ui.s4hana.extends4.print.print.PrintService", {

		constructor: function (renderService) {
			sap.ui.base.Object.prototype.constructor.apply(this, arguments);

			if (renderService)
				this._renderer = renderService;
			else
				this._renderer = new RenderService();
		},

		print: function (printRequestPayload) {
			let printTaskPayload = printRequestPayload.printTask;
			let documentName = "OutboundDelivery_" + printRequestPayload.renderRequest.printData.form1.LabelForm[0].DeliveryId + ".pdf"
			printTaskPayload.printContents = {"documentName": documentName};

			if (this._renderer.hasPdfForPayload(printRequestPayload.renderRequest))
				return this._printExistingPdf(printRequestPayload);
			else
				return this._directPrint(printRequestPayload);
		},

		_printExistingPdf: async function (printRequestPayload) {
			let token = await helpers.getCSRFToken();
			let pdf = await this._renderer.getPdfForPayload(printRequestPayload.renderRequest);
			let printTaskPayload = printRequestPayload.printTask;

			const fd = new FormData();
			fd.append('file', pdf, /*filename*/ "Labels.pdf");
			fd.append('printTask', new Blob([JSON.stringify(printTaskPayload)], {type: "application/json"}));

			return new Promise((resolve, reject) => {
				$.ajax({
					url: "/backend/api/v1/PrintQueues/Multipart",
					method: "POST",
					cache: false,
					contentType: false,
					processData: false,
					data: fd,
					headers: {
						"X-CSRF-Token": token
					},
					success: resolve,
					error: reject
				});
			});
		},

		_directPrint: async function (printRequestPayload) {
			let token = await helpers.getCSRFToken();
			return new Promise((resolve, reject) => {
				$.ajax({
					url: "/backend/api/v1/PrintQueues",
					method: "POST",
					contentType: "application/json",
					data: JSON.stringify(printRequestPayload),
					headers: {
						"X-CSRF-Token": token
					},
					success: resolve,
					error: reject
				});
			});
		}

	});

});

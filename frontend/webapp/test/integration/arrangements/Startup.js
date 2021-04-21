sap.ui.define([
	"sap/ui/test/Opa5",
	"sap/ui/s4hana/extends4/print/localService/mockserver",
	"sap/ui/model/odata/v2/ODataModel"
], function(Opa5, mockserver, ODataModel) {
	"use strict";

	function getFrameUrl(sHashParameter, sUrlParameters) {
		var sUrl = jQuery.sap.getResourcePath("sap/ui/s4hana/extends4/print/index", ".html");
		var sHash = sHashParameter || "";
		var sUrlParams = sUrlParameters ? "&" + sUrlParameters : "";

		// return sUrl + "?" + sUrlParameters;
		return sUrl + "?sap-language=en" + sUrlParams + sHash;
	}

	return Opa5.extend("sap.ui.s4hana.extends4.print.test.integration.arrangements.Startup", {

		iStartTheAppWithBackendErrors: function() {
			this.iStartMyAppInAFrame(getFrameUrl(undefined, "metadataError=true"));
		},
		
		iStartMyApp: function (oOptions) {
			oOptions = oOptions || {};

			this._clearSharedData();

			// start the app with a minimal delay to make tests fast but still async to discover basic timing issues
			oOptions.delay = oOptions.delay || 50;

			// configure mock server with the current options
			var serversInitializedPromises = mockserver.initAll(oOptions);
			this.iWaitForPromise(Promise.all(serversInitializedPromises));

			// start the app UI component
			this.iStartMyUIComponent({
				componentConfig: {
					name: "sap.ui.s4hana.extends4.print",
					manifest: true
				},
				hash: oOptions.hash,
				autoWait: oOptions.autoWait
			});
		},

		_clearSharedData: function () {
			// clear shared metadata in ODataModel to allow tests for loading the metadata
			ODataModel.mSharedData = { server: {}, service: {}, meta: {} };
		}
	});
});

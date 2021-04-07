sap.ui.define([
	"sap/ui/core/UIComponent",
	"sap/ui/Device",
	"sap/ui/fl/FakeLrepConnectorLocalStorage",
	"./model/models",
	"./controller/ErrorHandlerSingleton"
], function (UIComponent, Device, FakeLrepConnectorLocalStorage, models, ErrorHandlerSingleton) {
	"use strict";

	return UIComponent.extend("sap.ui.s4hana.extends4.print.Component", {

		metadata: {
			manifest: "json"
		},

		/**
		 * The component is initialized by UI5 automatically during the startup of the app and calls the init method once.
		 * @public
		 * @override
		 */
		init: function () {
			FakeLrepConnectorLocalStorage.enableFakeConnector(jQuery.sap.getModulePath("sap.extensibilityexplorer.authoringtool.lrep.component-test-changes") + ".json");

			// call the base component's init function
			UIComponent.prototype.init.apply(this, arguments);

			// initialize the error handler with the component
			this._errorHandler = ErrorHandlerSingleton.getInstance(this);
			this._errorHandler.addModel(this.getModel());
			this._errorHandler.addModel(this.getModel("templateModel"));
			this._errorHandler.addModel(this.getModel("queueModel"));

			// set the device model
			this.setModel(models.createDeviceModel(), "device");

			// create the views based on the url/hash
			this.getRouter().initialize();

			this._convertTemplateModel();
		},

		destroy: function () {
			FakeLrepConnectorLocalStorage.disableFakeConnector();
			this._errorHandler.destroy();			
			UIComponent.prototype.destroy.apply(this, arguments);
		},

		_convertTemplateModel: function () {
			this.getModel("templateModel").dataLoaded().then( function () {
				const hasTemplateProperty = form => !!form.templates;
				// will be called once. Assuming that template model is not going to change during session.
				// If it changes, then this fnc has to be attached to the request completed event of the model.
				const data = this.getModel("templateModel").getData();
				const templatePaths = data
					.filter(hasTemplateProperty)
					.flatMap(form => form.templates.map(template => ({path: form.formName + "/" + template.templateName})))
				this.getModel("templateModel").setData(templatePaths);
			}.bind(this));
		}
	});
});
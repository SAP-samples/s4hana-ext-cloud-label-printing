sap.ui.define([
	"sap/ui/base/Object",
	"sap/m/MessageBox",
	"sap/base/assert"
], function (UI5Object, MessageBox, assert) {
	"use strict";

	let isODataModel = model => model.getMetadata().getName().search('v2.ODataModel') !== -1;
	let isJSONModel = model => model.getMetadata().getName().search('JSONModel') !== -1;

	let instance;
	let ErrorHandler = UI5Object.extend("sap.ui.s4hana.extends4.print.controller.ErrorHandler", {
		_resourceBundle: null,
		_component: null,
		_technicalErrorMessageIsOpen: false,

		/**
		 * Handles application errors by automatically attaching to the model events and displaying errors when needed.
		 * @class
		 * @param {sap.ui.core.UIComponent} component reference to the app's component
		 * @public
		 */
		constructor: function (component) {
			UI5Object.prototype.constructor.apply(this, arguments);

			this._resourceBundle = component.getModel("i18n").getResourceBundle();
			this._component = component;
		},

		destroy: function (component) {
			instance = null;
			
			UI5Object.prototype.destroy.apply(this, arguments);
		},

		addModel: function (model) {
			if (isODataModel(model)) return this._addODataModel(model);
			else if (isJSONModel(model)) return this._addJSONModel(model);

			throw Error("Provided argument is neither an OData V2 model nor JSON model");
		},

		onError: function (event) {
			let errorMessage = this._parseErrorMessageFromParameters(event.getParameters());
			this._showServiceError(errorMessage);
		},

		onXhrError: function (xhr) {
			let errorMessage = this._parseErrorMessageFromXhr(xhr);
			this._showServiceError(errorMessage);
		},

		/**
		 * Attaches to the {@link sap.ui.model.odata.v2.ODataModel} to detect the errors.
		 * Only metadata failure is covered. Not found case is covered in the controller.
		 * This project does not have POST request, so that case is not implemented as well.
		 * @private
		 */
		_addODataModel: function (model) {
			model.attachMetadataFailed(this.onError.bind(this));
		},

		_addJSONModel: function (model) {
			model.attachRequestFailed(this.onError.bind(this));
		},

		/**
		 * Shows a {@link sap.m.MessageBox} when a service call has failed.
		 * Only the first error message will be display.
		 * @param {string} message to be displayed on request
		 * @private
		 */
		_showServiceError: function (message) {
			if (this._technicalErrorMessageIsOpen)
				return;
			this._technicalErrorMessageIsOpen = true;
			MessageBox.error(
				this._resourceBundle.getText("genericErrorText"),
				{
					details: message,
					actions: [MessageBox.Action.CLOSE],
					onClose: () => {
						this._technicalErrorMessageIsOpen = false;
						this._component.getRouter().getTargets().display("technicalError");
					}
				}
			);
		},

		_parseErrorMessageFromXhr: function (xhr) {
			return this._parseErrorMessageFromParameters(xhr);
		},

		/**
		 * Prepares an object to show in the {@link sap.m.MessageBox}.
		 * @param {object} eventParameters a technical error object received from backend
		 * @private
		 * @returns {string} formatted text to show in {@link sap.m.MessageBox} as details.
		 */
		_parseErrorMessageFromParameters: function (eventParameters) {
			let formatIfJSONString = str => {
				try {
					return JSON.stringify(JSON.parse(str), null, 4);
				} catch (e) { }
				return str;
			};
			let sErrorMessageParsed = "<p>Backend responded with the following information:<br>";

			if (!eventParameters.responseText) {
				sErrorMessageParsed +=
					"<pre>" + JSON.stringify(eventParameters, null, 4) + "</pre>" + "</p>";
			} else {
				var formattedResponse = eventParameters.responseText.replace(/\\n/g, "");
				formattedResponse = formattedResponse.replace(/\\r/g, "");
				formattedResponse = formattedResponse.replace(/\\"/g, "\"");
				formattedResponse = formattedResponse.replace(/"{/g, "{");
				formattedResponse = formattedResponse.replace(/}"/g, "}");
				sErrorMessageParsed += "<pre>" + formatIfJSONString(formattedResponse) + "</pre>";
			}

			sErrorMessageParsed +=
				"<p><strong>This can happen if:</strong></p>" +
				"<ul>" +
				"<li>Print Service configuration is wrong</li>" +
				"<li>Backend system is down</li>" +
				"<li>Wrong S/4 destination configuration</li>" +
				"<li>Missing/wrong communication arrangement</li>" +
				"<li>Technical user is locked due to several log on trial with wrong credentials<br>" +
				"You can simply check this by using <em>Display Technical Users</em> app in your S/4HANA.<br>" +
				"If this is the case, you can simply unclock your user in the app as well.</li>" +
				"<li>a backend component is not <em>available</em></li>" +
				"<li>You are not connected to the internet</li>" +
				"</ul>" +
				"<p>Get more help <a href='https://help.sap.com/viewer/index' target='_top'>here</a>.";

			return sErrorMessageParsed;
		}
	});

	function createInstance(component) {
		if (!component) 
			assert("Component must be provided to create ErrorHandler instance.");
		return new ErrorHandler(component);
	}

	return {
		getInstance: function (component) {
			if (!instance) 
				instance = createInstance(component);
			
			return instance;
		}
	};
});
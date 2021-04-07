sap.ui.define("RenderService", [
	"sap/ui/base/Object",
	"sap/ui/s4hana/extends4/print/print/Helpers"
], function (ui5Object, helpers) {
	"use strict";

	const isObject = objectCandidate => typeof objectCandidate === 'object' && objectCandidate !== null;
	const deepCompare = (obj1,obj2)=> {
		return isObject(obj1) && isObject(obj2) &&
			Object.keys(obj1).length === Object.keys(obj2).length &&
			Object.keys(obj1).every( key => obj2.hasOwnProperty(key)) &&
			Object.keys(obj1).filter( key => !isObject(obj1[key])).every(key => obj1[key] === obj2[key]) &&
			Object.keys(obj1).filter( key => isObject(obj1[key])).every(key => deepCompare(obj1[key], obj2[key]));
	};

	return ui5Object.extend("sap.ui.s4hana.extends4.print.print.RenderService", {
		_pdfDocument: null,
		_renderRequest: null,

		constructor: function () {
			sap.ui.base.Object.prototype.constructor.apply(this, arguments);
		},

		hasPdfForPayload: function (renderRequest) {
			return deepCompare(renderRequest, this._renderRequest);
		},

		/**
		 * Does the actual request to backend.
		 * @private
		 * @param {object} renderRequest the request object
		 * @throws {Error} XMLHttpRequest error occurred during csrf token request.
		 * @returns {Promise<Binary|Error>} A promise to the pdf document | XMLHttpRequest error occurred during rendering
		 */
		_renderPdf: async function (renderRequest) {
			let token = await helpers.getCSRFToken();
			return new Promise((resolve, reject) => {
				let xhr = new XMLHttpRequest();
				xhr.open("POST", "/backend/api/v1/pdf/render");
				xhr.responseType = 'blob';
				xhr.setRequestHeader('Content-type', 'application/json');
				xhr.setRequestHeader('X-CSRF-Token', token);

				xhr.onreadystatechange = function () {
					if (xhr.readyState === 2) {
						if (xhr.status === 200) {
							xhr.responseType = "blob";
						} else {
							xhr.responseType = "text";
						}
					}
				};

				xhr.onload = () => {
					if (xhr.status === 200) {
						// at this point renderRequest is pure JSON object. So copying like that is still dirty, but a working solution.
						this._renderRequest = JSON.parse(JSON.stringify(renderRequest));
						this._pdfDocument = xhr.response;
						return resolve(xhr.response);
					}
					reject(xhr);
				};

				xhr.send(JSON.stringify(renderRequest));
			});
		},

		/**
		 * Convenient method to get the pdf for the given payload.
		 * It compares the given payload with the stored one, and returns the existing pdf if payloads match.
		 * Otherwise it makes the request to the backend.
		 * @public
		 * @param {object} renderRequest the request object
		 * @throws {Error} XMLHttpRequest error. Can be caused by inner functions and will not be caught here.
		 * @returns {Promise<Blob>} A promise to the pdf document
		 */
		getPdfForPayload: function (renderRequest) {
			if (deepCompare(renderRequest, this._renderRequest)){
				return Promise.resolve(this._pdfDocument);
			}
			return this._renderPdf(renderRequest);
		}

	});

});

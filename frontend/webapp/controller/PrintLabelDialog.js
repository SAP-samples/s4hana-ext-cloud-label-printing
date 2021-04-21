sap.ui.define("PrintLabelDialog", [
	"sap/ui/base/EventProvider",
	"sap/ui/core/Fragment",
	'sap/ui/model/json/JSONModel',
	"sap/m/PDFViewer",
	"sap/base/security/URLWhitelist",
	"sap/ui/s4hana/extends4/print/print/RenderService",
	"sap/ui/s4hana/extends4/print/print/PrintService",
	"sap/ui/s4hana/extends4/print/controller/ErrorHandlerSingleton"
], function (EventProvider, Fragment, JSONModel, PDFViewer, URLWhitelist, RenderService, PrintService, ErrorHandlerSingleton) {
	"use strict";

	return EventProvider.extend("sap.ui.s4hana.extends4.print.controller.PrintLabelDialog", {
		_sFragmentName: "sap.ui.s4hana.extends4.print.view.PrintLabelDialog",
		_fragmentId: "idPrintDialogFragment",

		_printService: null,
		_renderService: null,

		constructor: function () {
			sap.ui.base.EventProvider.prototype.constructor.apply(this, arguments);
			this._onFragmentCreated = this._createFragment();
			this._renderService = new RenderService();
			this._printService = new PrintService(this._renderService);
		},

		onFragmentCreated: function () {
			return this._onFragmentCreated;
		},

		getFragment: function () {
			if (!this._oFragment) {
				throw Error("Fragment is not yet rendered! onFragmentCreated returns a promise, have you waited it to resolve?")
			}
			return this._oFragment;
		},

		open: function () {
			this.getFragment().open();
		},

		_restoreDelegatesInitialState: function () {
			this._initModels(); // calling initialization on Open will restore the models as well.
			this._restoreComboBoxInitialState();
		},

		closeDialog: function () {
			this.getFragment().close();
		},

		onBeforeClose: function () {
			this._restoreDelegatesInitialState();
		},

		_restoreComboBoxInitialState: function () {
			["idComboBoxQueue", "idComboBoxTemplate"].forEach(id => {
				this.byId(id).setValueState("None");
				this.byId(id).setValue(null);
			});
		},

		_initModels: function () {
			this._initPrintModel();
			this._initViewModel();
		},

		_createFragment: function () {
			var fragmentLoadPromise = Fragment.load({
				type: "XML",
				name: this._sFragmentName,
				controller: this,
				id: this._fragmentId
			});

			fragmentLoadPromise.then(fragment => {
				this._oFragment = fragment;
				// Register other looks like onBeforeRendering, etc.
				this._oFragment.addEventDelegate(this, this);
				this._initModels();
			});

			return fragmentLoadPromise;
		},

		_initPrintModel: function () {
			var initialPrintRequestPayload = {
				renderRequest: {
					templatePath: "",
					printData: {
						form1: {
							LabelForm: []
						}
					}
				},
				printTask: {
					qname: "",
					numberOfCopies: 1,
					username: "cpm"
				}
			};

			this._printPayloadModel = new JSONModel(initialPrintRequestPayload);
			this.setModel(this._printPayloadModel, "printPayloadModel");
		},


		_initViewModel: function () {
			this._viewModel = new JSONModel({
				busy: false,
			});

			this.setModel(this._viewModel, "viewModel");
		},

		onQueueSelect: function (event) {
			if (this._validateNewValueOfEvent(event, "Please provide a valid queue."))
				this._printPayloadModel.setProperty("/printTask/qname", event.getParameter("newValue"));
			else
				this._printPayloadModel.setProperty("/printTask/qname", "");
		},

		onTemplateSelect: function (event) {
			if (this._validateNewValueOfEvent(event, "Please provide a valid template."))
				this._printPayloadModel.setProperty("/renderRequest/templatePath", event.getParameter("newValue"));
			else
				this._printPayloadModel.setProperty("/renderRequest/templatePath", "");
		},

		_validateNewValueOfEvent: function (event, errorMsg) {
			var validatedComboBox = event.getSource(),
				selectedKey = validatedComboBox.getSelectedKey(),
				value = validatedComboBox.getValue();

			if (!selectedKey && value) {
				validatedComboBox.setValueState("Error");
				validatedComboBox.setValueStateText(errorMsg);
				return false;
			} else {
				validatedComboBox.setValueState("None");
				return true;
			}
		},

		getModel: function (modelName) {
			return this.getFragment().getModel(modelName);
		},

		setModel: function (model, modelName) {
			return this.getFragment().setModel(model, modelName);
		},

		byId: function (id) {
			return sap.ui.core.Fragment.byId(this._fragmentId, id);
		},

		_openPdfViewerForSource: function (pdfDocumentURL) {
			if (!this._pdfViewer) {
				this._pdfViewer = new PDFViewer();
				this.getFragment().addDependent(this._pdfViewer);
				this._pdfViewer.attachError(event => ErrorHandlerSingleton.getInstance().onError(event));
				// to show the pdf in this viewer, need to whitelist the blob protocol
				URLWhitelist.add("blob");
			}
			this._pdfViewer.setSource(pdfDocumentURL);
			this._pdfViewer.open();
		},

		onPreview: function () {
			this._viewModel.setProperty("/busy", true);
			let printRequestPayload = this._preparePayloadForRenderRequest();

			this._renderService.getPdfForPayload(printRequestPayload.renderRequest)
			.then(pdfDocument => {
				var pdfBlob = pdfDocument.slice(0, pdfDocument.size, "application/pdf");
				var pdfDocumentURL = URL.createObjectURL(pdfBlob);

				this._openPdfViewerForSource(pdfDocumentURL);
			})
			.catch(e => ErrorHandlerSingleton.getInstance().onXhrError(e))
			.finally( () => this._viewModel.setProperty("/busy", false));
		},

		_preparePayloadForRenderRequest: function () {
			var isForPrint = item => item.NumberOfPackages.trim() !== "" && item.NumberOfPackages !== "0";
			var prepareLabelPayloadForItem = item => {
				var packageQuantities = this._calculateQuantities(item);
				var labels = [];
				for (var idxPackage = 0; idxPackage < Number.parseInt(item.NumberOfPackages); idxPackage++) {
					labels.push({
						DeliveryId: item.DeliveryDocument,
						Position: Number.parseInt(item.DeliveryDocumentItem),
						MaterialNo: item.Material,
						Quantity: packageQuantities[idxPackage],
						Package: `${idxPackage + 1} from ${item.NumberOfPackages}`
					});
				}
				return labels;
			};

			var preparedLabelForms = this.getModel("itemsModel").getProperty("/")
				.filter(isForPrint)
				.reduce( (labelForms, item) =>{
					return labelForms.concat(...prepareLabelPayloadForItem(item));
				}, []);

			console.log(preparedLabelForms);
			this._printPayloadModel.setProperty("/renderRequest/printData/form1/LabelForm", preparedLabelForms);
			return this._printPayloadModel.getData();
		},

		onPrint: function () {
			let successMessage = this.getModel("i18n").getProperty("xtxt.PrintSuccessMessage")
			this._viewModel.setProperty("/busy", true);

			this._preparePayloadForRenderRequest(); // prepares the data and fills the _printPayloadModel
			this._printService.print(this._printPayloadModel.getData())
			.then( () => sap.m.MessageToast.show(successMessage))
			.catch(e => ErrorHandlerSingleton.getInstance().onXhrError(e))
			.finally( ()=> {
				this._viewModel.setProperty("/busy", false);
				this.closeDialog();
			});
		},

		_calculateQuantities: function (item) {
			var minQuantityPerPackage = Math.floor(Number.parseInt(item.ActualDeliveryQuantity) / Number.parseInt(item.NumberOfPackages));
			var numberOfPackagesThatShouldHaveExtraItem =
				Number.parseInt(item.ActualDeliveryQuantity) % Number.parseInt(item.NumberOfPackages);

			return new Array(Number.parseInt(item.NumberOfPackages))
				.fill(minQuantityPerPackage + 1, 0, numberOfPackagesThatShouldHaveExtraItem)
				.fill(minQuantityPerPackage, numberOfPackagesThatShouldHaveExtraItem);
		},

	});

});

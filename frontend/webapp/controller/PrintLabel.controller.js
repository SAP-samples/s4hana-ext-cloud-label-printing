sap.ui.define([
	"./BaseController",
	'sap/ui/model/json/JSONModel',
	"sap/ui/core/routing/History",
	"../model/formatter",
	"../model/numberOfPackagesValidator",
	"./PrintLabelDialog"
], function(BaseController, JSONModel, History, formatter, numberOfPackagesValidator, PrintLabelDialog) {
	"use strict";

	return BaseController.extend("sap.ui.s4hana.extends4.print.controller.PrintLabel", {

		formatter: formatter,

		onInit: function () {
			this._dataModel = this.getOwnerComponent().getModel();
			this._view = this.getView();

			this.getRouter().getRoute("printLabel").attachPatternMatched(this._onPatternMatched, this);
		},

		onNavBack : function() {
			var sPreviousHash = History.getInstance().getPreviousHash();

			if (sPreviousHash !== undefined) {
				// If there is a history entry we go one step back in the browser history
				history.go(-1);
			} else {
				// If not, it will replace the current entry of the browser history with the home route.
				this.getRouter().navTo("home", {}, true);
			}
		},

		_initViewModel: function () {
			this._viewModel = new JSONModel({
				busy: true,
				isOverallValueStateSuccess: true,
				atLeastOneInputHasNonEmptyValue: false,
			});

			this.setModel(this._viewModel, "printLabelView");
		},

		_onPatternMatched: function (event) {
			this._initViewModel();

			var deliveryId = event.getParameter("arguments").deliveryId;
			this._dataModel.metadataLoaded().then( function() {
				this._objectPath = "/" + this._dataModel.createKey("A_OutbDeliveryHeader", {
					DeliveryDocument :  deliveryId
				});
				this._bindView();
			}.bind(this));
		},

		_bindView : function () {
			this.getView().bindElement({
				path: this._objectPath,
				parameters: {
					expand: "to_DeliveryDocumentItem"
				},
				events: {
					change: this._onBindingChange.bind(this),
					dataRequested: function () {
						this._dataModel.metadataLoaded().then(function () {
							// Busy indicator on view should only be set if metadata is loaded,
							// otherwise there may be two busy indications next to each other on the
							// screen. This happens because route matched handler already calls '_bindView'
							// while metadata is loaded.
							this._viewModel.setProperty("/busy", true);
						}.bind(this));
					}.bind(this),
					dataReceived: function () {
						this._viewModel.setProperty("/busy", false);
					}.bind(this)
				}
			});
		},

		_onBindingChange: function () {
			if (!this._view.getElementBinding().getBoundContext()) {
				this.getRouter().getTargets().display("notFound");
				return;
			}

			var deliveryId = this._view.getBindingContext().getObject().DeliveryDocument;
			this._updateHeaderTitle(deliveryId);

			var itemsModel = this._createItemsModel();
			this.setModel(itemsModel, "itemsModel");

			this._viewModel.setProperty("/busy", false);
		},

		_updateHeaderTitle: function (deliveryId) {
			this._viewModel.setProperty("/headerTitle",
				this.getResourceBundle().getText("xtit.PrintLabelObjectHeader", [deliveryId]));
		},

		onTableUpdateFinish: function (event) {
			var numberOfItemsInTable = event.getParameter("actual");
			this._viewModel.setProperty("/tableTitle",
				this.getResourceBundle().getText("xtit.ItemTable", [numberOfItemsInTable]));

			this._inputComponents = this._getAllInputComponentsRenderedInTable();
		},

		_getAllInputComponentsRenderedInTable: function () {
			var inputComponents = [];
			var table = this.byId("id_itemsTable");
			table.getAggregation("items").forEach( columnListItem => {
				var componentCandidate = columnListItem.getAggregation("cells").find( cell => {
					return cell.getMetadata().getElementName() === "sap.m.Input";
				});
				if (typeof componentCandidate !== "undefined")
					inputComponents.push(componentCandidate)
			});

			return inputComponents;
		},

		_createItemsModel: function () {
			var bindingObject = this._view.getBindingContext().getObject();

			var itemsData = [],
				that = this;
			bindingObject.to_DeliveryDocumentItem.__list.forEach(itemPath => {
				var item = that._dataModel.getProperty("/" + itemPath);
				item.NumberOfPackages = "";
				itemsData.push(item);
			});

			return new JSONModel(itemsData);
		},

		_handleValidationForInputEvent: function(event){
			var inputComponent = event.getSource();
			var bindingContext = inputComponent.getBindingContext("itemsModel");
			var value = event.getParameter("newValue");

			try {
				numberOfPackagesValidator.validateValue(value, bindingContext);
			} catch (e) {
				inputComponent.setValueState(sap.ui.core.ValueState.Error);
				inputComponent.setValueStateText(e.message);
				return; // no further action required.
			}

			inputComponent.setValueState(sap.ui.core.ValueState.None);
			inputComponent.setValueStateText(null);
		},

		onQuantityInput: function (event) {
			this._handleValidationForInputEvent(event);

			this._updateViewModelAfterInputEvent();
		},

		_updateViewModelAfterInputEvent: function () {
			var isOverallValueStateError = Boolean(this._inputComponents.find( inputComponent  => {
				return inputComponent.getValueState() === sap.ui.core.ValueState.Error;
			}));

			var atLeastOneInputHasNonEmptyValue = Boolean(this._inputComponents.find( inputComponent  => {
				return !(inputComponent.getValue().trim() === "" || inputComponent.getValue().trim() === "0");
			}));

			this._viewModel.setProperty("/isOverallValueStateSuccess", !isOverallValueStateError);
			this._viewModel.setProperty("/atLeastOneInputHasNonEmptyValue", atLeastOneInputHasNonEmptyValue);
		},

		onPrintPressed: function (event) {
			if (!this._printDialog) {
				this._viewModel.setProperty("/busy", true);

				this._printDialog = new PrintLabelDialog();
				this._printDialog.onFragmentCreated().then( fragment => {
					this._viewModel.setProperty("/busy", false);
					this.getView().addDependent(fragment);
					this._printDialog.open();
				});
			} else
				this._printDialog.open();

		}

	});
});
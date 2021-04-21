sap.ui.define([
	"sap/ui/test/Opa5",
	"sap/ui/test/matchers/I18NText",
	"sap/ui/test/actions/Press",
	"sap/ui/test/matchers/Properties",
	"sap/ui/test/matchers/AggregationLengthEquals",
	"sap/ui/s4hana/extends4/print/test/integration/actions/PressEscKey",
	"sap/ui/thirdparty/sinon"
], function (Opa5, I18NText, Press, Properties, AggregationLengthEquals, PressEscKey, sinon) {
	"use strict";
	var sViewName = "PrintLabel";
	Opa5.createPageObjects({

		onThePrintLabelDialog: {
			actions: {
				_createChooseATemplateQueueItem: function(bindingTitle){
					return this.waitFor({
						controlType: "sap.m.ComboBox",
						searchOpenDialogs: true,
						matchers: new AggregationLengthEquals({
							length: 6,
							name: "items"
						}),
						actions: new Press(),
						success: function () {
							this.waitFor({
								controlType: "sap.m.StandardListItem",
								matchers: new Properties({ title: bindingTitle }),
								actions: new Press(),
								errorMessage: "Cannot select Template from Dropdown"
							});
						},
						errorMessage: "Could not find ComboBox"
					});
				},
				
				iChooseATemplateFromTheDropdown: function () {
					return this._createChooseATemplateQueueItem("form 1/template 2");
				},

				iChooseAnotherTemplateFromTheDropdown: function () {
					return this._createChooseATemplateQueueItem("form 2/template 1");
				},

				iChooseNonExistingTemplateFromTheDropdown: function () {
					return this._createChooseATemplateQueueItem("form 3/Not Existing Template");
				},

				iChooseAQueueFromTheDropdown: function () {
					return this.waitFor({
						controlType: "sap.m.ComboBox",
						searchOpenDialogs: true,
						matchers: new AggregationLengthEquals({
							length: 3,
							name: "items"
						}),
						actions: new Press(),
						success: function () {
							this.waitFor({
								controlType: "sap.m.StandardListItem",
								matchers: new Properties({ title: "queue 1" }),
								actions: new Press(),
								errorMessage: "Cannot select Queue from Dropdown"
							});
						},
						errorMessage: "Could not find Queue ComboBox"
					})
				},

				iPressOnDialogPrintButton: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Button",
						matchers: new I18NText({
							key: "xbtn.DialogPrintButtonText",
							propertyName: "text"
						}),
						actions: new Press(),
						errorMessage: "Cannot find the Dialog Print button"
					});
				},

				iPressOnDialogPreviewButton: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Button",
						matchers: new I18NText({
							key: "xbtn.DialogPreviewButtonText",
							propertyName: "text"
						}),
						actions: new Press(),
						errorMessage: "Cannot find the Dialog Preview button"
					});
				},

				iPressOnEscKey: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Bar",
						actions: new PressEscKey(),
						errorMessage: "Cannot find the Escape key"
					});
				},

				iSpyRenderPdf: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Dialog",
						success: function (dialog) {
							Opa5.getContext()._renderPdfSpy = sinon.spy(dialog[0].getParent().getController()._printDialog._renderService, "_renderPdf")
						},
						errorMessage: "Cannot spy pdf"
					});
				},

				iSpyPrintExistingPdf: function () {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Dialog",
						success: function (dialog) {
							Opa5.getContext()._printExistingPdfSpy = sinon.spy(dialog[0].getParent().getController()._printDialog._printService, "_printExistingPdf")
						},
						errorMessage: "Cannot spy printExisting pdf"
					});
				}

			},

			assertions: {
				iSeeADialog: function () {
					return this.waitFor({
						controlType: "sap.m.Title",
						searchOpenDialogs: true,
						matchers: new I18NText({
							key: "xtit.PrintDialog",
							propertyName: "text"
						}),
						success: function () {
							Opa5.assert.ok(true, "A dialog with Print Label Title is shown");
						},
						errorMessage: "No dialog with that title was open"
					});
				},

				iShouldSeeTemplateComboBoxWithElements: function () {
					return this.waitFor({
						controlType: "sap.m.ComboBox",
						searchOpenDialogs: true,
						matchers: new AggregationLengthEquals({
							length: 6,
							name: "items"
						}),
						success: function () {
							Opa5.assert.ok(true, "Combo box for template paths (6) was found");
						},
						errorMessage: "No Combo box for template paths was found"
					});
				},

				iShouldSeeQueueComboBoxWithElements: function () {
					return this.waitFor({
						controlType: "sap.m.ComboBox",
						searchOpenDialogs: true,
						matchers: new AggregationLengthEquals({
							length: 3,
							name: "items"
						}),
						success: function () {
							Opa5.assert.ok(true, "Combo box for queues (3) was found");
						},
						errorMessage: "No Combo box for queues was found"
					});
				},

				iShouldSeePrintButtonIsDisabled: function () {
					return this._createPrintButtonEnabledQueueItem(false);
				},

				iShouldSeePrintButtonIsEnabled: function () {
					return this._createPrintButtonEnabledQueueItem(true);
				},

				_createPrintButtonEnabledQueueItem: function (enabled) {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Button",
						searchOpenDialogs: true,
						enabled: false, /*This property is needed to search for disabled components as well.*/
						matchers: new I18NText({
							key: "xbtn.DialogPrintButtonText",
							propertyName: "text"
						}),
						success: function (buttons) {
							Opa5.assert.strictEqual(buttons.length, 1, "There is one print button");
							Opa5.assert.strictEqual(buttons[0].getEnabled(), enabled, `The print button is ${enabled ? 'enabled' : 'disabled'}`);
						},
						errorMessage: "Cannot find button with a i18n key 'xbtn.DialogPrintButtonText'"
					});
				},

				iShouldSeePreviewButtonIsDisabled: function () {
					return this._createPreviewButtonEnabledQueueItem(false);
				},

				iShouldSeePreviewButtonIsEnabled: function () {
					return this._createPreviewButtonEnabledQueueItem(true);
				},

				_createPreviewButtonEnabledQueueItem: function (enabled) {
					return this.waitFor({
						viewName: sViewName,
						controlType: "sap.m.Button",
						searchOpenDialogs: true,
						enabled: false, /*This property is needed to search for disabled components as well.*/
						matchers: new I18NText({
							key: "xbtn.DialogPreviewButtonText",
							propertyName: "text"
						}),
						success: function (buttons) {
							Opa5.assert.strictEqual(buttons.length, 1, "There is one preview button");
							Opa5.assert.strictEqual(buttons[0].getEnabled(), enabled, `The preview button is ${enabled ? 'enabled' : 'disabled'}`);
						},
						errorMessage: "Cannot find button with a i18n key 'xbtn.DialogPreviewButtonText'"
					});
				},

				iShouldSeeASuccessMessage: function () {
					return this.waitFor({
						autoWait: false,
						check: function () {
							return $(".sapMMessageToast").length > 0;
						},
						success: function (whatIsThis) {
							console.log(whatIsThis);
							Opa5.assert.ok(true, "The message toast was shown");
						},
						errorMessage: "The message toast did not show up"
					});
				},

				iShouldSeePdf: function () {
					return this.waitFor({
						controlType: "sap.m.PDFViewer",
						success: function () {
							Opa5.assert.ok(true, "PDFViewer is shown");
						},
						errorMessage: "Did not see the PDFViewer"
					});
				},

				iCloseThePdfViewer: function () {
					return this.waitFor({
						controlType: "sap.m.Button",
						matchers: new Properties({
							text: "Close" // matching to the PdfViewer's button. It does not use i18n property.
						}),
						actions: new Press(),
						errorMessage: "Did not see the Close Button"
					})
				},

				iShouldSeePrintExistingPdfIsCalled: function (expectedCalls) {
					return this.waitFor({
						success: function () {
							Opa5.assert.strictEqual(
								Opa5.getContext()._printExistingPdfSpy.callCount, 
								expectedCalls,
								`_printExistingPdf is called ${expectedCalls} times.`);
						},
						errorMessage: `_printExistingPdf is called ${Opa5.getContext()._printExistingPdfSpy.callCount} times but expected ${expectedCalls} times.`
					})
				},

				iShouldSeeRenderPdfIsCalled: function (expectedCalls) {
					return this.waitFor({
						success: function () {
							Opa5.assert.strictEqual(Opa5.getContext()._renderPdfSpy.callCount, expectedCalls, `_renderPdf is called ${expectedCalls} times.`);
						},
						errorMessage: `_renderPdf is called ${Opa5.getContext()._renderPdfSpy.callCount} times but expected ${expectedCalls} times.`
					})
				}
			}
		}
	});

});

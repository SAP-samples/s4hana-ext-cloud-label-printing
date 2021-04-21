sap.ui.define(["jquery.sap.global", "sap/ui/test/actions/Action", "sap/ui/test/Opa5"],
    function ($, Action, Opa5) {
        "use strict";

        return Action.extend("sap.ui.s4hana.extends4.print.test.integration.actions.PressEscKey", {
            metadata: {
            },
            executeOn: function (oControl) {
                // focus it
                var $ActionDomRef = $(oControl.getFocusDomRef()),
                    oActionDomRef = $ActionDomRef[0];

                $ActionDomRef.focus();

                if (!$ActionDomRef.is(":focus")) {
                    $.sap.log.warning("Control " + oControl + " could not be focused - maybe you are debugging?", this._sLogPrefix);
                }
                var oUtils = Opa5.getUtils();

                // Input change will fire here
                oUtils.triggerKeydown(oActionDomRef, $.sap.KeyCodes.ESCAPE);
                // Seachfield will fire here
                oUtils.triggerKeyup(oActionDomRef, $.sap.KeyCodes.ESCAPE);
                // To make extra sure - textarea only works with blur
                oUtils.triggerEvent("blur", oActionDomRef);
            }
        });

    }, true);
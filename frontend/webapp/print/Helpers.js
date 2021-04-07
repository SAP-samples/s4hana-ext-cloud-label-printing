sap.ui.define([], function () {
	"use strict";
	return {

		/**
		 * Convenient method to get the csrf token for/from the print sample backend.
		 * @public
		 * @throws {Error} XMLHttpRequest error occurred during csrf token request.
		 * @returns {Promise<String>} A promise to the token
		 */
		getCSRFToken: function () {
			return new Promise((resolve, reject) => {
				$.ajax({
					url: "/backend/api/v1/Store",
					method: "GET",
					headers: {
						"X-CSRF-Token": "Fetch"
					},
					success: function (result, xhr, data) {
						resolve(data.getResponseHeader("X-CSRF-Token"));
					},
					error: function (e) {
						console.error("Could not fetch CSRF token");
						reject(e);
					}
				});
			})
		}

	};
});
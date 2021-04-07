sap.ui.define([
	"sap/ui/core/util/MockServer",
	"sap/ui/model/json/JSONModel",
	"sap/base/util/UriParameters",
	"sap/base/Log"
], function (MockServer, JSONModel, UriParameters, Log) {
	"use strict";

	var _mockServerForMainModel;
	var _mockServerForTemplateModel;
	var _mockServerForQueueModel;
	var _sAppPath = "sap/ui/s4hana/extends4/print/";
	var _sJsonFilesPath = _sAppPath + "localService/mockdata";

	var oMockServerInterface = {

		initAll : function (oOptionsParameter){
			var promises = [];
			promises.push(this.initMainModel(oOptionsParameter));
			promises.push(this.initTemplateModel());
			promises.push(this.initQueueModel());
			return promises;
		},

		/**
		 * Initializes the mock server asynchronously.
		 * You can configure the delay with the URL parameter "serverDelay".
		 * The local mock data in this folder is returned instead of the real data for testing.
		 * @protected
		 * @param {object} [oOptionsParameter] initMainModel parameters for the mockserver
		 * @returns{Promise} a promise that is resolved when the mock server has been started
		 */
		initMainModel : function (oOptionsParameter) {
			var oOptions = oOptionsParameter || {};

			return new Promise(function(fnResolve, fnReject) {
				var sManifestUrl = sap.ui.require.toUrl(_sAppPath + "manifest.json"),
					oManifestModel = new JSONModel(sManifestUrl);

				oManifestModel.attachRequestCompleted(function ()  {
					var oUriParameters = new UriParameters(window.location.href),
						// parse manifest for local metatadata URI
						sJsonFilesUrl = sap.ui.require.toUrl(_sJsonFilesPath),
						oMainDataSource = oManifestModel.getProperty("/sap.app/dataSources/mainService"),
						sMetadataUrl = sap.ui.require.toUrl(_sAppPath + oMainDataSource.settings.localUri),
						// ensure there is a trailing slash
						sMockServerUrl = /.*\/$/.test(oMainDataSource.uri) ? oMainDataSource.uri : oMainDataSource.uri + "/";

					// create a mock server instance or stop the existing one to reinitialize
					if (!_mockServerForMainModel) {
						_mockServerForMainModel = new MockServer({
							rootUri: sMockServerUrl
						});
					} else {
						_mockServerForMainModel.stop();
					}

					// configure mock server with the given options or a default delay of 0.5s
					MockServer.config({
						autoRespond : true,
						autoRespondAfter : (oOptions.delay || oUriParameters.get("serverDelay") || 500)
					});

					// simulate all requests using mock data
					_mockServerForMainModel.simulate(sMetadataUrl, {
						sMockdataBaseUrl : sJsonFilesUrl,
						bGenerateMissingMockData : true
					});

					var aRequests = _mockServerForMainModel.getRequests();

					// compose an error response for requesti
					var fnResponse = function (iErrCode, sMessage, aRequest) {
						aRequest.response = function(oXhr){
							oXhr.respond(iErrCode, {"Content-Type": "application/json"}, sMessage);
						};
					};

					// simulate metadata errors
					if (oOptions.metadataError || oUriParameters.get("metadataError")) {
						aRequests.forEach(function (aEntry) {
							if (aEntry.path.toString().indexOf("$metadata") > -1) {
								fnResponse(500, JSON.stringify({"error":{"code":null,"message":{"lang":"en","value":"Unable to fetch the metadata : Failed to execute OData Metadata request."}}}), aEntry);
							}
						});
					}

					_mockServerForMainModel.setRequests(aRequests);
					_mockServerForMainModel.start();

					Log.info("Running the app with mock data");
					fnResolve();
				});

				oManifestModel.attachRequestFailed(function () {
					var sError = "Failed to load application manifest";

					Log.error(sError);
					fnReject(new Error(sError));
				});
			});
		},

		initQueueModel : function (){
			return new Promise( (resolve, reject) => {
				let requests =  [];
				let queuesReader = new JSONModel();

				queuesReader.loadData(sap.ui.require.toUrl(_sJsonFilesPath + "/queues.json"));
				queuesReader.dataLoaded()
					.then( () => {
						let oUriParameters = new UriParameters(window.location.href);
						if (oUriParameters.get("queueError") === 'notFound'){
							requests.push({
								method: "GET",
								path: new RegExp("(.*)/api/v1/PrintQueues"),
								response: function(oXhr) {
									oXhr.respondJSON(404, {}, {
										"error" : {
											"code" : "NotFound: status 404 reading PrintService#getQueues()",
											"message" : {
												"lang" : "en",
												"value" : "default backend - 404"
											}
										}
									});
									return true;
								}
							});
						}else if (oUriParameters.get("queueError") === 'unavailable'){
							requests.push({
								method: "GET",
								path: new RegExp("(.*)/api/v1/PrintQueues"),
								response: function(oXhr) {
									oXhr.respondJSON(503, {}, {
										"error" : {
											"code" : "ServiceUnavailable: status 503 reading PrintService#getQueues()",
											"message" : {
												"lang" : "en",
												"value" : "<html>\r\n<head><title>503 Service Temporarily Unavailable</title></head>\r\n<body>\r\n<center><h1>503 Service Temporarily Unavailable</h1></center>\r\n<hr><center>openresty/1.15.8.2</center>\r\n</body>\r\n</html>\r\n"
											}
										}
									});
									return true;
								}
							});
						} else {
							requests.push({
								method: "GET",
								path: new RegExp("(.*)/api/v1/PrintQueues"),
								response: function(oXhr) {
									oXhr.respondJSON(200, {}, queuesReader.getJSON());
									return true;
								}
							});
						}

						if (oUriParameters.get("queueError") === 'tooLarge'){
							requests.push({
								method: "POST",
								path: new RegExp("(.*)/api/v1/PrintQueues"),
								response: function(oXhr) {
									oXhr.respond(504, {}, "Gateway Timeout");
									return true;
								}
							});

							requests.push({
								method: "POST",
								path: new RegExp("(.*)/api/v1/PrintQueues/Multipart"),
								response: function(oXhr) {
									oXhr.respondJSON(413, {}, {
										"error" : {
											"code" : "FeignException: status 413 reading PrintService#print(String,PrintTask)",
											"message" : {
												"lang" : "en",
												"value" : "<html>\r\n<head><title>413 Request Entity Too Large</title></head>\r\n<body>\r\n<center><h1>413 Request Entity Too Large</h1></center>\r\n<hr><center>openresty/1.15.8.2</center>\r\n</body>\r\n</html>\r\n"
											}
										}
									});
									return true;
								}
							});
						}else{
							requests.push({
								method: "POST",
								path: new RegExp("(.*)/api/v1/PrintQueues"),
								response: function(oXhr) {
									// Some checks to ensure the payload is correct
									if (oXhr.requestBody.includes("Not Existing Template")) {
										oXhr.respondJSON(500, {}, {
											"error" : {
												"code" : "InternalServerError: status 500 reading AdsService#renderFormFromStorage(AdsRenderRequest)",
												"message" : {
													"lang" : "en",
													"value" : "{\"message\":\"Internal API Error\",\"results\":\"Server Error.\",\"trace\":\"while trying to load from index 1 of an object array with length 1, loaded from local variable 'path': null\",\"errorLevel\":\"500\"}"
												}
											}
										});
										return true;
									}

									oXhr.respond(204);
									return true;
								}
							});

							requests.push({
								method: "POST",
								path: new RegExp("(.*)/api/v1/PrintQueues/Multipart"),
								response: function(oXhr) {
									// Some checks to ensure the payload is correct
									if (oXhr.requestBody.get("printTask").type !== "application/json") {
										oXhr.respond(500, {}, {});
										return true;
									}

									if (oXhr.requestBody.get("file").type !== "application/pdf") {
										oXhr.respond(500, {}, {});
										return true;
									}

									oXhr.respond(200, {}, "");
									return true;
								}
							});
						}

						if (!_mockServerForQueueModel) {
							_mockServerForQueueModel = new MockServer({
								rootUri: "/backend",
								requests: requests
							});
						} else {
							_mockServerForQueueModel.stop();
						}

						_mockServerForQueueModel.start();
						resolve();
					}).catch(e => {
						Log.error(e);
						var errorMsg = "Failed to load mock data for queues";
						reject(new Error(errorMsg));
					});

			});
		},

		_readPdfFile: function (url) {
			return new Promise( (resolve, reject) => {
				const xhr = new XMLHttpRequest();
				xhr.open("GET", url);
				xhr.responseType = 'blob';

				xhr.onload = function () {
					if (xhr.status === 200) {
						resolve(xhr.response);
						return;
					}

					console.log("Error case - response;");
					console.log(xhr.response);
					reject(xhr.response);
				};

				xhr.send();
			})
		},

		initTemplateModel : function (){
			return new Promise( (resolve, reject) => {
				let requests =  [];
				const templateReader = new JSONModel();

				templateReader.loadData(sap.ui.require.toUrl(_sJsonFilesPath + "/templates.json"));
				const pdfRead = this._readPdfFile(sap.ui.require.toUrl(_sJsonFilesPath + "/Rendered.pdf"));

				Promise.all([
					templateReader.dataLoaded(),
					pdfRead
				])
				.then( (values) => {
					let oUriParameters = new UriParameters(window.location.href);

					requests.push({
						method: "GET",
						path: new RegExp("(.*)/api/v1/Store"),
						response: function(oXhr) {
							if (oXhr.requestHeaders["X-CSRF-Token"] === "Fetch"){ // Token is being asked to this endpoint.
								if (oUriParameters.get("tokenError") === 'Unauthorized')
									oXhr.respond(401);
								else
									oXhr.respondJSON(200, {"X-CSRF-Token" : "ValidCSRFToken"}, templateReader.getJSON());
								return true;
							}

							oXhr.respondJSON(200, {}, templateReader.getJSON());
							return true;
						}
					});

					requests.push({
						method: "POST",
						path: new RegExp("(.*)api/v1/pdf/render"),
						response: function (oXhr) {
							if (oXhr.requestHeaders["X-CSRF-Token"] !== "ValidCSRFToken") {
								oXhr.respond(403);
							}

							if (oXhr.requestBody.includes("Not Existing Template")) {
								oXhr.respondJSON(404, {}, {
									"error": {
										"code": "NotFound: status 404 reading AdsService#renderFormFromStorage(AdsRenderRequest)",
										"message": {
											"lang": "en",
											"value": "{\"message\":\"Internal API Error\",\"results\":\"Template Store Error\",\"trace\":\"Template Store Error: Storage entity cannot be found. Please review your request parameters.\",\"errorLevel\":\"C110\"}"
										}
									}
								});
								return true;
							}

							oXhr.response = values[1];
							oXhr.respond(200, {
								'content-disposition': 'attachment; filename="renderedPdf.pdf"',
								'Content-Type': 'application/octet-stream'
							});
							return true;
						}
					});

					if (!_mockServerForTemplateModel) {
						_mockServerForTemplateModel = new MockServer({
							rootUri: "/backend",
							requests: requests
						});
					} else {
						_mockServerForTemplateModel.stop();
					}

					_mockServerForTemplateModel.start();
					resolve();
				}).catch(e => {
					Log.error(e);
					var errorMsg = "Failed to load mock data for templates";
					reject(new Error(errorMsg));
				});

			});
		}
	};

	return oMockServerInterface;
});
package com.sap.s4hana.sample.print.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
import com.sap.s4hana.sample.print.service.TestPrintServiceProducer;
import com.sap.s4hana.sample.render.service.TestAdsServiceProducer;
import io.restassured.RestAssured;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@RunWith(Arquillian.class)
public class PrintControllerTest {
	
	private static final MockUtil mockUtil = new MockUtil();

	@Rule
	public WireMockRule adsWireMockRule = mockUtil.mockServer(TestAdsServiceProducer.DESTINATION_NAME);
	
	@Rule
	public WireMockRule printWireMockRule = mockUtil.mockServer(TestPrintServiceProducer.DESTINATION_NAME);

	@ArquillianResource
	private URL baseUrl;
	
	@Deployment
	public static WebArchive createDeployment() {
		return TestUtil
				.createDeployment(PrintController.class,
						TestPrintServiceProducer.class,
						TestAdsServiceProducer.class
				)
				.addPackages(/* recursive = */ true, "com.sap.s4hana.sample.rest")
				.addAsResource("application.properties", "WEB-INF/classes/application.properties")
				.addAsWebInfResource("web.xml");
	}

	@BeforeClass
	public static void beforeClass() {
		mockUtil.mockDefaults();
	}

	@Before
	public void before() {
		RestAssured.baseURI = baseUrl.toExternalForm();
	}

	/**
	 * This test uses WireMock stubs defined in the following files:
	 * <ul>
	 * <li>ADS_Forms_by_Adobe_REST_API/RenderPdfFromStore_PrintControllerTest.json</li>
	 * <li>PrintService/PUT_printTask_PrintControllerTest.json</li>
	 * </ul>
	 */
	@Test
	public void testRenderAndPrint() {
		System.out.println("Base URL: " + baseUrl.toExternalForm());
		given().
			contentType(JSON).
			body("{\r\n" + 
					"   \"printTask\":{\r\n" + 
					"      \"qname\":\"expected_queue_name\",\r\n" + 
					"      \"numberOfCopies\":100500,\r\n" + 
					"      \"username\":\"expected_user_name\"\r\n" +
					"   },\r\n" + 
					"   \"renderRequest\":{\r\n" + 
					"      \"templatePath\":\"expected form name/expected template name\",\r\n" + 
					"      \"printData\":{\r\n" + 
					"         \"form\":\"easter egg\"\r\n" + 
					"      }\r\n" + 
					"   }\r\n" + 
					"}").
		when().
			post("/api/v1/PrintQueues").			
		then().
			statusCode(is(204));
	}
	/*
	/**
	 * This test uses WireMock stubs defined in the following file:
	 * <ul>
	 * <li>PrintService/PUT_printTask_PrintControllerTest.json</li>
	 * </ul>
	 */
	@Test
	public void testPrintFileMultipart() throws IOException {
		given().
			multiPart("file", 
					TestUtil.loadFileAsString("/__files/PrintService/not.pdf"), 
					"application/octet-stream").
			multiPart("printTask", 
					"{\r\n" + 
					"  \"qname\": \"expected_queue_name\",\r\n" + 
					"  \"numberOfCopies\": 100500,\r\n" + 
					"  \"username\": \"expected_user_name\",\r\n" +
					"  \"printContents\": {\r\n" + 
					"      \"documentName\": \"expected_name.pdf\",\r\n" +
					"      \"countId\": 0\r\n" +
					"  }\r\n" +
					"}", 
					"application/json").
		when().
			post("/api/v1/PrintQueues/Multipart").
		then().
			statusCode(is(204));
	}
	
	@Test
	public void testPrintFileMultipartDocumentNameMustNotBeNull() throws IOException {
		given().
			multiPart("file", 
					TestUtil.loadFileAsString("/__files/PrintService/not.pdf"), 
					"application/octet-stream").
			multiPart("printTask", 
					"{\r\n" + 
					"  \"qname\": \"expected queue name\",\r\n" + 
					"  \"numberOfCopies\": 100500,\r\n" + 
					"  \"username\": \"expected_user_name\",\r\n" +
					"  \"printContents\": {     }\r\n" + // must contain non-empty documentName
					"}", 
					"application/json").
		when().
			post("/api/v1/PrintQueues/Multipart").
		then().
			statusCode(is(422)).
			body("error.innererror[0].error.message.value", containsString("printFile.printTask.printContents.documentName may not be null"));
	}
	
	@Test
	public void testPrintFileMultipartDocumentNameMustNotBeEmpty() throws IOException {
		given().
			multiPart("file", 
					TestUtil.loadFileAsString("/__files/PrintService/not.pdf"), 
					"application/octet-stream").
			multiPart("printTask", 
					"{\r\n" + 
					"  \"qname\": \"expected queue name\",\r\n" + 
					"  \"numberOfCopies\": 100500,\r\n" + 
					"  \"username\": \"expected_user_name\",\r\n" +
					"  \"printContents\": {\r\n" + 
					"      \"documentName\": \"\"\r\n" + 
					"  }\r\n" + 
					"}", 
					"application/json").
		when().
			post("/api/v1/PrintQueues/Multipart").
		then().
			statusCode(is(422)).
			body("error.innererror[0].error.message.value", containsString("printFile.printTask.printContents.documentName may not be empty"));
	}
	
	@Test
	public void testPrintFileMultipartPrintContentsIsMandatory() throws IOException {
		given().
			multiPart("file", 
					TestUtil.loadFileAsString("/__files/PrintService/not.pdf"), 
					"application/octet-stream").
			multiPart("printTask", 
					"{\r\n" + 
					"  \"qname\": \"expected queue name\",\r\n" + 
					"  \"numberOfCopies\": 100500,\r\n" + 
					"  \"username\": \"expected_user_name\"\r\n" +
					"}", 
					"application/json").
		when().
			post("/api/v1/PrintQueues/Multipart").
		then().
			statusCode(is(422)).
			body("error.innererror[0].error.message.value", containsString("printFile.printTask.printContents may not be null"));
	}
	
	@Test
	public void testBeanValidationWorksForArguments() {
		given().
		contentType(JSON).
		body("{\r\n" + 
				"   \"printTask\": null, \r\n" + // printTask must not be null				
				"   \"renderRequest\":{\r\n" + 
				"      \"templatePath\":\"expected form name/expected template name\",\r\n" + 
				"      \"printData\":{\r\n" + 
				"         \"form\":\"easter egg\"\r\n" + 
				"      }\r\n" + 
				"   }\r\n" + 
				"}").
		when().
			post("/api/v1/PrintQueues").			
		then().
			statusCode(is(422)).
			body("error.innererror[0].error.message.value", containsString("renderAndPrint.body.printTask may not be null"));
	}
	
	@Test
	public void testNotNullValidationWorksForArguments() {
		given(). // body is null
			contentType(JSON).
		when().
			post("/api/v1/PrintQueues").			
		then().
			statusCode(is(422)).
			body("error.innererror[0].error.message.value", containsString("renderAndPrint.body may not be null"));
	}

}

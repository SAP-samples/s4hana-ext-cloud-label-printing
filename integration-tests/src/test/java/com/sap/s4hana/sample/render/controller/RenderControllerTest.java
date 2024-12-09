package com.sap.s4hana.sample.render.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
import com.sap.s4hana.sample.print.service.TestPrintServiceProducer;
import com.sap.s4hana.sample.render.service.TestAdsServiceProducer;
import com.sap.s4hana.sample.rest.RestApplication;
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

import java.net.URL;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.BINARY;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(Arquillian.class)
public class RenderControllerTest {
	
	private static final MockUtil mockUtil = new MockUtil();
	
	@Rule
	public WireMockRule adsWireMockRule = mockUtil.mockErpServer(TestAdsServiceProducer.DESTINATION_NAME);
	
	@Rule
	public WireMockRule printWireMockRule = mockUtil.mockErpServer(TestPrintServiceProducer.DESTINATION_NAME);

	@ArquillianResource
	private URL baseUrl;
	
	@Deployment
	public static WebArchive createDeployment() {
		return TestUtil
				.createDeployment(RenderController.class,
						TestAdsServiceProducer.class)
				.addPackages(/* recursive = */ true, "com.sap.s4hana.sample.rest")
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
	 * This test uses WireMock stubs defined in the following file:
	 * <ul>
	 * <li>ADS_Forms_by_Adobe_REST_API/RenderPdfFromStore_PrintControllerTest.json</li>
	 * </ul>
	 */
	@Test
	public void test() {
		given().
			contentType(JSON).
			body("{\r\n" + 
					"  \"templatePath\":\"expected form name/expected template name\",\r\n" + 
					"  \"printData\":{\r\n" + 
					"    \"form\":\"easter egg\"\r\n" + 
					"  }\r\n" + 
					"}").
		when().
			post(RestApplication.PATH + RenderController.PATH).			
		then().
			statusCode(is(200)).
			body(equalTo("easter egg")).
			contentType(BINARY).
			header("Content-Disposition", "attachment; filename=\"renderedPdf.pdf\"");
	}
	
}

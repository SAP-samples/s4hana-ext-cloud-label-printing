package com.sap.s4hana.sample.render.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
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

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

@RunWith(Arquillian.class)
public class TemplateStoreControllerTest {
		
	private static final MockUtil mockUtil = new MockUtil();
	
	@Rule
	public WireMockRule adsWireMockRule = mockUtil.mockServer(TestAdsServiceProducer.DESTINATION_NAME);
	
	@ArquillianResource
	private URL baseUrl;
		
	@Deployment
	public static WebArchive createDeployment() {
		return TestUtil
				.createDeployment(TemplateStoreController.class,
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
	 * This test uses WireMock stubs defined in the following files:
	 * <ul>
	 * <li>ADS_Forms_by_Adobe_REST_API/GetForms_Status200.json</li>
	 * </ul>
	 * @throws IOException 
	 */
	@Test
	public void testGetFormsWhen_200Response() throws IOException {
		
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/ADS_FormTemplateResponse.json");
		
		given().
		when().
			get("/api/v1/Store").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}
}

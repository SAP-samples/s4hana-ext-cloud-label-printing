package com.sap.s4hana.sample.rest;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
import com.sap.s4hana.sample.render.controller.TemplateStoreController;
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

import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(Arquillian.class)
public class FeignExceptionMapperTest {
	
	private static final MockUtil mockUtil = new MockUtil();

	@Rule
	public WireMockRule wireMockRule = mockUtil.mockErpServer(TestAdsServiceProducer.DESTINATION_NAME);

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

	@Test
	public void test() {
		// Given a stub that models an extremely likely situation described in
		// https://tools.ietf.org/html/rfc2324#section-2.3.2 
		final int expectedErrorCode = 418;
		final String expectedErrorResponseBody = "I'm a little teapot, short and stout"; 
		givenThat(get(anyUrl()).
				willReturn(aResponse().withStatus(expectedErrorCode).withBody(expectedErrorResponseBody)));
		
		given().
		when().
			get("/api/v1/Store").
		then().
			statusCode(is(expectedErrorCode)). // taken from original error response
			body("error.code", equalTo("FeignException: status " + expectedErrorCode + " reading AdsService#getForms()")).
			body("error.message.lang", equalTo("en")).
			body("error.message.value", equalTo(expectedErrorResponseBody));
	}

}

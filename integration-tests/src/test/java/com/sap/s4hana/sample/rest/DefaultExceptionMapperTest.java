package com.sap.s4hana.sample.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.sap.cloud.sdk.cloudplatform.exception.ShouldNotHappenException;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
import com.sap.s4hana.sample.rest.RestApplication;

import io.restassured.RestAssured;

@RunWith(Arquillian.class)
public class DefaultExceptionMapperTest {
	
	private static final MockUtil mockUtil = new MockUtil();
	
	@ArquillianResource
	private URL baseUrl;
	
	@Path(ErrorController.PATH)
	public static class ErrorController {
		
		public static final String PATH = "/Error";

		@GET
		public void error() {
			throw new UnsupportedOperationException("Outer error message", new ShouldNotHappenException("Inner error message"));
		}
		
	}
	
	@Deployment
	public static WebArchive createDeployment() {
		return TestUtil
				.createDeployment(ErrorController.class) // always throws an error
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
		given().
		when().
			get(RestApplication.PATH + ErrorController.PATH).
		then().
			statusCode(is(500)). // default error code
			body(sameJSONAs("{\r\n" + 
					"\"error\": {\r\n" + 
						"\"code\": \"UnsupportedOperationException\",\r\n" + 
						"\"message\": {\r\n" + 
							"\"lang\": \"en\",\r\n" + 
							"\"value\": \"Outer error message\"\r\n" + 
						"},\r\n" + 
						"\"innererror\": [ {\r\n" + 
							"\"error\": {\r\n" + 
								"\"code\": \"ShouldNotHappenException\",\r\n" + 
								"\"message\": {\r\n" + 
									"\"lang\": \"en\",\r\n" + 
									"\"value\": \"Inner error message\"\r\n" + 
								"}\r\n" + 
							"}\r\n" + 
						"} ]\r\n" + 
					"}\r\n" + 
					"}"));
	}

}

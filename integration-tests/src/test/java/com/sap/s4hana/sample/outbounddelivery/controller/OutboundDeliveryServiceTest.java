package com.sap.s4hana.sample.outbounddelivery.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;

import io.restassured.RestAssured;

@RunWith(Arquillian.class)
public class OutboundDeliveryServiceTest {

	private static final MockUtil mockUtil = new MockUtil();

	@Rule
	public WireMockRule wireMockRule = mockUtil.mockErpServer("ErpQueryEndpoint");

	@ArquillianResource
	private URL baseUrl;

	@Deployment
	public static WebArchive createDeployment() {
		return TestUtil
				.createDeployment(OutboundDeliveryService.class,
						org.apache.olingo.odata2.core.servlet.ODataServlet.class)
				.addPackages(/* recursive = */ true, "com.sap.cloud.sdk.service.prov").addAsWebInfResource("web.xml");
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
	public void testMetadata() {
		given().
		when().
			get("/odata/v2/OutboundDeliveryService/$metadata").
		then().
			statusCode(is(200)).
			body("Edmx.DataServices.Schema.@Namespace", equalTo("OutboundDeliveryService")).
			body("Edmx.DataServices.Schema.EntityType.size()", equalTo(2)).
			body("Edmx.DataServices.Schema.EntityType[0].@Name", equalTo("A_OutbDeliveryHeader")).
			body("Edmx.DataServices.Schema.EntityType[1].@Name", equalTo("A_OutbDeliveryItem"));
	}

	@Test
	public void testQueryAllDeliveries() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryHeader_queryAll.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}
	
	@Test
	public void testReadOneDelivery() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryHeader_readOne.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			urlEncodingEnabled(false). // otherwise ( ) and ' in the request path will be encoded
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader('80000000')").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}
	
	@Test
	public void testQueryDeliveriesWithAllParameters() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryHeader_queryWithAllParameters.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			queryParam("$top", 3).
			queryParam("$skip", 2).
			queryParam("$select", "DeliveryDocument").
			queryParam("$filter", "DeliveryDocument ne '80000000'").
			queryParam("$inlinecount", "allpages").
			queryParam("$orderby", "CreationDate,DeliveryDocument desc").
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields());
	}
	
	@Test
	public void testReadOneDeliveryItem() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryItem_readOne.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			urlEncodingEnabled(false). // otherwise ( ) and ' in the request path will be encoded
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryItem(DeliveryDocument='80000000',DeliveryDocumentItem='000010')").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields().allowingAnyArrayOrdering());
	}
	
	@Test
	public void testQueryDeliveryItemsWithAllParameters() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryItem_queryWithAllParameters.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			queryParam("$top", 3).
			queryParam("$skip", 2).
			queryParam("$select", "DeliveryDocument,DeliveryDocumentItem").
			queryParam("$filter", "DeliveryDocument ne '80000000'").
			queryParam("$inlinecount", "allpages").
			queryParam("$orderby", "CreationDate,DeliveryDocument desc").
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryItem").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields());
	}
	
	/* tests for $expand and navigational properties */
	
	@Test
	public void testQueryDeliveriesExpandToItems() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryHeader_queryExpandToItems.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			queryParam("$expand", "to_DeliveryDocumentItem").
			queryParam("$select", "DeliveryDocument,to_DeliveryDocumentItem/DeliveryDocument,to_DeliveryDocumentItem/DeliveryDocumentItem").
			queryParam("$filter", "DeliveryDocument eq '80000000'").
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields());
	}
	
	@Test
	public void testReadDeliveryExpandToItems() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryHeader_readExpandToItems.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			queryParam("$expand", "to_DeliveryDocumentItem").
			queryParam("$select", "DeliveryDocument,to_DeliveryDocumentItem/DeliveryDocument,to_DeliveryDocumentItem/DeliveryDocumentItem").
			urlEncodingEnabled(false). // otherwise ( ) and ' in the request path will be encoded
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader('80000000')").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields());
	}
	
	@Test
	public void testNavigationFromHeaderViaNavigationProperty() throws IOException {
		final String expectedResponseJson = TestUtil.loadFileAsString("/expectedResults/A_OutbDeliveryItem_navigationFromHeaderViaNavigationProperty.json");
		
		given().
			header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).
			queryParam("$select", "DeliveryDocument,DeliveryDocumentItem").
			urlEncodingEnabled(false). // otherwise ( ) and ' in the request path will be encoded
		when().
			get("/odata/v2/OutboundDeliveryService/A_OutbDeliveryHeader('80000000')/to_DeliveryDocumentItem").
		then().
			statusCode(is(200)).
			header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON). 
			body(sameJSONAs(expectedResponseJson).allowingExtraUnexpectedFields());
	}
	
}

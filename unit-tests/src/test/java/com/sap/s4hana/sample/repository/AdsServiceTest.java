package com.sap.s4hana.sample.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.render.model.AdsRenderRequest;
import com.sap.s4hana.sample.render.model.AdsRenderResponse;
import com.sap.s4hana.sample.render.model.GetFormResponse;
import com.sap.s4hana.sample.render.model.GetFormResponse.Template;
import com.sap.s4hana.sample.render.service.AdsService;
import com.sap.s4hana.sample.render.service.AdsServiceProducer;

public class AdsServiceTest {
	
	public static final MockUtil mockUtil = new MockUtil();
	
	@Rule
	public WireMockRule wireMockRule = mockUtil.mockServer(AdsService.DESTINATION_NAME);
	
	AdsService testee;
	
	@BeforeClass
	public static void setUpBeforeClass() {
		mockUtil.mockDefaults();
	}
	
	@Before
	public void setUp() {
		testee = new AdsServiceProducer().adsService();
	}
	
	@Test
	public void testRenderFormFromTemplate() throws JsonMappingException, JsonProcessingException {
		
		// Given	
		final Map<Object, Object> dataContent = new LinkedHashMap<>();
		// Encodes <form>easter egg</form>
		dataContent.put("form", "easter egg");
		
		final AdsRenderRequest testRequest = AdsRenderRequest.builder()
				.xdpTemplate("expected_form_name")
				.data(dataContent)
				.build();	
		
		// @see mappings/ADS-Store/RenderPdfFromStore_Status200.json
		final AdsRenderResponse expectedResponse = AdsRenderResponse.builder()
				.fileName("expectedFilename")
				.fileContent("ZWFzdGVyIGVnZw==")
				.build();
		
		// When
		final AdsRenderResponse actualResponse = testee.renderFormFromStorage(testRequest);

		// Then
		assertThat("Service response", actualResponse, samePropertyValuesAs(expectedResponse));
	}
	
	@Test
	public void testGetForms() {
		// Given	
		final Template expectedTemplate1 = Template.builder()
				.templateName("ExpectedTemplate1")
				.build();
		
		final Template expectedTemplate2 = Template.builder()
				.templateName("ExpectedTemplate2")
				.build();
		
		final List<Template> expectedTemplates = new ArrayList<>();
		expectedTemplates.add(expectedTemplate1);
		expectedTemplates.add(expectedTemplate2);
		
		final GetFormResponse expectedFormTemplateStucture = GetFormResponse.builder()
				.formName("FormA")
				.templates(expectedTemplates)
				.build();
		
		final List<GetFormResponse> expectedFormTemplateResponse = new ArrayList<>();
		expectedFormTemplateResponse.add(expectedFormTemplateStucture);
				
		// When
		final List<GetFormResponse> actualResponse = testee.getForms();

		// Then
		assertThat("Get Forms response", actualResponse, equalTo(expectedFormTemplateResponse));		
	}	
}


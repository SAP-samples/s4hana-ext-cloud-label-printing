package com.sap.s4hana.sample.print.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.net.URI;
import java.util.function.Consumer;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.s4hana.sample.print.service.PrintService;
import com.sap.s4hana.sample.print.service.PrintServiceProducer;

import feign.RequestTemplate;

public class PrintServiceProducerTest {

	private static final String EXPECTED_CLIENT_ID = "expected_client_id";
	private static final String EXPECTED_CLIENT_SECRET = "expected_client_secret";
	private static final String EXPECTED_UAA_URL = "https://example.com/uaa_url";
	private static final String EXPECTED_SERVICE_URL = "https://example.com/service_url";
	
	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
		.set("VCAP_SERVICES", 
				String.format("{\r\n" + 
					"   \"print\":[\r\n" + 
					"      {\r\n" + 
					"         \"label\":\"print\",\r\n" + 
					"         \"plan\":\"sender\",\r\n" + 
					"         \"name\":\"printServiceInstanceName\",\r\n" + 
					"         \"credentials\":{\r\n" + 
					"            \"uaa\":{\r\n" + 
					"               \"clientid\":\"%s\",\r\n" + 
					"               \"clientsecret\":\"%s\",\r\n" + 
					"               \"url\":\"%s\"\r\n" + 
					"            },\r\n" + 
					"            \"service_url\":\"%s\"\r\n" + 
					"         }\r\n" + 
					"      }\r\n" + 
					"   ]\r\n" + 
					"}", 
					EXPECTED_CLIENT_ID, 
					EXPECTED_CLIENT_SECRET, 
					EXPECTED_UAA_URL, 
					EXPECTED_SERVICE_URL));
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

	@Mock
	ScpCfService scpCfServiceMock;
	
	@Spy
	RequestTemplate requestTemplateSpy;
	
	@Test
	public void testScpServiceFromVcapServices() {
		final ScpCfService scpService = new PrintServiceProducer().getScpPrintService();
		
		assertThat("service URL", scpService.getServiceLocationInfo(), is(equalTo(EXPECTED_SERVICE_URL)));
		assertThat("service client id", scpService.getClientCredentials().getClientId(), is(equalTo(EXPECTED_CLIENT_ID)));
		assertThat("service client secret", scpService.getClientCredentials().getClientSecret(), is(equalTo(EXPECTED_CLIENT_SECRET)));
		assertThat("service UAA URL", scpService.getAuthUri(), is(equalTo(URI.create(EXPECTED_UAA_URL))));
	}
	
	@Test
	public void testProducerMehtod() {
		final PrintService producedService = new PrintServiceProducer().createPrintService();
		
		// we can only check if nothing bad happens as the result service is a proxy
		assertThat("print service", producedService, is(not(nullValue())));
	}
	
	/* tests for PrintServiceProducer.AuthHeaderSetter */
	
	@Test
	public void testAuthHeaderSetter() {
		// Given
		final String expectedAuthHeaderValue = "ExpectedAuthHeader";
		
		doAnswer(by(req -> req.addHeader(HttpHeaders.AUTHORIZATION, expectedAuthHeaderValue)))
			.when(scpCfServiceMock)
			.addBearerTokenHeader(Mockito.any());
		
		// When
		PrintServiceProducer.AuthHeaderSetter.of(scpCfServiceMock).apply(requestTemplateSpy);

		// Then
		verify(requestTemplateSpy).header(HttpHeaders.AUTHORIZATION, expectedAuthHeaderValue);
	}
	
	@Test
	public void testAuthHeaderSetterWhenHeaderIsSetToNull() {
		// Given an ScpService that sets the auth header to null
		doAnswer(by(req -> req.addHeader(HttpHeaders.AUTHORIZATION, null)))
			.when(scpCfServiceMock)
			.addBearerTokenHeader(Mockito.any());
		
		// When
		PrintServiceProducer.AuthHeaderSetter.of(scpCfServiceMock).apply(requestTemplateSpy);

		// Then auth header is not set on the request
		verifyNoInteractions(requestTemplateSpy);
	}
	
	@Test
	public void testAuthHeaderSetterWhenHeaderIsNotSet() {
		// Given an ScpService that doesn't set the auth header
		doAnswer(by(req -> {}))
			.when(scpCfServiceMock)
			.addBearerTokenHeader(Mockito.any());
		
		// When
		PrintServiceProducer.AuthHeaderSetter.of(scpCfServiceMock).apply(requestTemplateSpy);

		// Then auth header is not set on the request
		verifyNoInteractions(requestTemplateSpy);
	}
	
	/**
	 * This is a helper method that makes {@link ScpCfService}'s mock do something
	 * when the {@link ScpCfService#addBearerTokenHeader(HttpRequest)} method is
	 * called
	 * 
	 * @param actionOnHttpRequest action to be executed on {@link HttpRequest}
	 * @return nothing (void method)
	 */
	private static Answer<Void> by(final Consumer<HttpRequest> actionOnHttpRequest) {
		return invocation -> {
			HttpRequest arg = (HttpRequest) invocation.getArguments()[0];

			actionOnHttpRequest.accept(arg);

			return null;
		};
	}

}

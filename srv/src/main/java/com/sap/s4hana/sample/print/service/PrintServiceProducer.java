package com.sap.s4hana.sample.print.service;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpGet;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.sdk.cloudplatform.ScpCfServiceDesignator;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.s4hana.sample.util.DelegatingEncoder;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.RequiredArgsConstructor;

@ApplicationScoped
public class PrintServiceProducer {
	
	@Produces @RequestScoped
	public PrintService createPrintService() {
		final ScpCfService printService = getScpPrintService();
		
		return Feign.builder()
				.encoder(DelegatingEncoder.builder()
						.encoder("application/json", new JacksonEncoder())
						.build())
				.decoder(new JacksonDecoder())
				.requestInterceptor(AuthHeaderSetter.of(printService))
				.logger(new Slf4jLogger(PrintService.class))
				.target(PrintService.class, printService.getServiceLocationInfo());
	}
	
	@VisibleForTesting
	protected ScpCfService getScpPrintService() {
		final ScpCfServiceDesignator printServiceDesignator = ScpCfServiceDesignator.builder()
				.serviceType("print")
				.servicePlan("sender")
				.build();
		
		return ScpCfService.of(printServiceDesignator, 
				"credentials/uaa/url", 
				"credentials/uaa/clientid", 
				"credentials/uaa/clientsecret", 
				"credentials/service_url");
	}
	
	/**
	 * This helper class for Feign handles authentication to the SAP Business Technology Platform
	 * Print Service using {@link ScpCfService#addBearerTokenHeader(HttpRequest)}
	 * method.
	 * 
	 */
	@VisibleForTesting
	@RequiredArgsConstructor(staticName = "of")
	protected static class AuthHeaderSetter implements RequestInterceptor {
		
		private final ScpCfService scpCfService;

		@Override
		public void apply(RequestTemplate template) {
			final HttpGet interceptor = new HttpGet();
			
			scpCfService.addBearerTokenHeader(interceptor);

			Optional.ofNullable(interceptor.getFirstHeader(HttpHeaders.AUTHORIZATION)).
				map(Header::getValue).
				ifPresent(value -> template.header(HttpHeaders.AUTHORIZATION, value));
		}
		
	}

}

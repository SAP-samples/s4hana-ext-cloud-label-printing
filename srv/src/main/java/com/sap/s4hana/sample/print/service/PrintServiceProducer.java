package com.sap.s4hana.sample.print.service;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.sdk.cloudplatform.ScpCfServiceDesignator;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.s4hana.sample.util.AuthHeaderSetterViaXsuaaService;
import com.sap.s4hana.sample.util.CustomPrintDecoder;
import com.sap.s4hana.sample.util.DelegatingEncoder;
import feign.Feign;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
@Slf4j
public class PrintServiceProducer {

	@Produces @RequestScoped
	public PrintService createPrintService() {
		final ScpCfService printService = getScpPrintService();

		return Feign.builder()
			.contract(new JAXRSContract())
			.encoder(DelegatingEncoder.builder()
				.encoder("application/json", new JacksonEncoder())
				.build())
			.decoder(new CustomPrintDecoder())
			.requestInterceptor(AuthHeaderSetterViaXsuaaService.of(printService))
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

}
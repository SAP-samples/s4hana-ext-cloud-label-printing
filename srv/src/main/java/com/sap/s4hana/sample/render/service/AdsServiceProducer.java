package com.sap.s4hana.sample.render.service;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.sdk.cloudplatform.ScpCfServiceDesignator;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.s4hana.sample.print.service.PrintService;
import com.sap.s4hana.sample.util.AuthHeaderSetterViaXsuaaService;
import com.sap.s4hana.sample.util.DelegatingEncoder;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.slf4j.Slf4jLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class AdsServiceProducer {

	@Produces
	@RequestScoped
	public AdsService createAdsService() {
		final ScpCfService adsService = getScpAdsService();

		return Feign.builder()
			.contract(new JAXRSContract())
			.encoder(DelegatingEncoder.builder()
				.encoder("application/json", new JacksonEncoder())
				.build())
			.decoder(new JacksonDecoder())
			.requestInterceptor(AuthHeaderSetterViaXsuaaService.of(adsService))
			.logger(new Slf4jLogger(PrintService.class))
			.target(AdsService.class, adsService.getServiceLocationInfo());
	}

	@VisibleForTesting
	protected ScpCfService getScpAdsService() {

		final ScpCfServiceDesignator adsServiceDesignator = ScpCfServiceDesignator.builder()
			.serviceType("adsrestapi")
			.servicePlan("standard")
			.build();

		return ScpCfService.of(adsServiceDesignator,
			"credentials/uaa/url",
			"credentials/uaa/clientid",
			"credentials/uaa/clientsecret",
			"credentials/uri");
	}

}
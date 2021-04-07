package com.sap.s4hana.sample.render.service;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import com.sap.s4hana.sample.util.DestinationHelper;

import feign.Feign;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;

@ApplicationScoped
public class AdsServiceProducer {
	
	@Produces
	public AdsService adsService() { 
		return Feign.builder()
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.logger(new Slf4jLogger(AdsService.class))
				.client(new ApacheHttpClient(DestinationHelper.getHttpClient(AdsService.DESTINATION_NAME)))
			.target(AdsService.class, DestinationHelper.getUrl(AdsService.DESTINATION_NAME));
	}
	
}

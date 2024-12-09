package com.sap.s4hana.sample.render.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.util.DestinationHelper;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.slf4j.Slf4jLogger;
import org.junit.Rule;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

/**
 * If you list this class in {@link TestUtil#createDeployment(Class...)}, it
 * will produce a {@link AdsService} to be injected
 * via @{@link javax.inject.Inject} in tests.
 * <p>
 * The produced {@link AdsService} makes calls to a local WireMock server that
 * can be started in JUnit tests like this:
 * <pre>
 * <tab>@{@link Rule}
 * <tab>{@link WireMockRule} wireMockRule =
 *         {@link MockUtil}.mockErpServer({@link TestAdsServiceProducer#DESTINATION_NAME});
 * <pre>
 *
 */
@ApplicationScoped
public class TestAdsServiceProducer {

	public static final String DESTINATION_NAME = "WireMockServer";

	@Produces @RequestScoped
	public AdsService createAdsService() {
		return Feign.builder()
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new JacksonDecoder())
				.logger(new Slf4jLogger(AdsService.class))
			.target(AdsService.class, DestinationHelper.getUrl(DESTINATION_NAME));
	}
	
}

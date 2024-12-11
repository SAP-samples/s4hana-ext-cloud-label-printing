package com.sap.s4hana.sample.print.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sap.cloud.sdk.testutil.MockUtil;
import com.sap.s4hana.sample.TestUtil;
import com.sap.s4hana.sample.util.DestinationHelper;
import com.sap.s4hana.sample.util.TestHeaderSetterViaXsuaaService;
import feign.Feign;
import feign.codec.StringDecoder;
import feign.jackson.JacksonEncoder;
import feign.jaxrs.JAXRSContract;
import feign.slf4j.Slf4jLogger;
import org.junit.Rule;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

/**
 * If you list this class in {@link TestUtil#createDeployment(Class...)}, it
 * will produce a {@link PrintService} to be injected
 * via @{@link javax.inject.Inject} in tests.
 * <p>
 * The produced {@link PrintService} makes calls to a local WireMock server that
 * can be started in JUnit tests like this:
 * <pre>
 * <tab>@{@link Rule}
 * <tab>{@link WireMockRule} wireMockRule =
 *         {@link MockUtil}.mockErpServer({@link TestPrintServiceProducer#DESTINATION_NAME});
 * <pre>
 *
 */
@ApplicationScoped
public class TestPrintServiceProducer {
	
	public static final String DESTINATION_NAME = "WireMockServer";

	@Produces @RequestScoped
	public PrintService createPrintService() {
		return Feign.builder()
				.contract(new JAXRSContract())
				.encoder(new JacksonEncoder())
				.decoder(new StringDecoder())
				.requestInterceptor(new TestHeaderSetterViaXsuaaService())
				.logger(new Slf4jLogger(PrintService.class))
			.target(PrintService.class, DestinationHelper.getUrl(DESTINATION_NAME));
	}

}
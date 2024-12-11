package com.sap.s4hana.sample.print.service;

import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import feign.RequestTemplate;
import org.apache.http.HttpRequest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.net.URI;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PrintServiceProducerTest {

	private static final String EXPECTED_CLIENT_ID = "expected_client_id";
	private static final String EXPECTED_CLIENT_SECRET = "expected_client_secret";
	private static final String EXPECTED_UAA_URL = "https://example.com/uaa_url";
	private static final String EXPECTED_SERVICE_URL = "https://example.com/service_url";
	
	@Rule
	public final EnvironmentVariables environmentVariables = new EnvironmentVariables()
		.set("VCAP_SERVICES", 
				String.format("{  " + 
					"\"print\": [ " +
						"{ " +
						"\"label\": \"print\", " +
						"  \"provider\": null, " +
						"  \"plan\": \"sender\", " +
						"  \"name\": \"print-sender\", " +
						"  \"tags\": [ " +
						"   \"Print\", " +
						"   \"Output Management\" " +
						"  ], " +
						"  \"instance_name\": \"print-sender\", " +
						"  \"binding_name\": null, " +
						"  \"credentials\": { " +
						"   \"uaa\": { " +
						"   \"tenantmode\": \"shared\", " +
						"   \"sburl\": \"\", " +
						"   \"credential-type\": \"binding-secret\", " +
						"   \"clientid\": \"expected_client_id\", " +
						"   \"clientsecret\": \"expected_client_secret\", " +
						"   \"url\": \"https://example.com/uaa_url\", " +
						"   \"uaadomain\": \"\", " +
						"   }, " +
						"   \"service_url\": \"https://example.com/service_url\" " +
						"  }, " +
						"  \"syslog_drain_url\": null, " +
						"  \"volume_mounts\": [] " +
						"  } " +
						" ],  " +
						"\"adsrestapi\": [" +
						"{" +
						"\"label\": \"adsrestapi\"," +
						"\"provider\": null," +
						"\"plan\": \"standard\"," +
						"\"name\": \"adobe-form-rest\"," +
						"\"tags\": []," +
						"\"instance_guid\": \"\"," +
						"\"instance_name\": \"\"," +
						"\"binding_guid\": \"\"," +
						"\"binding_name\": null," +
						"\"credentials\": {" +
						"\"uri\": \"https://example.com/service_url\"," +
						"\"uaa\": {" +
						"\"tenantmode\": \"shared\"," +
						"\"sburl\": \"\"," +
						"\"subaccountid\": \"\"," +
						"\"credential-type\": \"binding-secret\"," +
						"\"clientid\": \"expected_client_id\"," +
						"\"xsappname\": \"\"," +
						"\"clientsecret\": \"expected_client_secret\"," +
						"\"serviceInstanceId\": \"\"," +
						"\"url\": \"https://example.com/uaa_url\"," +
						"\"uaadomain\": \"\"," +
						"\"verificationkey\": \"\"," +
						"\"apiurl\": \"\"," +
						"\"identityzone\": \"\"," +
						"\"identityzoneid\": \"\"," +
						"\"tenantid\": \"\"," +
						"\"zoneid\": \"\"" +
						"}" +
						"}," +
						"\"syslog_drain_url\": null," +
						"\"volume_mounts\": []" +
						"}" +
						"]" +
					"},",
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
	public void testScpServiceFromVcaoServices() {
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

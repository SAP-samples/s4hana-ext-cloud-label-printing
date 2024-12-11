package com.sap.s4hana.sample.util;

import org.apache.http.client.HttpClient;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

public class DestinationHelper {

	/**
	 * @return the URL of the destination with {@code destinationName}
	 * 
	 */
	public static String getUrl(String destinationName) {
		return DestinationAccessor
				.getDestination(destinationName)
				.asHttp()
				.getUri()
				.toString();
	}

	/**
	 * @return an HTTP client preconfigured for the destination
	 * 
	 */
	public static HttpClient getHttpClient(final String destinationName) {
		final HttpDestination s4HanaDestination = DestinationAccessor
				.getDestination(destinationName)
				.asHttp();
		
		return HttpClientAccessor.getHttpClient(s4HanaDestination);
	}
	
}

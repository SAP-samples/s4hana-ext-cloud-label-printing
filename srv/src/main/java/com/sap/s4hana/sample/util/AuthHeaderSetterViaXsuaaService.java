package com.sap.s4hana.sample.util;

import com.google.common.annotations.VisibleForTesting;
import com.sap.cloud.sdk.cloudplatform.connectivity.Header;
import com.sap.cloud.sdk.cloudplatform.connectivity.ScpCfService;
import com.sap.cloud.sdk.cloudplatform.connectivity.XsuaaService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * This helper class for Feign handles authentication to the SAP Business Technology Platform by token exchange
 * Print Service using
 * {@link XsuaaService#retrieveAccessTokenHeaderViaUserTokenGrant(java.net.URI, com.sap.cloud.sdk.cloudplatform.security.ClientCredentials)}
 * method.
 */
@VisibleForTesting
@RequiredArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @VisibleForTesting)
public class AuthHeaderSetterViaXsuaaService implements RequestInterceptor {

	public static AuthHeaderSetterViaXsuaaService of(ScpCfService scpCfService) {
		return new AuthHeaderSetterViaXsuaaService(scpCfService, new XsuaaService());
	}

	private final ScpCfService scpCfService;

	private final XsuaaService xsuaaService;

	@Override
	public void apply(RequestTemplate template) {
		// test
		final Header authHeader = xsuaaService.retrieveAccessTokenHeaderViaClientCredentialsGrant(
			scpCfService.getAuthUri(),
			scpCfService.getClientCredentials());
		template.header(authHeader.getName(), authHeader.getValue());
		template.header("If-None-Match", "*");
	}

}
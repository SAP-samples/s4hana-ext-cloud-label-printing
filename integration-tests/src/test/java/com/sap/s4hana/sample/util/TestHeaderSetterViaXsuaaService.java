package com.sap.s4hana.sample.util;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TestHeaderSetterViaXsuaaService  implements RequestInterceptor  {

	@Override
	public void apply(RequestTemplate template) {
		template.header("If-None-Match", "*");
	}
}

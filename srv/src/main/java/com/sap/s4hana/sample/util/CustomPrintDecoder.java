package com.sap.s4hana.sample.util;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.codec.StringDecoder;
import feign.jackson.JacksonDecoder;

import java.io.IOException;
import java.lang.reflect.Type;

public class CustomPrintDecoder implements Decoder {

	private final Decoder jsonDecoder = new JacksonDecoder();
	private final Decoder stringDecoder = new StringDecoder();

	@Override
	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
		if(response.request().url().contains("/dm/")){
			return stringDecoder.decode(response, type);
		} else {
			return jsonDecoder.decode(response, type);
		}
	}
}

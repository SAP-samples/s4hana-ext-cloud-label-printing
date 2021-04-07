package com.sap.s4hana.sample.util;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.annotations.VisibleForTesting;

import feign.Headers;
import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;

/**
 * By default, Feign uses the same {@link Encoder} for all content types.
 * <p>
 * This encoder allows to register a dedicated encoder for each content type.
 * <p>
 * Note that the content must be set via "Content-Type" {@link Headers Header}
 * and both header name and value are case-sensitive.
 *
 */
@Getter
@Builder
public class DelegatingEncoder implements Encoder {
	
	@Singular
	private Map<String, Encoder> encoders;
	
	@lombok.Builder.Default
	@NonNull
	private Encoder defaultEncoder = new Encoder.Default();

	@Override
	public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
		final String contentType = Optional.ofNullable(template.headers().get("Content-Type"))
			.orElse(Collections.emptyList()) // an HTTP header can have several values
			.stream()
			.findFirst()
			.orElse(null);
		
		getEncoder(contentType).encode(object, bodyType, template);
	}
	
	@VisibleForTesting
	protected Encoder getEncoder(@Nullable String contentType) {
		return Optional.ofNullable(contentType)
				.map(encoders::get)
				.orElse(defaultEncoder);
	}

}

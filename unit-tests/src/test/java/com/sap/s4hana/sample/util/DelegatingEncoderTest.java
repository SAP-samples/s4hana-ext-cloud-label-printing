package com.sap.s4hana.sample.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import feign.codec.Encoder;
import feign.jackson.JacksonEncoder;

public class DelegatingEncoderTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testLombokAssignsEmptyCollectionWhenSingularSetterNotCalled() {
		final DelegatingEncoder testee = DelegatingEncoder.builder().build();
		
		assertThat("delegates map", testee.getEncoders(), is(not(nullValue())));
		assertThat("delegates map must be empty", testee.getEncoders().isEmpty());
	}
	
	@Test
	public void testNonNullOnBuilder() {
		thrown.expect(IllegalArgumentException.class);
		thrown.expectMessage("defaultEncoder is marked non-null but is null");
		
		DelegatingEncoder.builder().defaultEncoder(null);
	}
	
	@Test
	public void testDefaultEncoderIsReturnedForNotSupportedContentTypes() {
		final DelegatingEncoder testee = DelegatingEncoder.builder()
				.encoder("application/json", new JacksonEncoder())
				.build();
		
		assertThat("getEncoder(not json)", 
				testee.getEncoder("not json"),
				is(instanceOf(Encoder.Default.class)));
	}
	
	@Test
	public void testDefaultEncoderIsReturnedForNull() {
		final DelegatingEncoder testee = DelegatingEncoder.builder()
				.encoder("application/json", new JacksonEncoder())
				.build();
		
		assertThat("getEncoder(null)", 
				testee.getEncoder(null),
				is(instanceOf(Encoder.Default.class)));
	}
	
	@Test
	public void testDefaultEncoderIsReturnedForNullEvenWhenNullEncoderIsSet() {
		final DelegatingEncoder testee = DelegatingEncoder.builder()
				.encoder("application/json", new JacksonEncoder())
				.encoder(null, null)
				.build();
		
		assertThat("getEncoder(null)", 
				testee.getEncoder(null),
				is(instanceOf(Encoder.Default.class)));
		
		assertThat("encoders.get(null)", 
				testee.getEncoders().get(null),
				is(nullValue()));
	}
	
	@Test
	public void testDefaultEncoderIsNotOverridenByDelegateForNullContentType() {
		final DelegatingEncoder testee = DelegatingEncoder.builder()
				.encoder(null, new JacksonEncoder())
				.build();
		
		assertThat("getEncoder(null)", 
				testee.getEncoder(null),
				is(instanceOf(Encoder.Default.class)));
		
		assertThat("encoders.get(null)", 
				testee.getEncoders().get(null),
				is(instanceOf(JacksonEncoder.class)));
	}

}

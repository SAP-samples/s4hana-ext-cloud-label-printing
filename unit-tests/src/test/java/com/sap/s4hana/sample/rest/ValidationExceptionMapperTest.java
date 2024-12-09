package com.sap.s4hana.sample.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.mockito.Answers;

import com.google.common.collect.Sets;
import com.sap.s4hana.sample.rest.ExceptionResponse;
import com.sap.s4hana.sample.rest.ValidationExceptionMapper;

import validation.ValidationUtil;

public class ValidationExceptionMapperTest {
	
	@Test
	public void testExceptionResponseWithViolations() {
		final String expectedMessage = "expected message";
		final String[] violationMessages = new String[] {"Violation message 1", "Violation message 2"};
		
		// Given a list of violations with expected messages
		final Set<ConstraintViolation<?>> violations = Arrays.stream(violationMessages)
				.map(ValidationExceptionMapperTest::mockViolation)
				.collect(Collectors.toSet());
		
		// Given a ConstraintViolationException with the violations
		final ConstraintViolationException exception = new ConstraintViolationException(expectedMessage, violations);
		
		// When
		final ExceptionResponse errorResponse = ValidationExceptionMapper.toExceptionResponse(exception);
		
		// Then
		assertThat("errorResponse.code", errorResponse.getCode(), is(ConstraintViolationException.class.getName()));
		assertThat("errorResponse.message", errorResponse.getMessage().getValue(), is(expectedMessage));

		// Then there are two causes
		assertThat("errorResponse.cause", errorResponse.getInnerErrors(), hasSize(violationMessages.length));
		
		// And their code is ValidationExceptionMapper.CAUSE_ERROR_CODE
		final List<String> causeCodes = errorResponse.getInnerErrors().stream()
				.map(ExceptionResponse::getCode)
				.distinct()
				.collect(Collectors.toList());
		
		assertThat("codes of errorResponse.cause", 
				causeCodes,
				containsInAnyOrder(ValidationExceptionMapper.CAUSE_ERROR_CODE));
		
		// Then messages of causes are equal to violation messages
		final List<String> causeMessages = errorResponse.getInnerErrors().stream()
				.map(response -> response.getMessage().getValue())
				.distinct()
				.collect(Collectors.toList());
		
		assertThat("messages of errorResponse.cause", 
				causeMessages,
				containsInAnyOrder(violationMessages));
	}
	
	@Test
	public void testExceptionResponseDoesNotContainViolationIfItIsNull() {
		// Given a ConstraintViolationException with the null violation
		final ConstraintViolationException exception = new ConstraintViolationException("any message",
				/* constraintViolations */ Collections.singleton(null));
		
		// When
		final ExceptionResponse errorResponse = ValidationExceptionMapper.toExceptionResponse(exception);
		
		// Then
		assertThat("errorResponse.cause", errorResponse.getInnerErrors(), is(empty()));
	}
	
	@Test
	public void testExceptionResponseDoesNotContainDuplicatedViolationMessages() {
		final String violationMessage = "expected violation message";
		
		// Given a list of two violations with the same message
		final HashSet<ConstraintViolation<?>> duplicatedViolations = Sets.newHashSet(mockViolation(violationMessage), mockViolation(violationMessage));
		
		// Given a ConstraintViolationException with the violations
		final ConstraintViolationException exception = new ConstraintViolationException(violationMessage,
				/* constraintViolations */ duplicatedViolations);
		
		// When
		final ExceptionResponse errorResponse = ValidationExceptionMapper.toExceptionResponse(exception);
		
		assertThat("errorResponse.cause", errorResponse.getInnerErrors(), hasSize(1));
		
		// Then messages of causes are equal to violation messages
		final List<String> causeMessages = errorResponse.getInnerErrors().stream()
				.map(response -> response.getMessage().getValue())
				.collect(Collectors.toList());
		
		assertThat("messages of errorResponse.cause", 
				causeMessages,
				containsInAnyOrder(violationMessage));
	}

	protected static ConstraintViolation<?> mockViolation(String message) {
		final ConstraintViolation<?> violationMock = mock(ConstraintViolation.class, Answers.RETURNS_DEEP_STUBS);
		doReturn(message).when(violationMock).getMessage();
		return violationMock;
	}
	
	@Test
	public void testViolationMessageWhenPropertyPathIsNull() {
		// Given a violation which property path is null		
		final String violationMessagePart = "violation message part";
		final ConstraintViolation<?> violationMock = mockViolation(violationMessagePart);
		when(violationMock.getPropertyPath()).thenReturn(null);
		
		// When
		final String violationMessage = ValidationExceptionMapper.getViolationMessage(violationMock);
		
		// Then
		assertThat("violation message", violationMessage, is(violationMessagePart));
	}
	
	@Test
	public void testViolationMessageWhenPropertyPathIsNullWhenConvertedToString() {
		// Given a violation which property path is null when converted to string		
		final String violationMessagePart = "violation message part";
		final ConstraintViolation<?> violationMock = mockViolation(violationMessagePart);
		when(violationMock.getPropertyPath().toString()).thenReturn(null);
		
		// When
		final String violationMessage = ValidationExceptionMapper.getViolationMessage(violationMock);
		
		// Then
		assertThat("violation message", violationMessage, is(violationMessagePart));
	}
	
	@Test
	public void testViolationMessageWhenMessageIsNull() {
		// Given a violation which property path is not null but the message is		
		final ConstraintViolation<?> violationMock = mockViolation(null);
		final String propertyPath = "property path";
		when(violationMock.getPropertyPath().toString()).thenReturn(propertyPath);
		
		// When
		final String violationMessage = ValidationExceptionMapper.getViolationMessage(violationMock);
		
		// Then
		assertThat("violation message", violationMessage, is(propertyPath));
	}
	
	@Test
	public void testViolationMessageWhenMessageAndPropertyPathAreNull() {
		// Given a violation which property path AND the message are both null		
		final ConstraintViolation<?> violationMock = mockViolation(null);
		when(violationMock.getPropertyPath().toString()).thenReturn(null);
		
		// When
		final String violationMessage = ValidationExceptionMapper.getViolationMessage(violationMock);
		
		// Then
		assertThat("violation message", violationMessage, is(""));
	}
	
	public static class InvalidTestee {
		
		@NotNull
		final Object invalidProperty = null;
		
	}
	
	@Test
	public void testWithRealInvalidBean() {
		final String expectedMessage = "expected message";
		final Set<ConstraintViolation<InvalidTestee>> violations = ValidationUtil.validate(new InvalidTestee());
		// Given a ConstraintViolationException with the violations
		final ConstraintViolationException exception = new ConstraintViolationException(expectedMessage, violations);
		
		// When
		final ExceptionResponse errorResponse = ValidationExceptionMapper.toExceptionResponse(exception);
		
		// Then
		assertThat("errorResponse.code", errorResponse.getCode(), is(ConstraintViolationException.class.getName()));
		assertThat("errorResponse.message", errorResponse.getMessage().getValue(), is(expectedMessage));

		// Then there is one cause
		assertThat("errorResponse.cause", errorResponse.getInnerErrors(), hasSize(1));
		
		// And its message is as expeceted
		assertThat("messages of errorResponse.cause", 
				errorResponse.getInnerErrors().iterator().next().getMessage().getValue(),
				is("invalidProperty may not be null"));
	}

}
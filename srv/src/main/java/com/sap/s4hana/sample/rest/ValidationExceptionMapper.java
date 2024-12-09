package com.sap.s4hana.sample.rest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.sap.s4hana.sample.rest.ExceptionResponse.ErrorMessage;

/**
 * Custom exception mapper for CXF to be used to produce a nice HTTP error
 * response for a caught {@link ConstraintViolationException}: it lists violated
 * property paths and describes violations
 * 
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
	
	private static final Logger log = LoggerFactory.getLogger(ValidationExceptionMapper.class);
	
	/**
	 * The error code that will be used for each of
	 * {@link ExceptionResponse#getMessage()}
	 */
	public static final String CAUSE_ERROR_CODE = "ConstraintViolation";

	/**
	 * @return an error response with JSON body in {@link ExceptionResponse} format.
	 * 
	 * @see ValidationExceptionMapper#toExceptionResponse(ConstraintViolationException)
	 * @see ConstraintViolationException#getConstraintViolations()
	 * @see <a href="https://tools.ietf.org/html/rfc4918#section-11.2">RFC 4918,
	 *      section 11.2: 422 Unprocessable Entity</a>
	 */
	@Override
	public Response toResponse(ConstraintViolationException exception) {
		log.info("Map exception", exception);
		
		return Response
				.status(422) // Unprocessable Entity
				.type(MediaType.APPLICATION_JSON)
				.entity(toExceptionResponse(exception))
				.build();
	}

	/**
	 * @return {@link ExceptionResponse} which {@code code} is the class name of
	 *         {@code exception}, {@code message} is {@code exception}'s message
	 *         and {@code cause} contains the list of constraint violations from
	 *         {@code exception}.
	 */
	public static ExceptionResponse toExceptionResponse(ConstraintViolationException exception) {

		final List<ExceptionResponse> cause = Optional.ofNullable(exception.getConstraintViolations())
				.orElse(Collections.emptySet())
				.stream()
				.filter(Objects::nonNull)
				.map(ValidationExceptionMapper::getViolationMessage)
				.filter(Objects::nonNull)
				.distinct()
				.map(message -> ExceptionResponse.builder()
						.message(ErrorMessage.of(message))
						.code(CAUSE_ERROR_CODE)
						.build())
				.collect(Collectors.toList());
		
		final ExceptionResponse errorResponse = ExceptionResponse.builder()
				.message(ErrorMessage.of(exception.getMessage()))
				.code(exception.getClass().getName())
				.innerErrors(cause)
				.build();				
		
		return errorResponse;
	}

	@VisibleForTesting
	protected static String getViolationMessage(ConstraintViolation<?> violation) {
		return Stream.of(
					/* property path*/ Optional.ofNullable(violation.getPropertyPath()).map(Objects::toString).orElse(null), 
					/* violation message */ violation.getMessage())
				.filter(Objects::nonNull)
				.collect(Collectors.joining(" "));
	}

}
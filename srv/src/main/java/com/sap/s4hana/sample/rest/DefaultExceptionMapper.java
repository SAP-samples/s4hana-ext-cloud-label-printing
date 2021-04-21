package com.sap.s4hana.sample.rest;

import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

/**
 * Optional: Custom {@link ExceptionMapper} for CXF to be used with exception
 * types for which no other specific mapper exists.
 * <p>
 * Returns an error response with the default HTTP error code 500 and the JSON
 * body in {@link ExceptionResponse} format. ErrorResponse.code is the
 * exception's class name and ErrorResponse.message is the exception's message.
 * 
 */
@Slf4j
@Provider
public class DefaultExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(Exception exception) {
		log.info("Map exception", exception);

		final ExceptionResponse errorResponse = toErrorResponse(exception);

		return Response.status(Status.INTERNAL_SERVER_ERROR).
				type(MediaType.APPLICATION_JSON).
				entity(errorResponse).
				build();
	}

	protected static ExceptionResponse toErrorResponse(Throwable exception) {
		final ExceptionResponse errorResponse = ExceptionResponse.builder()
				.code(exception.getClass().getSimpleName())
				.message(ExceptionResponse.ErrorMessage.of(exception.getMessage()))
				.build();

		// Optional: fill inner errors recursively
		if (exception.getCause() != null) {
			errorResponse.setInnerErrors(Collections.singletonList(toErrorResponse(exception.getCause())));
		}

		return errorResponse;
	}

}

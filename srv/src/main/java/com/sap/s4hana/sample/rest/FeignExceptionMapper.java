package com.sap.s4hana.sample.rest;

import java.util.Collections;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpStatus;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

/**
 * Optional: Custom {@link ExceptionMapper} for CXF to be used with exception
 * types for which no other specific mapper exists.
 * <p>
 * Returns an error response with the default HTTP error code taken from the
 * original {@code FeignException} and the JSON body in
 * {@link ExceptionResponse} format.
 * <p>
 * ErrorResponse.code is the exception's class name and short error message.
 * <p>
 * ErrorResponse.message is the original service's response body in UTF-8.
 * 
 */
@Slf4j
@Provider
public class FeignExceptionMapper implements ExceptionMapper<FeignException> {

	@Override
	public Response toResponse(FeignException exception) {
		log.info("Map exception", exception);

		final ExceptionResponse errorResponse = toErrorResponse(exception);

		return Response.status(httpStatusFor(exception.status())).
				type(MediaType.APPLICATION_JSON).
				entity(errorResponse).
				build();
	}

	protected static ExceptionResponse toErrorResponse(FeignException exception) {
		final ExceptionResponse errorResponse = ExceptionResponse.builder()
				.code(exception.getClass().getSimpleName() + ": " + exception.getMessage()) 
				.message(ExceptionResponse.ErrorMessage.of(StringUtils.newStringUtf8(exception.content()))) // exception.contentUTF8() throws NPE if a response body is empty  
				.build();

		// Optional: fill inner errors recursively using default exception mapper
		if (exception.getCause() != null) {
			errorResponse.setInnerErrors(Collections.singletonList(DefaultExceptionMapper.toErrorResponse(exception.getCause())));
		}

		return errorResponse;
	}
	
	private static int httpStatusFor(int status) {
		if (status > 99 && status < 600) {
			return status;
		} else {
			return HttpStatus.SC_INTERNAL_SERVER_ERROR; // default status is 500
		}
	}
	
}
package com.sap.s4hana.sample.rest;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Optional: A DTO to represent the error response body in JSON. The error model
 * follows the error model for OData v2 Services
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // for Jackson
@JsonTypeName(value = "error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class ExceptionResponse {

	@Data
	@AllArgsConstructor(staticName = "of")
	@NoArgsConstructor // for Jackson
	public static class ErrorMessage {
		
		@JsonProperty("lang")
		private final String lang = "en";

		@JsonProperty("value")
		private String value;
		
	}

	@JsonProperty("code")
	private String code;

	@JsonProperty("message")
	private ExceptionResponse.ErrorMessage message;

	@Singular
	@JsonProperty("innererror")	
	@JsonInclude(Include.NON_EMPTY)
	private List<ExceptionResponse> innerErrors;

}

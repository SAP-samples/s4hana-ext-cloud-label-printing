package com.sap.s4hana.sample.render.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GetFormResponse {
	
	@JsonProperty("formName")
	private String formName;

	@JsonProperty("templates")
	private List<Template> templates;

	@Data
	@NoArgsConstructor
	@Builder
	@AllArgsConstructor
	public static class Template {

		@JsonProperty("templateName")
		private String templateName;

	}

}

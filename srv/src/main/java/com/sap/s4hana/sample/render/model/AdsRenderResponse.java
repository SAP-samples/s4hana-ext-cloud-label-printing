package com.sap.s4hana.sample.render.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body model object for {@code POST /ads.restapi/v1/adsRender/pdf}
 * method of Forms by Adobe REST API on SAP Business Technology Platform
 * 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsRenderResponse {

	@JsonProperty("fileName")
	private String fileName;
	
	@JsonProperty("fileContent")
	private String fileContent;
	
}

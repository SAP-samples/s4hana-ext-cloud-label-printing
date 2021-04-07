package com.sap.s4hana.sample.print.model;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.s4hana.sample.print.controller.PrintController;
import com.sap.s4hana.sample.render.model.AdsRenderRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Used in {@link PrintController#renderAndPrint(RenderAndPrintRequest)}
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // for Jackson
public class RenderAndPrintRequest {

	@Valid
	@NotNull
	@JsonProperty("renderRequest")
	private AdsRenderRequest renderRequest;
	
	/**
	 * {@link JsonIgnoreProperties} annotation makes sure that print contents are
	 * ignored even if specified explicitly as they are generated automatically by
	 * the ADS Service when rendering the PDF to be printed
	 */
	@Valid
	@NotNull
	@JsonProperty("printTask")
	private PrintTask printTask;
	
}

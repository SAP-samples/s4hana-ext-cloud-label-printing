package com.sap.s4hana.sample.print.model;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.s4hana.sample.validation.ForPrinting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @see <a href="https://api.sap.com/api/PRINTAPI/resource">Print Service API
 *      documentation on SAP API Business Hub</a>
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // for Jackson
public class PrintContent {

	@JsonProperty("objectKey")
	private String objectKey;

	@NotNull(groups = ForPrinting.class) @NotEmpty(groups = ForPrinting.class)
	@JsonProperty("documentName")
	private String documentName;
	
	@JsonProperty("countId")
	private Integer countId;

}

package com.sap.s4hana.sample.print.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @see <a href="https://api.sap.com/api/PRINTAPI/resource">Print Service API
 *      documentation on SAP API Business Hub</a>
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor // for Jackson
public class PrintQueue {

	@JsonProperty("qname")
	private String name;

	@JsonProperty("qdescription")
	private String description;

	@JsonProperty("qstatus")
	private String status;

	@JsonProperty("qformat")
	private String format;

	@JsonProperty("cleanupPrd")
	private BigDecimal cleanupPeriod;

	@JsonProperty("techUserName")
	private String technicalUserName;

}

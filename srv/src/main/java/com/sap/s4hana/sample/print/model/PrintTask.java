package com.sap.s4hana.sample.print.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sap.s4hana.sample.validation.ForPrinting;
import lombok.*;
import org.apache.bval.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * @see <a href="https://api.sap.com/api/PRINTAPI/resource">Print Service API
 *      documentation on SAP API Business Hub</a>
 *
 */
@Data
@Builder
@NoArgsConstructor // for Jackson
@AllArgsConstructor
public class PrintTask {

	public static final String PRINT_CONTENTS_JSON_PROPERTY = "printContents";

	@JsonProperty("numberOfCopies")
	private Integer numberOfCopies;

	@JsonProperty("username")
	private String username;

	@JsonProperty("qname")
	@NotNull @NotEmpty
	private String queueName;

	@JsonAlias(PRINT_CONTENTS_JSON_PROPERTY)
	@Valid @NotNull(groups = ForPrinting.class)
	@With
	private PrintContent printContents;

	@JsonProperty(PRINT_CONTENTS_JSON_PROPERTY)
	protected List<PrintContent> getPrintContensForJsonSerialization() {
		return Collections.singletonList(printContents);
	}

}


package com.sap.s4hana.sample.render.model;

import java.util.Map;

import javax.validation.constraints.NotNull;

import org.apache.bval.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sap.s4hana.sample.render.model.util.XmlBase64Serializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body model object for {@code POST /ads.restapi/v1/adsRender/pdf}
 * method of Forms by Adobe REST API on SAP Business Technology Platform
 * 
 * @see <a href=
 *      "https://help.sap.com/viewer/6d3eac5a9e3144a7b43932a1078c7628/Cloud/en-US/b3e65e471ba4454a9f4acf55d15590cb.html">Render
 *      a PDF Using a Template Entity from Storage</a>
 *      
 */
@Data
@Builder
@NoArgsConstructor // for Jackson
@AllArgsConstructor
public class AdsRenderRequest {

	@JsonProperty("xdpTemplate")
	@JsonAlias("templatePath")
	@NotNull @NotEmpty
	private String xdpTemplate;

	@JsonProperty("xmlData")
	@JsonAlias("printData")
	@JsonSerialize(using = XmlBase64Serializer.class)
	private Map<Object, Object> data;

}

package com.sap.s4hana.sample.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Custom JSON serializer for CXF
 * <p>
 * {@link Produces} and {@link Consumes} annotations are needed to override the
 * default JSON provider. Please check Apache CXF <a href=
 * "http://cxf.apache.org/docs/jax-rs.html#JAX-RS-CXF3.1.2ProviderSortingChanges">documentation</a>
 * for further details on how providers are sorted.
 * <p>
 * It can also be reused in Feign service provider if you want to use the same
 * Jackson configuration there.
 * 
 */
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class JsonProvider extends JacksonJsonProvider {

	@Override
	public ObjectMapper locateMapper(Class<?> type, MediaType mediaType) {
		final ObjectMapper om = super.locateMapper(type, mediaType);

		// recommended: resolve conflict between Jackson naming and Java Bean
		// naming for properties which names start with two or more upper-case
		// letters
		// see https://github.com/FasterXML/jackson-databind/issues/1824
		om.enable(MapperFeature.USE_STD_BEAN_NAMING);

		// optional: include only properties with non-null values
		om.setSerializationInclusion(Include.NON_NULL);

		// optional: pretty print
		om.enable(SerializationFeature.INDENT_OUTPUT);
		
		return om;
	}
	
}

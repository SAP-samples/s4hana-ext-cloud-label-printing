package com.sap.s4hana.sample.render.model.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Map;

import org.json.JSONObject;
import org.json.XML;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sap.s4hana.sample.render.model.AdsRenderRequest;

/**
 * Converts a dynamic JSON object represented as {@link Map} to XML and Base-64
 * encodes it, so that it can be passed to ADS Forms by Adobe REST API.
 * <p>
 * Used in {@link AdsRenderRequest}.
 * 
 * @see <a href=
 *      "https://help.sap.com/viewer/6d3eac5a9e3144a7b43932a1078c7628/Cloud/en-US/b3e65e471ba4454a9f4acf55d15590cb.html">Render
 *      a PDF Using a Template Entity from Storage</a>
 *
 */
public class XmlBase64Serializer extends JsonSerializer<Map<?, ?>> {

	@Override
	public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		final String xmlValue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
				XML.toString(new JSONObject(value));
			
		final String base64EncodedXml = Base64.encodeBase64String(xmlValue.getBytes(StandardCharsets.UTF_8));
			
		gen.writeString(base64EncodedXml);
	}
		
}

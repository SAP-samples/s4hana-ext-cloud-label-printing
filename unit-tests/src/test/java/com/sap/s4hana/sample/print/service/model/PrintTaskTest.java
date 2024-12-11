package com.sap.s4hana.sample.print.service.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.s4hana.sample.print.model.PrintContent;
import com.sap.s4hana.sample.print.model.PrintTask;
import com.sap.s4hana.sample.rest.JsonProvider;
import com.sap.s4hana.sample.validation.ForPrinting;
import org.junit.Test;
import validation.ValidationUtil;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class PrintTaskTest {
	
	@Test
	public void testJacksonUsesCustomPropertyNames() throws JsonMappingException, JsonProcessingException {
		// Given
		final String expectedQueueName = "expectedQueueName";
		final String json = String.format("{\"qname\" : \"%s\"}", expectedQueueName);
		
		final ObjectMapper om = new JsonProvider().locateMapper(PrintTask.class, null);
		
		// When
		final PrintTask testeeFromJson = om.readValue(json, PrintTask.class);
		
		// Then
		assertThat("queue name (qname in JSON)", testeeFromJson.getQueueName(), is(equalTo(expectedQueueName))); 
	}
	
	@Test
	public void testBeanValidationWithOnePrintContentElement() {
		final Set<ConstraintViolation<PrintTask>> violations = ValidationUtil.validate(validPrintTask());
		
		assertThat("there must be no violations", violations, is(empty()));
	}
	
	@Test
	public void testBeanValidationWithEmptyContentElements() {
		// Given a print task ...
		final PrintTask testee = validPrintTask();
		
		// ... that has invalid print contents
		testee.setPrintContents(PrintContent.builder().build());
		
		// When it is validated
		final Collection<String> violatedProperties = ValidationUtil.getViolatedPropertyPaths(testee, ForPrinting.class);
		
		// Then there must be a violation
		assertThat("violated properties", violatedProperties, contains("printContents.documentName"));
	}
	
	@Test
	public void testBeanValidationWithTotallyInvalidObject() {
		// Given a testee object that violates everything
		final PrintTask totallyInvalid = PrintTask.builder().build();
		
		// When it is validated
		final Collection<String> violatedProperties = ValidationUtil.getViolatedPropertyPaths(totallyInvalid);
				
		// Then there must be violations (except @NotNull on printContents - it's only valid for ForPrinting validation group)
		assertThat("violated properties", violatedProperties, containsInAnyOrder("queueName"));
	}
	
	@Test
	public void testBeanValidationWithTotallyInvalidObjectAndTwoValidationGroups() {
		// Given a testee object that violates both queueName (for the default
		// validation group) and printContents (for the ForPrinting validation group)
		final PrintTask totallyInvalid = PrintTask.builder().build();
		
		// When it is validated
		final Collection<String> violatedProperties = ValidationUtil.getViolatedPropertyPaths(totallyInvalid, ForPrinting.class);
				
		// Then there must violation from both validation groups because ForPrinting extends the Default group
		assertThat("violated properties", violatedProperties, containsInAnyOrder("queueName", "printContents"));
	}

	private static PrintTask validPrintTask() {
		return PrintTask.builder()
			.queueName("must not be null or empty")
			.printContents(validPrintContent())
			.build();
	}

	private static PrintContent validPrintContent() {
		return PrintContent.builder()
			.objectKey(UUID.randomUUID().toString())
			.countId(0)
			.documentName("must not be null or empty")
			.build();
	}

}

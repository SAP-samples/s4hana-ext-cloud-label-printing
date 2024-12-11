package com.sap.s4hana.sample.print.service.controller;

import com.sap.s4hana.sample.print.controller.PrintController;
import com.sap.s4hana.sample.print.model.PrintContent;
import com.sap.s4hana.sample.print.model.PrintTask;
import com.sap.s4hana.sample.print.service.PrintService;
import feign.Request;
import feign.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PrintControllerTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private PrintService printService;
	
	@Mock
	private PrintTask printTaskMock;
	
	@Mock
	private PrintContent printContentMock;
	
	@Captor
	private ArgumentCaptor<String> idCaptor;
	
	@InjectMocks
	PrintController testee;
	
	@Before
	public void setUp() {
		when(printTaskMock.getPrintContents()).thenReturn(printContentMock);

		when(printContentMock.getDocumentName()).thenReturn("documentName");
	}
	
	@Test
	public void testPrintFileWithValidContents() throws IOException {
		// Given
		final String expectedFileContents = "fileContents";
		String testUUID = UUID.randomUUID().toString();
		when(printContentMock.getObjectKey()).thenReturn(testUUID);

		Request request = Request.create(
			Request.HttpMethod.GET,
			"http://example.com/api/resource",
			new HashMap<>(),
			null,
			null
		);

		Response response = Response.builder()
			.status(200)
			.reason("OK")
			.headers(new HashMap<>())
			.body(testUUID.getBytes())
			.request(request)
			.build();
		when(printService.putPrintDocument(any())).thenReturn(testUUID);

		// When
		testee.printFile(printTaskMock, expectedFileContents.getBytes());
		
		// Then
		verify(printService).putPrintDocument(expectedFileContents.getBytes());
		verify(printContentMock).setObjectKey(idCaptor.capture());
		verify(printService).putPrintTask(eq(testUUID), eq(printTaskMock));
		
		try {
			UUID.fromString(idCaptor.getValue());
		} catch (Throwable t) {
			fail("print content should get a valid UUID");
		}
	}

}

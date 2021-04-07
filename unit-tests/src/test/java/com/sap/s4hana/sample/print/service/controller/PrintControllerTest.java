package com.sap.s4hana.sample.print.service.controller;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

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

import com.sap.s4hana.sample.print.controller.PrintController;
import com.sap.s4hana.sample.print.model.PrintContent;
import com.sap.s4hana.sample.print.model.PrintTask;
import com.sap.s4hana.sample.print.service.PrintService;
import com.sap.s4hana.sample.render.service.AdsService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PrintControllerTest {
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Mock
	private PrintService printService;
	
	@Mock
	private AdsService adsService;
	
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
		when(printTaskMock.getNumberOfCopies()).thenReturn(1);
		when(printTaskMock.getQueueName()).thenReturn("queueName");
		when(printTaskMock.getPrintContents()).thenReturn(printContentMock);
		
		when(printContentMock.getDocumentName()).thenReturn("documentName");
	}
	
	@Test
	public void testPrintFileWithValidContents() throws IOException {
		// Given
		final String expectedFileContents = "fileContents";
		when(printContentMock.getObjectKey()).thenReturn("uuid");
		when(printService.putPrintDocument(expectedFileContents.getBytes())).thenReturn(UUID.randomUUID().toString());
		
		// When
		testee.printFile(printTaskMock, expectedFileContents.getBytes());
		
		// Then
		verify(printContentMock).setObjectKey(idCaptor.capture());
		verify(printService).putPrintTask(eq("uuid"), eq(printTaskMock));
		
		try {
			UUID.fromString(idCaptor.getValue());
		} catch (Throwable t) {
			fail("print content should get a valid UUID");
		}
	}

}

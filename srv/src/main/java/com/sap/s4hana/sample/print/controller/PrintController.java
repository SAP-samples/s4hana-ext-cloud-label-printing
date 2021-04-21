package com.sap.s4hana.sample.print.controller;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.common.io.CharStreams;
import feign.Feign;
import org.apache.bval.constraints.NotEmpty;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.sap.s4hana.sample.print.model.PrintContent;
import com.sap.s4hana.sample.print.model.PrintQueue;
import com.sap.s4hana.sample.print.model.PrintTask;
import com.sap.s4hana.sample.print.model.RenderAndPrintRequest;
import com.sap.s4hana.sample.print.service.PrintService;
import com.sap.s4hana.sample.render.model.AdsRenderResponse;
import com.sap.s4hana.sample.render.service.AdsService;
import com.sap.s4hana.sample.validation.ForPrinting;

import lombok.extern.slf4j.Slf4j;

@Path(PrintController.PATH)
@Slf4j
public class PrintController {

	public static final String PATH = "/PrintQueues";

	@Inject
	private PrintService printService;

	@Inject
	private AdsService adsService;

	@GET
    public List<PrintQueue> getPrintQueues() {
		log.info("GET {}", PATH);

		return printService.getQueues();
	}

	@POST
    public void renderAndPrint(@Valid @NotNull RenderAndPrintRequest body) throws IOException {
		log.info("POST {} with request {}", PATH, body);

		// render a PDF file using SAP Forms service by Adobe
		final AdsRenderResponse adsRenderResponse = adsService.renderFormFromStorage(body.getRenderRequest());

		// upload the rendered PDF to the object store

		final byte[] decodedFile = Base64.decodeBase64(adsRenderResponse.getFileContent().getBytes(UTF_8));
		final String objectKey = CharStreams.toString(printService.putPrintDocument(decodedFile).body().asReader());

		final PrintTask printTask = body.getPrintTask();
		printTask.getPrintContents().setObjectKey(objectKey);
		printTask.getPrintContents().setCountId(0);

		printService.putPrintTask(objectKey, printTask);
	}

	@POST
	@Path("/Multipart")
	public void printFile(
			@Multipart(value = "printTask", type = MediaType.APPLICATION_JSON)
			@ConvertGroup(from = Default.class, to = ForPrinting.class) @Valid @NotNull
			PrintTask printTask,

			@Multipart(value = "file", type = MediaType.APPLICATION_OCTET_STREAM)
			@NotNull @NotEmpty
			byte[] body) throws IOException {

		log.info("Print PDF file with settings: {}", printTask);

		// upload the PDF file to the object store
		String objectKey = CharStreams.toString(printService.putPrintDocument(body).body().asReader());

		// create a print task using the uploaded PDF
		final PrintContent printContent = printTask.getPrintContents();
		printContent.setObjectKey(objectKey);
		printService.putPrintTask(printContent.getObjectKey(), printTask);
	}

}
package com.sap.s4hana.sample.print.controller;

import com.sap.s4hana.sample.print.model.PrintContent;
import com.sap.s4hana.sample.print.model.PrintQueue;
import com.sap.s4hana.sample.print.model.PrintTask;
import com.sap.s4hana.sample.print.model.RenderAndPrintRequest;
import com.sap.s4hana.sample.print.service.PrintService;
import com.sap.s4hana.sample.render.model.AdsRenderResponse;
import com.sap.s4hana.sample.render.service.AdsService;
import com.sap.s4hana.sample.validation.ForPrinting;
import lombok.extern.slf4j.Slf4j;
import org.apache.bval.constraints.NotEmpty;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
	public void renderAndPrint(@Valid @NotNull RenderAndPrintRequest body) {
		log.info("POST {} with request {}", PATH, body);

		// render a PDF file using SAP Forms by Adobe service
		final AdsRenderResponse adsRenderResponse = adsService.renderFormFromStorage(body.getRenderRequest());

		// upload the rendered PDF to the object store
		final byte[] decodedFile = Base64.decodeBase64(adsRenderResponse.getFileContent().getBytes(UTF_8));
		final String objectKey = printService.putPrintDocument(decodedFile);

		final PrintContent printContent = PrintContent.builder().
			countId(0).objectKey(objectKey).documentName(adsRenderResponse.getFileName()).build();

		final PrintTask printTask = body.getPrintTask();
		printTask.setPrintContents(printContent);

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
		final String objectKey = printService.putPrintDocument(body);

		// create a print task using the uploaded PDF
		final PrintContent printContent = printTask.getPrintContents();
		printContent.setObjectKey(objectKey);
		printService.putPrintTask(printContent.getObjectKey(), printTask);
	}

}
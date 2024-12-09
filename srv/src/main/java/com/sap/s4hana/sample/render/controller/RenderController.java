package com.sap.s4hana.sample.render.controller;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;

import com.sap.s4hana.sample.render.model.AdsRenderRequest;
import com.sap.s4hana.sample.render.model.AdsRenderResponse;
import com.sap.s4hana.sample.render.service.AdsService;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;

/**
 * REST API for preview and download functionality for UI
 *
 */
@Path(RenderController.PATH)
@Consumes("application/json")
@Slf4j
public class RenderController {
	
	static final String PATH = "/pdf/render";
	
	@Inject
	private AdsService adsService;
	
	@POST
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response renderPdf(@Valid @NotNull AdsRenderRequest renderRequest) throws IOException {
		log.info("POST {} with body {}", PATH, renderRequest);

	 	final AdsRenderResponse adsRenderResponse = adsService.renderFormFromStorage(renderRequest);
	 	
		final String base64EncodedPdfContent = adsRenderResponse.getFileContent();
		final byte[] renderedPdfBytes = Base64.decodeBase64(base64EncodedPdfContent.getBytes(UTF_8));

		return Response.ok(renderedPdfBytes, MediaType.APPLICATION_OCTET_STREAM)
				.header("Content-Disposition", "attachment; filename=\"renderedPdf.pdf\"" )
				.build();
    }
	
}

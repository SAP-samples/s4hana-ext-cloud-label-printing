package com.sap.s4hana.sample.print.service;

import com.sap.s4hana.sample.print.model.PrintQueue;
import com.sap.s4hana.sample.print.model.PrintTask;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Feign client for <a href="https://api.sap.com/api/PRINTAPI/resource">Print
 * Service API</a>
 *
 */
public interface PrintService  {

	/**
	 * Get print queue list.
	 *
	 */
	@GET
	@Path("/qm/api/v1/rest/queues")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<PrintQueue> getQueues();

	/**
	 * Create print task.
	 *
	 * @param itemId a valid GUID.
	 *
	 */
	@PUT
	@Path("/qm/api/v1/rest/print-tasks/{itemId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putPrintTask(@PathParam("itemId") String itemId, /* body */ PrintTask printTask);

	@POST
	@Path("/dm/api/v1/rest/print-documents")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	public String putPrintDocument(/* body */ byte[] doc);

}
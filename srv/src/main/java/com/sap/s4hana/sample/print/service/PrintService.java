package com.sap.s4hana.sample.print.service;

import java.util.List;

import com.sap.s4hana.sample.print.model.PrintQueue;
import com.sap.s4hana.sample.print.model.PrintTask;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import javax.ws.rs.Consumes;

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
	@RequestLine("GET /qm/api/v1/rest/queues")
	@Headers("Accept: application/json")
    public List<PrintQueue> getQueues();

	/**
	 * Create print task.
	 * 
	 * @param itemId a valid GUID.
	 * 
	 */
    @RequestLine("PUT /qm/api/v1/rest/print-tasks/{itemId}")
    @Headers({
    	"Content-Type: application/json",
    	"If-None-Match: *"
    	})
    public void putPrintTask(@Param("itemId") String itemId, /* body */ PrintTask printTask);
    
    @RequestLine("POST /dm/api/v1/rest/print-documents")
    @Headers({
    	"Content-Type: application/octet-stream",
    	"If-None-Match: *",
    	})
    public Response putPrintDocument(/* body */ byte[] doc);
    
}


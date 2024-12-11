package com.sap.s4hana.sample.outbounddelivery.controller;

import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.filter.FilterFunctionException;
import com.sap.cloud.sdk.service.prov.api.operations.Query;
import com.sap.cloud.sdk.service.prov.api.operations.Read;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;
import com.sap.s4hana.sample.outbounddelivery.service.OutboundDeliveryRepository;

/**
 * Exposes OData service {@link OutboundDeliveryService#SERVICE_NAME} using
 * SAP Cloud Application Programming model (CAP)
 *
 */
public class OutboundDeliveryService {
	
	public static final String SERVICE_NAME = "OutboundDeliveryService";
	
	public static final String HEADER_ENTITY_SET = "A_OutbDeliveryHeader";
	public static final String ITEM_ENTITY_SET = "A_OutbDeliveryItem";
	
	private final OutboundDeliveryRepository s4hanaService = new OutboundDeliveryRepository();

	@Read(entity = HEADER_ENTITY_SET, serviceName = SERVICE_NAME)
	public ReadResponse readDelivery(ReadRequest req) throws ODataException {
		return s4hanaService.read(req);
	}

	@Query(entity = HEADER_ENTITY_SET, serviceName = SERVICE_NAME)
	public QueryResponse queryDeliveries(QueryRequest req) throws FilterFunctionException, ODataException {
		return s4hanaService.query(req);
	}
	
	@Read(entity = ITEM_ENTITY_SET, serviceName = SERVICE_NAME)
	public ReadResponse readDeliveryItem(ReadRequest req) throws ODataException {
		return s4hanaService.read(req);
	}

	@Query(entity = ITEM_ENTITY_SET, serviceName = SERVICE_NAME)
	public QueryResponse queryDeliveryItems(QueryRequest req) throws FilterFunctionException, ODataException {
		return s4hanaService.query(req);
	}
	
	@Query(entity = ITEM_ENTITY_SET, sourceEntity = HEADER_ENTITY_SET, serviceName = SERVICE_NAME)
	public QueryResponse queryDeliveryItemsAsExpandFromDelivery(QueryRequest req) throws FilterFunctionException, ODataException {
		return s4hanaService.query(req);
	}
	
}

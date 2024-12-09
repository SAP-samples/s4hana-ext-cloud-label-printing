package com.sap.s4hana.sample.outbounddelivery.service;

import static com.sap.s4hana.sample.util.DestinationHelper.getHttpClient;

import java.util.Optional;
import java.util.stream.Collectors;

import com.sap.cloud.sdk.odatav2.connectivity.FilterExpression;
import com.sap.cloud.sdk.odatav2.connectivity.ODataException;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQuery;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryBuilder;
import com.sap.cloud.sdk.odatav2.connectivity.ODataQueryResult;
import com.sap.cloud.sdk.odatav2.connectivity.ODataType;
import com.sap.cloud.sdk.odatav2.connectivity.filter.FilterExpressionConverter;
import com.sap.cloud.sdk.odatav2.connectivity.filter.FilterFunctionException;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.OutboundDeliveryV2Service;
import com.sap.cloud.sdk.service.prov.api.request.QueryRequest;
import com.sap.cloud.sdk.service.prov.api.request.ReadRequest;
import com.sap.cloud.sdk.service.prov.api.response.QueryResponse;
import com.sap.cloud.sdk.service.prov.api.response.ReadResponse;

/**
 * Converts SAP Cloud Application Programming model (CAP) OData requests to
 * OData requests to SAP S/4HANA Cloud executed via destination on SAP Cloud
 * Platform {@link OutboundDeliveryRepository#S4HANA_DESTINATION_NAME}
 *
 */
public class OutboundDeliveryRepository {
	
	public static final String S4HANA_DESTINATION_NAME = "ErpQueryEndpoint";
	
	public static final String SERVICE_NAME = "OutboundDeliveryService";
	public static final String HEADER_ENTITY_SET = "A_OutbDeliveryHeader";

	public ReadResponse read(ReadRequest req) throws ODataException {
		ODataQuery readQuery = ODataQueryBuilder
				.withEntity(OutboundDeliveryV2Service.DEFAULT_SERVICE_PATH, req.getEntityName())
				.keys(req.getKeys())
				.select(req.getSelectProperties())
				.build();

		ODataQueryResult result = readQuery.execute(getHttpClient(S4HANA_DESTINATION_NAME));
		
		return ReadResponse.setSuccess().setData(result.asMap()).response();
	}

	public QueryResponse query(QueryRequest req) throws FilterFunctionException, ODataException {
		// $filter
		FilterExpression filter = FilterExpressionConverter.convertTo(req.getQueryExpression());
		
		// $expand: navigate from the source entity
		if (req.getSourceKeys() != null) {
			// note that this works only if target (this) entity has properties that keep
			// all keys of the source entity and have the same names as in the source entity
			final FilterExpression filterBySourceEntityProperties = req.getSourceKeys().entrySet().stream()
					.map(entry -> new FilterExpression(entry.getKey(), "eq", ODataType.of(entry.getValue())))
					.reduce(FilterExpression::and)
					.get();
			
			if (filter != null) {
				filter = filter.and(filterBySourceEntityProperties);
			} else {
				filter = filterBySourceEntityProperties;
			}
		}
		
		final ODataQueryBuilder queryBuilder = ODataQueryBuilder
				.withEntity(OutboundDeliveryV2Service.DEFAULT_SERVICE_PATH, req.getEntityName())
				.select(req.getSelectProperties())
				.skip(req.getSkipOptionValue())
				.top(req.getTopOptionValue())
				.filter(filter);

		// $inlinecount=allpages
		if (req.isInlineCountCall()) {
			queryBuilder.inlineCount();
		}
		
		// $orderby
		if (!req.getOrderByProperties().isEmpty()) {
			final String orderBy = req.getOrderByProperties().stream()
					.map(orderByExpression -> orderByExpression.getOrderByProperty()
							+ (orderByExpression.isDescending() ? " desc" : ""))
					.collect(Collectors.joining(","));
			
			queryBuilder.param("$orderby", orderBy);
		}
		
		ODataQueryResult result = queryBuilder.build().execute(getHttpClient(S4HANA_DESTINATION_NAME));
		
		return QueryResponse.setSuccess()
				.setData(result.asListOfMaps())
				.setSkipDone(true)
				.setInlineCount(Optional.ofNullable(result.getInlineCount()).orElse(-1))
				.response();
	}

}

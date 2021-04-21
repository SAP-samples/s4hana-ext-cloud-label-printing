using API_OUTBOUND_DELIVERY_SRV from '../edmx/API_OUTBOUND_DELIVERY_SRV';

service OutboundDeliveryService {

	@cds.persistence.skip
	entity A_OutbDeliveryHeader 
		as projection on API_OUTBOUND_DELIVERY_SRV.A_OutbDeliveryHeaderType 
			excluding {to_DeliveryDocumentPartner};
			
	@cds.persistence.skip
	entity A_OutbDeliveryItem 
		as projection on API_OUTBOUND_DELIVERY_SRV.A_OutbDeliveryItemType 
			excluding {to_DocumentFlow, to_SerialDeliveryItem};
			
}

package org.mifos.sms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.sms.data.SmsOutboundMessageResponseData;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.domain.SmsOutboundMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReadSmsOutboundMessageServiceImpl implements ReadSmsOutboundMessageService {
	private final SmsOutboundMessageRepository smsOutboundMessageRepository;
	
	@Autowired
	public ReadSmsOutboundMessageServiceImpl(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository;
	}
	
	@Override
	public Collection<SmsOutboundMessageResponseData> findAll(List<Long> idList, String mifosTenantIdentifier) {
	    Collection<SmsOutboundMessageResponseData> smsOutboundMessageResponseDataCollection = new ArrayList<>();
	    Collection<SmsOutboundMessage> smsOutboundMessageCollection = this.smsOutboundMessageRepository
	            .findByIdInAndMifosTenantIdentifier(idList, mifosTenantIdentifier);
	    
	    for (SmsOutboundMessage smsOutboundMessage : smsOutboundMessageCollection) {
	        
	        Long id = smsOutboundMessage.getInternalId();
            Long externalId = smsOutboundMessage.getId();
            LocalDate addedOnDate = smsOutboundMessage.getAddedOnDate();
            LocalDate deliveredOnDate = smsOutboundMessage.getDeliveredOnDate();
            Integer deliveryStatus = smsOutboundMessage.getDeliveryStatus();
	        
	        SmsOutboundMessageResponseData smsOutboundMessageResponseData = SmsOutboundMessageResponseData.getInstance(id, externalId, 
	                addedOnDate, deliveredOnDate, deliveryStatus, false, "");
	        
	        smsOutboundMessageResponseDataCollection.add(smsOutboundMessageResponseData);
	    }
	    
		return smsOutboundMessageResponseDataCollection;
	}
}

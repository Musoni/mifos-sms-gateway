package org.mifos.sms.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mifos.sms.data.SmsOutboundMessageResponseData;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.domain.SmsOutboundMessageRepository;
import org.mifos.sms.helper.GlobalConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class WriteSmsOutboundMessageServiceImpl implements WriteSmsOutboundMessageService {
	private final SmsOutboundMessageRepository smsOutboundMessageRepository;
	
	@Autowired
	public WriteSmsOutboundMessageServiceImpl(SmsOutboundMessageRepository smsOutboundMessageRepository) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository;
	}

	@Transactional
	@Override
	public List<SmsOutboundMessageResponseData> create(List<SmsOutboundMessage> smsOutboundMessages) {
		
		Iterator<SmsOutboundMessage> iterator = smsOutboundMessages.iterator();
		List<SmsOutboundMessageResponseData> smsOutboundMessagesResponseData = new ArrayList<>();
		
		while(iterator.hasNext()) {
			SmsOutboundMessage smsOutboundMessage = iterator.next();
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(GlobalConstants.SIMPLE_DATE_FORMAT);
			
			// check if message object has values for mandatory parameters
			if(!StringUtils.isEmpty(smsOutboundMessage.getInternalId()) && 
					!StringUtils.isEmpty(smsOutboundMessage.getMifosTenantIdentifier()) && 
					!StringUtils.isEmpty(smsOutboundMessage.getSourceAddress()) && 
					!StringUtils.isEmpty(smsOutboundMessage.getMobileNumber()) && 
					!StringUtils.isEmpty(smsOutboundMessage.getMessage())) {
				
				// set "addedOnDate" to today
				smsOutboundMessage.setAddedOnDate(new Date());
				
				// insert to the sms outbound message into the "smsOutboundMessage" table
				smsOutboundMessageRepository.save(smsOutboundMessage);
				
				// add a response data object to the "SmsOutboundMessageResponseData" list
				smsOutboundMessagesResponseData.add(SmsOutboundMessageResponseData.getInstance(smsOutboundMessage.getInternalId(), 
						smsOutboundMessage.getId(), simpleDateFormat.format(smsOutboundMessage.getAddedOnDate()), null, 
						smsOutboundMessage.getDeliveryStatus(), false, null));
			}
			
			else {
				// validation errors exist
				// add a response data object to the "SmsOutboundMessageResponseData" list
				smsOutboundMessagesResponseData.add(SmsOutboundMessageResponseData.getInstance(smsOutboundMessage.getInternalId(), 
						smsOutboundMessage.getId(), null, null, null, true, "Missing value for one or more mandatory parameters"));
			}
		}
		
		return smsOutboundMessagesResponseData;
	}
}

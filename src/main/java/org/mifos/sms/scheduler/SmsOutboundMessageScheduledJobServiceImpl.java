package org.mifos.sms.scheduler;

import java.util.Date;
import java.util.List;

import org.mifos.sms.gateway.infobip.SmsGatewayHelper;
import org.mifos.sms.gateway.infobip.SmsGatewayImpl;
import org.mifos.sms.gateway.infobip.SmsGatewayMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.mifos.sms.domain.SmsMessageStatusType;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.domain.SmsOutboundMessageRepository;

@Service
public class SmsOutboundMessageScheduledJobServiceImpl implements SmsOutboundMessageScheduledJobService {
    private final SmsOutboundMessageRepository smsOutboundMessageRepository;
    private final SmsGatewayImpl smsGatewayImpl;
    private final SmsGatewayHelper smsGatewayHelper;
    
    @Autowired
    public SmsOutboundMessageScheduledJobServiceImpl(SmsOutboundMessageRepository smsOutboundMessageRepository,
    		SmsGatewayHelper smsGatewayHelper,
    		SmsGatewayImpl smsGatewayImpl) {
    	this.smsOutboundMessageRepository = smsOutboundMessageRepository;
    	this.smsGatewayHelper = smsGatewayHelper;
    	this.smsGatewayImpl = smsGatewayImpl;
    	this.smsGatewayHelper.connectAndBindSession();
    }

	@Override
	@Transactional
	@Scheduled(fixedDelay = 60000)
	public void sendMessages() {
		// check if the scheduler is enabled
		if(smsGatewayHelper.smsGatewayConfiguration.getEnableOutboundMessageScheduler()) {
			
			if(smsGatewayHelper.isConnected) {
				Pageable pageable = new PageRequest(0, getMaximumNumberOfMessagesToBeSent());
				List<SmsOutboundMessage> smsOutboundMessages = smsOutboundMessageRepository.findByDeliveryStatus(SmsMessageStatusType.PENDING.getValue(), pageable);
				
				// only proceed if there are pending messages
		        if(smsOutboundMessages.size() > 0) {
		            
		            for(SmsOutboundMessage smsOutboundMessage : smsOutboundMessages) {
		                SmsGatewayMessage smsGatewayMessage = new SmsGatewayMessage(smsOutboundMessage.getId(), 
		                        smsOutboundMessage.getExternalId(), smsOutboundMessage.getSourceAddress(), 
		                        smsOutboundMessage.getMobileNumber(), smsOutboundMessage.getMessage());
		                
		                // send message to SMS message gateway
		                smsGatewayMessage = smsGatewayImpl.sendMessage(smsGatewayMessage);
		                
		                // update the "submittedOnDate" property of the SMS message in the DB
		                smsOutboundMessage.setSubmittedOnDate(new Date());
		                
		                // check if the returned SmsGatewayMessage object has an external ID
		                if(!StringUtils.isEmpty(smsGatewayMessage.getExternalId())) {
		                    
		                    // update the external ID of the SMS message in the DB
		                    smsOutboundMessage.setExternalId(smsGatewayMessage.getExternalId());
		                    
		                    // update the status of the SMS message in the DB
		                    smsOutboundMessage.setDeliveryStatus(SmsMessageStatusType.SENT);
		                }
		                
		                else {
		                    // update the status of the SMS message in the DB
		                    smsOutboundMessage.setDeliveryStatus(SmsMessageStatusType.FAILED);
		                }
		                
		                smsOutboundMessageRepository.save(smsOutboundMessage);
		            }
		        }
			}
			
			else {
				// reconnect
				smsGatewayHelper.reconnectAndBindSession();
			}
		}
	}
	
	/** 
	 * Get the maximum number of messages to be sent to the SMS gateway
	 * 
	 * TODO this should be configurable, add to c_configuration
	 **/
	private int getMaximumNumberOfMessagesToBeSent() {
		return 5000;
	}
}

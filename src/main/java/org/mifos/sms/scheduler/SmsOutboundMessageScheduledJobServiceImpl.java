package org.mifos.sms.scheduler;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.mifos.sms.gateway.infobip.SmsGatewayDeliveryReport;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mifos.sms.domain.SmsMessageStatusType;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.domain.SmsOutboundMessageRepository;

@Service
public class SmsOutboundMessageScheduledJobServiceImpl implements SmsOutboundMessageScheduledJobService {
	private static final Logger logger = LoggerFactory.getLogger(SmsOutboundMessageScheduledJobService.class);
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
				
				// send messages to infobip
				final boolean sendMessages = this.sendMessages(smsOutboundMessages);
		        
		        // check if there are any SMS gateway delivery reports
		        if(sendMessages && (smsGatewayHelper.smsGatewayDeliveryReports.size() > 0)) {
		        	// go ahead and process the delivery reports
		        	processDeliveryReports();
		        }
			}
			
			else {
				// reconnect
				smsGatewayHelper.reconnectAndBindSession();
			}
		}
	}
	
	/** 
	 * send SMS messages to the infobip SMS gateway and update the delivery status, 
	 * submitted on date and external ID of the {@link SmsOutboundMessage} entities
	 * 
	 * @return boolean true 
	 **/
	private boolean sendMessages(List<SmsOutboundMessage> smsOutboundMessages) {
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
	    
        return true;
	}
	
	/** 
	 * Process delivery reports, update the status, delivery date, etc of the SMS messages
	 * 
	 * @return void
	 **/
	private void processDeliveryReports() {
	    // when iterating over a synchronized list, we need to synchronize access to the synchronized list
	    synchronized(smsGatewayHelper.smsGatewayDeliveryReports) {
    		Iterator<Object> iterator = smsGatewayHelper.smsGatewayDeliveryReports.iterator();
    		
    		while(iterator.hasNext()) {
    			// get the next iteration
    			SmsGatewayDeliveryReport smsGatewayDeliveryReport = (SmsGatewayDeliveryReport) iterator.next();
    			
    			// get the SmsMessage object from the DB
    			SmsOutboundMessage smsOutboundMessage = smsOutboundMessageRepository.findByExternalId(smsGatewayDeliveryReport.getExternalId());
    			
    			if(smsOutboundMessage != null) {
    				// update the status of the SMS message
    				smsOutboundMessage.setDeliveryStatus(smsGatewayDeliveryReport.getStatus());
    				
    				switch(smsGatewayDeliveryReport.getStatus()) {
    					case DELIVERED:
    						// update the delivery date of the SMS message
    						smsOutboundMessage.setDeliveredOnDate(smsGatewayDeliveryReport.getDoneDate());
    						break;
    						
    					default:
    						// at the moment, do nothing
    						break;
    				}
    				
    				// save the "SmsOutboundMessage" entity
    				smsOutboundMessageRepository.save(smsOutboundMessage);
    				
    				// log success message
    				logger.info("SMS message with external ID '" + smsOutboundMessage.getExternalId() + "' successfully updated. Status set to: " + smsOutboundMessage.getDeliveryStatus().toString());
    			}
    			
    			// the report has been processed, so remove from the list of delivery reports
    			iterator.remove();
    		}
	    }
	}
	
	/** 
	 * Get the maximum number of messages to be sent to the SMS gateway
	 * 
	 * TODO this should be configurable, add to c_configuration
	 **/
	private int getMaximumNumberOfMessagesToBeSent() {
		return 100;
	}
}

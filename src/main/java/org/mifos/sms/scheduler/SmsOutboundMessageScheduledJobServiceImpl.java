package org.mifos.sms.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.mifos.sms.gateway.infobip.SmsGatewayHelper;
import org.mifos.sms.gateway.infobip.SmsGatewayImpl;
import org.mifos.sms.gateway.infobip.SmsGatewayMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.mifos.sms.domain.SmsMessageStatusType;
import org.mifos.sms.domain.SmsOutboundMessage;
import org.mifos.sms.domain.SmsOutboundMessageRepository;

@Service
public class SmsOutboundMessageScheduledJobServiceImpl implements SmsOutboundMessageScheduledJobService {
    private final SmsOutboundMessageRepository smsOutboundMessageRepository;
    private final SmsGatewayImpl smsGatewayImpl;
    private final SmsGatewayHelper smsGatewayHelper;
    private final static Logger logger = LoggerFactory.getLogger(SmsOutboundMessageScheduledJobServiceImpl.class);
    
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
		if(smsGatewayHelper.smsGatewayConfiguration.getEnableOutboundMessageScheduler() && 
		        this.isSchedulerEnabledInSmsGatewayPropertiesFile()) {
			
			if(smsGatewayHelper.isConnected) {
				Pageable pageable = new PageRequest(0, getMaximumNumberOfMessagesToBeSent());
				List<SmsOutboundMessage> smsOutboundMessages = smsOutboundMessageRepository.findByDeliveryStatus(SmsMessageStatusType.PENDING.getValue(), pageable);
				
				// only proceed if there are pending messages
		        if((smsOutboundMessages != null) && smsOutboundMessages.size() > 0) {
		            
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
		
		else {
		    //logger.warn("SEND MESSAGES SCHEDULER IS DISABLED ON THIS SERVER INSTANCE");
		}
	}
	
	/**
	 * check if the scheduler.enables property in the "/var/lib/tomcat7/conf/sms-gateway.properties" file is set to true
     * 
     * @return boolean true if value is true, else false
	 */
	private boolean isSchedulerEnabledInSmsGatewayPropertiesFile() {
	    // scheduler is disabled by default
        boolean isEnabled = false;
        Properties quartzProperties = new Properties();
        InputStream quartzPropertiesInputStream = null;
        File catalinaBaseConfDirectory = null;
        File quartzPropertiesFile = null;
        String scheduleDotEnablePropertyValue = null;
        
        try {
            // create a new File instance for the catalina base conf directory
            catalinaBaseConfDirectory = new File(System.getProperty("catalina.base"), "conf");
            
            // create a new File instance for the quartz properties file
            quartzPropertiesFile = new File(catalinaBaseConfDirectory, "sms-gateway.properties");
            
            // create file inputstream to the quartz properties file
            quartzPropertiesInputStream = new FileInputStream(quartzPropertiesFile);
            
            // read property list from input stream 
            quartzProperties.load(quartzPropertiesInputStream);
            
            scheduleDotEnablePropertyValue = quartzProperties.getProperty("scheduler.enabled");
            
            // make sure it isn't blank, before trying to parse the string as boolean
            if (StringUtils.isNoneBlank(scheduleDotEnablePropertyValue)) {
                isEnabled = Boolean.parseBoolean(scheduleDotEnablePropertyValue); 
            }
        } 
        
        catch (FileNotFoundException ex) {
            // isEnabled = true;
        }

        catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }


        finally {
            if (quartzPropertiesInputStream != null) {
                try {
                    quartzPropertiesInputStream.close();
                } 
                
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        
        return isEnabled;
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

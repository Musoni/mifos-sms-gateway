package org.mifos.sms.scheduler;

/** 
 * Scheduled job service interface for outbound SMS messages 
 * 
 * @author Emmanuel Nnaa
 **/
public interface SmsOutboundMessageScheduledJobService {
	
	/** 
	 * sends a batch of outbound SMS messages to the SMS gateway 
	 **/
	public void sendMessages();
	
	/**
	 * process incoming sms gateway delivery reports
	 */
	public void processSmsGatewayDeliveryReports();
}

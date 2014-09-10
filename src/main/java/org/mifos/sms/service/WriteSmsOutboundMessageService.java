package org.mifos.sms.service;

import java.util.List;

import org.mifos.sms.data.SmsOutboundMessageResponseData;
import org.mifos.sms.domain.SmsOutboundMessage;

/** 
 * Add Outbound SMS messages interface 
 * 
 * @author Emmanuel Nnaa
 **/
public interface WriteSmsOutboundMessageService {
	
	/** 
	 * add a new outbound SMS message entry to the smsOutboundMessage table 
	 **/
	public List<SmsOutboundMessageResponseData> create(final List<SmsOutboundMessage> smsOutboundMessages);
}

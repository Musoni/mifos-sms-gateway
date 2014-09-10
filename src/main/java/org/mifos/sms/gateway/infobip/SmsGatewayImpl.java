package org.mifos.sms.gateway.infobip;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * Infobip SMS gateway services is use in sending out sms messages.
 * More information - http://www.infobip.com/messaging/wholesale/apis/ 
 **/
@Service
public class SmsGatewayImpl implements SmsGateway {
	private final SmsGatewayHelper smsGatewayHelper;
	
	/** 
	 * SmsGatewayImpl constructor
	 * 
	 * @return void 
	 **/
	@Autowired
	public SmsGatewayImpl(final SmsGatewayHelper smsGatewayHelper) {
		this.smsGatewayHelper = smsGatewayHelper;
	}

	@Override
	public List<SmsGatewayMessage> sendMessages(List<SmsGatewayMessage> smsGatewayMessages) {
		List<SmsGatewayMessage> sentSmsGatewayMessages = new ArrayList<>(smsGatewayMessages.size());
		
		if(smsGatewayMessages.size() > 0) {
			for(SmsGatewayMessage smsGatewayMessage : smsGatewayMessages) {
				sentSmsGatewayMessages.add(smsGatewayHelper.submitShortMessage(smsGatewayMessage));
			}
		}
		
		return sentSmsGatewayMessages;
	}

	@Override
	public SmsGatewayMessage sendMessage(SmsGatewayMessage smsGatewayMessage) {
		return smsGatewayHelper.submitShortMessage(smsGatewayMessage);
	}
}

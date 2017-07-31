package org.mifos.sms.gateway.infobip;

import java.util.ArrayList;
import java.util.List;

import org.mifos.sms.smpp.session.SmppSessionFactoryBean;

/** 
 * Infobip SMS gateway services is use in sending out sms messages.
 * More information - http://www.infobip.com/messaging/wholesale/apis/ 
 **/
public class SmsGatewayImpl implements SmsGateway {
    private final SmppSessionFactoryBean smppSessionFactoryBean;
	
	/** 
	 * SmsGatewayImpl constructor
	 * 
	 * @return void 
	 **/
	public SmsGatewayImpl(final SmppSessionFactoryBean smppSessionFactoryBean) {
		this.smppSessionFactoryBean = smppSessionFactoryBean;
	}

	@Override
	public List<SmsGatewayMessage> sendMessages(List<SmsGatewayMessage> smsGatewayMessages) {
		List<SmsGatewayMessage> sentSmsGatewayMessages = new ArrayList<>(smsGatewayMessages.size());
		
		if(smsGatewayMessages.size() > 0) {
			for(SmsGatewayMessage smsGatewayMessage : smsGatewayMessages) {
				sentSmsGatewayMessages.add(smppSessionFactoryBean.submitShortMessage(smsGatewayMessage));
			}
		}
		
		return sentSmsGatewayMessages;
	}

	@Override
	public SmsGatewayMessage sendMessage(SmsGatewayMessage smsGatewayMessage) {
		return smppSessionFactoryBean.submitShortMessage(smsGatewayMessage);
	}
}

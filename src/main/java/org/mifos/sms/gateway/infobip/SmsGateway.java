package org.mifos.sms.gateway.infobip;

import java.util.List;

public interface SmsGateway {
	/** 
     * send batch of SMS messages to SMS gateway
     * 
     * @param smsGatewayMessages List of SmsGatewayMessage objects
     * @param session SMPPSession object
     * 
     * @return List of SmsGatewayMessage objects
     **/
    public List<SmsGatewayMessage> sendMessages(List<SmsGatewayMessage> smsGatewayMessages);
    
    /** 
     * Send SMS message to SMS gateway 
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * @param session SMPPSession object
     * 
     * @return SmsGatewayMessage object
     **/
    public SmsGatewayMessage sendMessage(SmsGatewayMessage smsGatewayMessage);
}

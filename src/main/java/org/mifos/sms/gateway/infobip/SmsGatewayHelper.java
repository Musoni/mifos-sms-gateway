package org.mifos.sms.gateway.infobip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.mifos.sms.data.ConfigurationData;
import org.mifos.sms.domain.SmsMessageStatusType;
import org.mifos.sms.gateway.infobip.SmsGatewayMessage;
import org.mifos.sms.service.ReadConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * Helper class for the SMS message gateway 
 * 
 * @author Emmanuel Nnaa
 **/
@Service
public class SmsGatewayHelper {
	private final ReadConfigurationService readConfigurationService;
	private static final Logger logger = LoggerFactory.getLogger(SmsGatewayHelper.class);
	private long reconnectInterval = 10000L; // 10 seconds
	private SMPPSession session = null;
	// the list is wrapped using "Collections.synchronizedList" to prevent accidental unsynchronized access to the list
	// @see http://docs.oracle.com/javase/7/docs/api/java/util/ArrayList.html
	public final List<Object> smsGatewayDeliveryReports = Collections.synchronizedList(new ArrayList<>());
	public Boolean isConnected = false;
	public Boolean isReconnecting = false;
	public SmsGatewayConfiguration smsGatewayConfiguration;
	public Boolean reconnect = true;
    
    @Autowired
    public SmsGatewayHelper(ReadConfigurationService readConfigurationService) {
    	this.readConfigurationService = readConfigurationService;
    	Collection<ConfigurationData> configurationDataCollection = this.readConfigurationService.findAll();
    	
    	// get an instance of the SmsGatewayConfiguration class
    	smsGatewayConfiguration = new SmsGatewayConfiguration(configurationDataCollection);
    }
    
    /** 
     * @return currently active SMPPSession object 
     **/
    public SMPPSession getSession() {
    	return session;
    }
    
    /** 
     * @return the system mode (production/development) 
     **/
    public final Boolean developmentMode() {
    	return smsGatewayConfiguration.getDevelopmentMode();
    }
    
    /** 
     * @return identifier of the system requesting a bind to the SMSC. 
     **/
    public final String systemId() {
    	String systemId = SMPPServerSimulatorConfiguration.SMS_GATEWAY_SYSTEM_ID.getValue().toString();
    	
    	if(!developmentMode()) {
    		systemId = smsGatewayConfiguration.getSystemId();
    	}
    	
    	return systemId;
    }
    
    /** 
     * @return SMS gateway host name 
     **/
    public final String host() {
    	String host = SMPPServerSimulatorConfiguration.SMS_GATEWAY_HOSTNAME.getValue().toString();
    	
    	if(!developmentMode()) {
    		host = smsGatewayConfiguration.getHostname();
    	}
    	
    	return host;
    }
    
    /** 
     * @return SMPP connection port number 
     **/
    public final Integer port() {
    	Integer port = Integer.parseInt(SMPPServerSimulatorConfiguration.SMS_GATEWAY_PORT.getValue().toString());
    	
    	if(!developmentMode()) {
    		port = smsGatewayConfiguration.getPortNumber();
    	}
    	
    	return port;
    }
    
    /** 
     * @return SMPP connection password
     **/
    public final String password() {
    	String password = SMPPServerSimulatorConfiguration.SMS_GATEWAY_PASSWORD.getValue().toString();
    	
    	if(!developmentMode()) {
    		password = smsGatewayConfiguration.getPassword();
    	}
    	
    	return password;
    }
    
    /** 
     * @return bind type used for the connection to the SMS gateway 
     **/
    public final BindType bindType() {
    	return BindType.BIND_TRX;
    }
    
    /** 
     * @return type of system requesting the bind 
     **/
    public final String systemType() {
    	String systemType = null;
    	
    	if(developmentMode()) {
    		systemType = "cp";
    	}
    	
        return systemType;
    }
    
    /** 
     * @return Type of Number for use in routing Delivery Receipts 
     **/
    public final TypeOfNumber addrTon() {
        return TypeOfNumber.UNKNOWN;
    }
    
    /** 
     * @return Numbering Plan Identity for use in routing Delivery Receipts.  
     **/
    public final NumberingPlanIndicator addrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return Address range for use in routing short messages and Delivery Receipts to an ESME. 
     **/
    public final String addressRange() {
        return null;
    }
    
    /** 
     * @return SMS Application service associated with the message 
     **/
    public final String serviceType() {
    	return "CMT";
    }
    
    /** 
     * @return type of number (TON) to be used in the SME (Short Message Entity) originator address parameters
     **/
    public final TypeOfNumber sourceAddrTon() {
        return TypeOfNumber.ALPHANUMERIC;
    }
    
    /** 
     * @return NPI (numeric plan indicator) to be used in the SME (Short Message Entity) originator address parameters
     **/
    public final NumberingPlanIndicator sourceAddrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return type of number (TON) to be used in the SME (Short Message Entity) destination address parameters 
     **/
    public final TypeOfNumber destAddrTon() {
        return TypeOfNumber.INTERNATIONAL;
    }
    
    /** 
     * @return numeric plan indicator (NPI) to be used in the SME (Short Message Entity) destination address parameters 
     **/
    public final NumberingPlanIndicator destAddrNpi() {
        return NumberingPlanIndicator.UNKNOWN;
    }
    
    /** 
     * @return a new instance of the ESMClass() class 
     **/
    public final ESMClass esmClass() {
        return new ESMClass();
    }
    
    /** 
     * @return GSM Protocol ID 
     **/
    public final byte protocolId() {
        return (byte)0;
    }
    
    /** 
     * @return priority level to be assigned to the short message 
     **/
    public final byte priorityFlag() {
        return (byte)0;
    }
    
    /** 
     * @return date and time (relative to GMT) at which delivery of the message must be attempted 
     **/
    public final String scheduledDeliveryTime() {
    	return null;
    }
    
    /** 
     * @return expiration time of this message specified as an absolute date and time of expiry 
     **/
    public final String validityPeriod() {
        return null;
    }
    
    /** 
     * @return new instance of the RegisteredDelivery() class which will indicating if the message is a registered short
     *  message and thus if a Delivery Receipt is required upon the message attaining a final state
     **/
    public final RegisteredDelivery registeredDelivery() {
        return new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE);
    }
    
    /** 
     * @return indication if submitted message should replace an existing message between the specified source and destination 
     **/
    public final byte replaceIfPresentFlag() {
        return (byte)0;
    }
    
    /** 
     * @return GSM Data-Coding-Scheme 
     **/
    public final DataCoding dataCoding() {
        // ignore any IDE warnings
        return new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
    }
    
    /** 
     * @return default short message to send, by providing an index into the table of Predefined Messages set up by the SMSC administrator.
     **/
    public final byte smDefaultMsgId() {
        return (byte)0;
    }
    
    /** 
     * create new SMPPSession object, connect and bind SMPP session 
     * 
     * @return {@link SMPPSession} object
     **/
    public final SMPPSession connectAndBindSession() {
    	session = new SMPPSession();
    	
    	try {
            session.connectAndBind(host(), port(), new BindParameter(bindType(), systemId(), 
            		password(), systemType(), addrTon(), addrNpi(), addressRange()));
            
            session.addSessionStateListener(new SessionStateListenerImpl());
            session.setMessageReceiverListener(new MessageReceiverListenerImpl());
        } 
    	
    	catch (IOException e) {
    		// log error
    		logger.error("Failed to connect and bind to host");
        }
    	
    	// isConnect is set to true if the last reading valid PDU from remote host is greater than 0, else false
    	isConnected = (session.getLastActivityTimestamp() > 0);
    	
    	return session;
    }
    
    /** 
     * Unbind and close open SMPP session
     * 
     * @return None
     **/
    public final void unbindAndCloseSession() {
    	session.unbindAndClose();
    }
    
    /** 
     * Send the SMS message to the SMS gateway
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * @param session SMPPSession object
     * 
     * @return {@link SmsGatewayMessage} object
     **/
    public SmsGatewayMessage submitShortMessage(SmsGatewayMessage smsGatewayMessage) {
        String messageId = "";
        String message = smsGatewayMessage.getMessage();
        String sourceAddress = smsGatewayMessage.getSourceAddress();
        String mobileNumber = smsGatewayMessage.getMobileNumber();
        
        try {
        	messageId = session.submitShortMessage(serviceType(), sourceAddrTon(), 
                    sourceAddrNpi(), sourceAddress, destAddrTon(), 
                    destAddrNpi(), mobileNumber, esmClass(), protocolId(), 
                    priorityFlag(), scheduledDeliveryTime(), validityPeriod(), 
                    registeredDelivery(), replaceIfPresentFlag(), dataCoding(), 
                    smDefaultMsgId(), message.getBytes());
            
            logger.info("Message sent to " + mobileNumber +  ", SMS gateway message ID is " + messageId);
        } 
        
        catch (PDUException e) {
            // Invalid PDU parameter
            logger.error("Invalid PDU parameter");
        } 
        
        catch (ResponseTimeoutException e) {
            // Response timeout
            logger.error("Response timeout");
        } 
        
        catch (InvalidResponseException e) {
            // Invalid response
            logger.error("Receive invalid response");
        } 
        
        catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            logger.error("Receive negative response");
        } 
        
        catch (IOException e) {
            logger.error("IO error occur");
        }
        
        return new SmsGatewayMessage(smsGatewayMessage.getId(), messageId, sourceAddress, mobileNumber, message);
    }
    
    /**
     * Reconnect session after specified interval.
     * 
     * @param timeInMillis is the interval.
     * @return None
     */
    public void reconnectAndBindSession() {
    	if(!isReconnecting && reconnect) {
    		new Thread() {
                @Override
                public void run() {
                    logger.info("Schedule reconnect after " + reconnectInterval + " millis");
                    
                    try {
                        Thread.sleep(reconnectInterval);
                    } 
                    
                    catch (InterruptedException e) {}
                    
                    int attempt = 0;
                    
                    while(session == null || session.getSessionState().equals(SessionState.CLOSED)) {
                    	// it is reconnecting
                    	isReconnecting = true;
                    	
                    	logger.info("Reconnecting attempt #" + (++attempt) + "...");
                    	
                    	// create a new connection and bind the session
                        session = connectAndBindSession();
                        
                        // wait for #reconnectInterval
                        try { 
                        	Thread.sleep(reconnectInterval); 
                    	} 
                        
                        catch (InterruptedException ee) {}
                    }
                    
                    isReconnecting = false;
                }
            }.start();
    	}
    }
    
    /**
     * This class will receive the notification from {@link SMPPSession} for the
     * state changes. It will schedule to re-initialize session.
     **/
    private class SessionStateListenerImpl implements SessionStateListener {
        
    	@Override
		public void onStateChange(SessionState newState, SessionState oldState, Object source) {
            
        	if (newState.equals(SessionState.CLOSED)) {
                
        		logger.info("Session closed");
                
                reconnectAndBindSession();
            }
        }
    }
    
    /** 
     * This listener will listen to every incoming short message, recognized by deliver_sm command.
     * It will update the smsGatewayDeliveryReports list if there are any new delivery reports
     **/
    private class MessageReceiverListenerImpl implements MessageReceiverListener {

    	@Override
		public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
        	if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
        		
               try {
                    DeliveryReceipt deliveryReceipt = deliverSm.getShortMessageAsDeliveryReceipt();
                    // retrieving the messageId in production is easy as getting the id of the "DeliveryReceipt" object
                    String messageId = deliveryReceipt.getId();
                    
                    // in development mode (a simulator gateway is used), retrieving the message ID is different
                    if(developmentMode()) {
                    	messageId = Long.toString(Long.parseLong(deliveryReceipt.getId()) & 0xffffffff, 16);
                    }
                    
                    SmsMessageStatusType messageStatus = null;
                    
                    switch(deliveryReceipt.getFinalStatus()) {
                    	case DELIVRD:
                    		messageStatus = SmsMessageStatusType.DELIVERED;
                    		break;
                    		
                    	case REJECTD:
                    	case EXPIRED:
                    	case UNDELIV:
                    		// rejected, expired and undelivered are grouped as failed 
                    		messageStatus = SmsMessageStatusType.FAILED;
                    		break;
                    		
						default:
							// in all other cases the status is invalid
							messageStatus = SmsMessageStatusType.INVALID;
							break;
                    }
                    
                    // create a new SmsGatewayDeliveryReport object with data received from the SMS gateway
                    SmsGatewayDeliveryReport smsGatewayDeliveryReport = new SmsGatewayDeliveryReport(messageId, deliveryReceipt.getSubmitDate(), deliveryReceipt.getDoneDate(), messageStatus);
                    
                    // add the SmsGatewayDeliveryReport object to the smsGatewayDeliveryReports List
                    smsGatewayDeliveryReports.add(smsGatewayDeliveryReport);
                    
                    // log success message
                    logger.info("Receiving delivery report for message '" + messageId + "' : " + smsGatewayDeliveryReport.toString());
               } 
               
               catch (InvalidDeliveryReceiptException e) {
            	   logger.error("Failed getting delivery report");
                }
            } 
        	
        	else {
                // inbound SMS message, this is currently not enabled - should never get in here
            	logger.info("Receiving message : " + new String(deliverSm.getShortMessage()));
            }
        }
        
        @Override
		public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
            return null;
        }
        
        @Override
		public void onAcceptAlertNotification(AlertNotification alertNotification) {}
    }
}

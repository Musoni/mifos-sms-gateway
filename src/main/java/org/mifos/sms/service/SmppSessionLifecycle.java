package org.mifos.sms.service;

import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.session.SessionStateListener;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.mifos.sms.data.SmsDeliveryStatus;
import org.mifos.sms.domain.SmsDeliveryReport;
import org.mifos.sms.domain.SmsDeliveryReportRepository;
import org.mifos.sms.gateway.infobip.SmsGatewayConfiguration;
import org.mifos.sms.smpp.session.SmppSessionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

public class SmppSessionLifecycle implements SmartLifecycle {
    private volatile Boolean running = false;
    private SMPPSession session;
    private volatile Boolean reconnect = true;
    private volatile Boolean isReconnecting = false;
    private static final Logger logger = LoggerFactory.getLogger(SmppSessionLifecycle.class);
    private final SmppSessionProperties smppSessionProperties;
    private final SmsGatewayConfiguration smsGatewayConfiguration;
    private final long reconnectInterval = 10000L; // 10 seconds
    private static SmppSessionLifecycle instance;
    private final int reconnectionAttemptLimit = 20;
    private final SmsDeliveryReportRepository smsDeliveryReportRepository;
    
    /**
     * @param session
     */
    public SmppSessionLifecycle(final SmppSessionProperties smppSessionProperties, 
            final SmsGatewayConfiguration smsGatewayConfiguration, 
            final SmsDeliveryReportRepository smsDeliveryReportRepository) {
        this.smppSessionProperties = smppSessionProperties;
        this.smsGatewayConfiguration = smsGatewayConfiguration;
        this.smsDeliveryReportRepository = smsDeliveryReportRepository;
        
        instance = this;
        
        this.running = true;
    }
    
    /**
     * @return {@link SmppSessionLifecycle} object
     */
    public static synchronized SmppSessionLifecycle getInstance() {
        return instance;
    }
    
    /**
     * Open SMPP session connection and bind immediately.
     */
    public void connectAndBindSmppSession() {
        try {
            if (session != null && !session.getSessionState().isBound()) {
                session.connectAndBind(this.smppSessionProperties.getHost(), this.smppSessionProperties.getPort(), 
                        new BindParameter(this.smppSessionProperties.getBindType(), this.smppSessionProperties.getSystemId(), 
                                this.smppSessionProperties.getPassword(), this.smppSessionProperties.getSystemType(), 
                                this.smppSessionProperties.getAddrTon(), this.smppSessionProperties.getAddrNpi(), 
                                this.smppSessionProperties.getAddressRange()));
                
                session.addSessionStateListener(new SessionStateListenerImpl());
                session.setMessageReceiverListener(new MessageReceiverListenerImpl());
                
                isReconnecting = false;
                
            } else {
                logger.error("Session is empty");
            }
            
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    
    /**
     * Creates a new instance of the {@link SMPPSession} object
     * 
     * @return
     */
    public SMPPSession createNewSmppSession() {
        return new SMPPSession();
    }
    
    /**
     * Unbind and close currently active SMPP session
     */
    public void unbindAndCloseSmppSession() {
        if (session != null && session.getSessionState().isBound()) {
            try { 
                session.unbindAndClose();
                
                logger.info("SMPP session connection successfully unbound and closed.");
                
            } catch (Throwable t) { 
                logger.warn("couldn't close and unbind the session", t); 
            } 
        }
    }
    
    /**
     * Restart the SMPP session
     */
    public void restartSmppSession() {
        if (this.running && this.reconnect && !this.isReconnecting) {
            
            // create a new instance of the SMPPSession class
            this.session = this.createNewSmppSession();
            
            new Thread() {
                @Override
                public void run() {
                    isReconnecting = true;
                    
                    logger.info("Schedule reconnect after " + reconnectInterval + " millis");
                    
                    try {
                        Thread.sleep(reconnectInterval);
                        
                    } catch (InterruptedException e) { }
                    
                    int attempt = 0;
                    
                    while (session.getSessionState().equals(SessionState.CLOSED) 
                            && (attempt < reconnectionAttemptLimit)) {
                        logger.info("Reconnecting attempt #" + (++attempt) + "...");
                        
                        // create a new connection
                        connectAndBindSmppSession();
                        
                        // wait for #reconnectInterval
                        try { 
                            Thread.sleep(reconnectInterval);
                            
                        }  catch (InterruptedException ee) {}
                    }
                }
            }.start();
        }
    }

    @Override
    public void start() {
        // create a new instance of the SMPPSession class 
        this.session = this.createNewSmppSession();
        
        this.connectAndBindSmppSession();
    }

    @Override
    public void stop() {
        // this will prevent a reconnection to the SMPP server
        reconnect = false;
        
        this.unbindAndCloseSmppSession();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public int getPhase() {
        return 1;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }
    
    /**
     * @return true if the session is still active
     */
    public boolean isActive() {
        return (this.running && (session != null) && (!session.getSessionState().equals(SessionState.CLOSED)));
    }

    @Override
    public void stop(Runnable callback) { }

    /**
     * @return the session
     */
    public SMPPSession getSession() {
        return session;
    }
    
    /**
     * This class will receive the notification from {@link SMPPSession} for the
     * state changes.
     **/
    private class SessionStateListenerImpl implements SessionStateListener {
        
        @Override
        public void onStateChange(SessionState newState, SessionState oldState, Object source) {
            
            if (newState.equals(SessionState.CLOSED)) { 
                String message = "Session closed. Will attempt to reconnect to the SMPP server.";
                
                // attempt to reconnect if reconnect boolean is set to true
                if (reconnect) {
                    restartSmppSession();
                    
                } else {
                    message = "Session closed.";
                }
                
                logger.info(message);
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
                    if(smsGatewayConfiguration.inDebugMode()) {
                        messageId = Long.toString(Long.parseLong(deliveryReceipt.getId()) & 0xffffffff, 16);
                    }
                    
                    final SmsDeliveryStatus smsDeliveryStatus = SmsDeliveryStatus.instance(deliveryReceipt.getFinalStatus());
                    final SmsDeliveryReport smsDeliveryReport = SmsDeliveryReport.instance(messageId, deliveryReceipt.getSubmitDate(), 
                            deliveryReceipt.getDoneDate(), smsDeliveryStatus.getId(), deliveryReceipt.getError());
                    
                    // add new delivery report to queue
                    smsDeliveryReportRepository.save(smsDeliveryReport);
                    
                    // log success message
                    logger.info("Receiving delivery report for message '" + messageId + "' : " + deliveryReceipt.toString());
               } 
               
               catch (InvalidDeliveryReceiptException e) {
                   logger.error(e.getMessage(), e);
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

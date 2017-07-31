package org.mifos.sms.smpp.session;

import java.io.IOException;
import java.util.Random;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GSMSpecificFeature;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageMode;
import org.jsmpp.bean.MessageType;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.StringParameter;
import org.mifos.sms.data.SmsShortMessage;
import org.mifos.sms.gateway.infobip.SmsGatewayConfiguration;
import org.mifos.sms.gateway.infobip.SmsGatewayMessage;
import org.mifos.sms.helper.Gsm0338;
import org.mifos.sms.service.SmppSessionLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory bean to create a {@link SMPPSession}.
 * <p/> 
 * The {@link SMPPSession } represents a connection to a SMSC, through which SMS messages are sent and received. 
 * <p/> 
 */
public class SmppSessionFactoryBean {
    private final SmppShortMessageProperties smppShortMessageProperties;
    private final SmppSessionProperties smppSessionProperties;
    private static final Logger logger = LoggerFactory.getLogger(SmppSessionFactoryBean.class);
    
    // represents the max size of a fragment of a concatenated short message
    public static final Integer SHORT_MESSAGE_FRAGMENT_MAX_SIZE = 140;
    
    private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2 = 70;
    private static final int MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT = 160;
    private final SmppSessionLifecycle smppSessionLifecycle;

    /**
     * @return the smppSessionLifecycle
     */
    public SmppSessionLifecycle getSmppSessionLifecycle() {
        return smppSessionLifecycle;
    }

    /**
     * @param smsGatewayConfiguration
     */
    public SmppSessionFactoryBean(final SmsGatewayConfiguration smsGatewayConfiguration) {
        this.smppShortMessageProperties = new SmppShortMessageProperties();
        this.smppSessionProperties = new SmppSessionProperties(smsGatewayConfiguration);
        this.smppSessionLifecycle = new SmppSessionLifecycle(smppSessionProperties, 
                smsGatewayConfiguration);
        
        // kick start the SMPP session life cycle
        this.smppSessionLifecycle.start();
    }
    
    /**
     * Creates multiple short messages (that include a user data header) by
     * splitting the binaryShortMessage data into 134 byte parts.  If the
     * binaryShortMessage does not need to be concatenated (less than or equal
     * to 140 bytes), this method will return NULL.
     * <br><br>
     * WARNING: This method only works on binary short messages that use 8-bit
     * bytes.  Short messages using 7-bit data or packed 7-bit data will not
     * be correctly handled by this method.
     * <br><br>
     * For example, will take a byte message (in hex, 138 bytes long)
     * <br>
     *   01020304...85&lt;byte 134&gt;87888990
     * <br><br>
     * Would be split into 2 parts as follows (in hex, with user data header)<br>
     *   050003CC020101020304...85&lt;byte 134&gt;<br>
     *   050003CC020287888990<br>
     * <br>
     * http://en.wikipedia.org/wiki/Concatenated_SMS
     *
     * @param binaryShortMessage The 8-bit binary short message to create the
     *      concatenated short messages from.
     * @param referenceNum The CSMS reference number that will be used in the
     *      user data header.
     * @return NULL if the binaryShortMessage does not need concatenated or
     *      an array of byte arrays representing each chunk (including UDH).
     * @throws IllegalArgumentException
     */
    private byte[][] createConcatenatedBinaryShortMessages(byte[] binaryShortMessage, byte referenceNum) throws IllegalArgumentException {
        if (binaryShortMessage == null) {
            return null;
        }
        // if the short message does not need to be concatenated
        if (binaryShortMessage.length <= 140) {
            return null;
        }

        // since the UDH will be 6 bytes, we'll split the data into chunks of 134
        int numParts = (int) (binaryShortMessage.length / 134) + (binaryShortMessage.length % 134 != 0 ? 1 : 0);
        //logger.debug("numParts=" + numParts);

        byte[][] shortMessageParts = new byte[numParts][];

        for (int i = 0; i < numParts; i++) {
            // default this part length to max of 134
            int shortMessagePartLength = 134;
            if ((i + 1) == numParts) {
                // last part (only need to add remainder)
                shortMessagePartLength = binaryShortMessage.length - (i * 134);
            }

            //logger.debug("part " + i + " len: " + shortMessagePartLength);

            // part will be UDH (6 bytes) + length of part
            byte[] shortMessagePart = new byte[6 + shortMessagePartLength];
            // Field 1 (1 octet): Length of User Data Header, in this case 05.
            shortMessagePart[0] = (byte) 0x05;
            // Field 2 (1 octet): Information Element Identifier, equal to 00 (Concatenated short messages, 8-bit reference number)
            shortMessagePart[1] = (byte) 0x00;
            // Field 3 (1 octet): Length of the header, excluding the first two fields; equal to 03
            shortMessagePart[2] = (byte) 0x03;
            // Field 4 (1 octet): 00-FF, CSMS reference number, must be same for all the SMS parts in the CSMS
            shortMessagePart[3] = referenceNum;
            // Field 5 (1 octet): 00-FF, total number of parts. The value shall remain constant for every short message which makes up the concatenated short message. If the value is zero then the receiving entity shall ignore the whole information element
            shortMessagePart[4] = (byte) numParts;
            // Field 6 (1 octet): 00-FF, this part's number in the sequence. The value shall start at 1 and increment for every short message which makes up the concatenated short message. If the value is zero or greater than the value in Field 5 then the receiving entity shall ignore the whole information element. [ETSI Specification: GSM 03.40 Version 5.3.0: July 1996]
            shortMessagePart[5] = (byte) (i + 1);

            // copy this part's user data onto the end
            System.arraycopy(binaryShortMessage, (i * 134), shortMessagePart, 6, shortMessagePartLength);
            shortMessageParts[i] = shortMessagePart;
        }

        return shortMessageParts;
    }
    
    /** 
     * Send the SMS message to the SMS gateway
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * 
     * @return {@link SmsGatewayMessage} object
     **/
    public SmsGatewayMessage submitShortMessage(SmsGatewayMessage smsGatewayMessage) {
        String message = smsGatewayMessage.getMessage();
        
        if (message != null && message.length() > StringParameter.SHORT_MESSAGE.getMax()) {
            smsGatewayMessage = this.submitSegmentedShortMessages(smsGatewayMessage);
        }
        
        else if (message != null) {
            // create a new SmsShortMessage object
            final SmsShortMessage smsShortMessage = SmsShortMessage.newSmsShortMessage(
                    smsGatewayMessage.getId(), smppShortMessageProperties.getServiceType(), 
                    smppShortMessageProperties.getSourceAddressTypeOfNumber(), 
                    smppShortMessageProperties.getSourceAddressNumberingPlanIndicator(), 
                    smsGatewayMessage.getSourceAddress(), smppShortMessageProperties.getDestinationAddressTypeOfNumber(), 
                    smppShortMessageProperties.getDestinationAddressNumberingPlanIndicator(), 
                    smsGatewayMessage.getMobileNumber(), 
                    smppShortMessageProperties.getEsmClass(), 
                    smppShortMessageProperties.getProtocolId(), smppShortMessageProperties.getPriorityFlag(), 
                    smppShortMessageProperties.getScheduleDeliveryTime(), 
                    smppShortMessageProperties.getValidityPeriod(), 
                    smppShortMessageProperties.getRegisteredDelivery(), 
                    smppShortMessageProperties.getReplaceIfPresentFlag(), 
                    smppShortMessageProperties.getDataCoding(), 
                    smppShortMessageProperties.getSmDefaultMsgId(), 
                    message.getBytes(), 1, 1);
            
            // send short message to SMSC (short message service center)
            smsGatewayMessage = this.submitShortMessage(smsShortMessage);
        }
        
        return smsGatewayMessage;
    }
    
    /**
     * Send SMS message to the SMS gateway
     * 
     * @param smsShortMessage
     * @return {@link SmsGatewayMessage} object
     */
    public SmsGatewayMessage submitShortMessage(final SmsShortMessage smsShortMessage) {
        String messageId = "";
        
        try {
            final SMPPSession session = smppSessionLifecycle.getSession();
            
            if (!smppSessionLifecycle.isActive()) {
                smppSessionLifecycle.restartSmppSession();
            }
            
            messageId = session.submitShortMessage(smsShortMessage.getServiceType(), 
                    smsShortMessage.getSourceAddressTypeofNumber(), 
                    smsShortMessage.getSourceAddressNumberingPlanIndicator(), 
                    smsShortMessage.getSourceAddress(), 
                    smsShortMessage.getDestinationAddressTypeOfNumber(), 
                    smsShortMessage.getDestinationAddressNumberingPlanIndicator(), 
                    smsShortMessage.getDestinationAddress(), 
                    smsShortMessage.getEsmClass(), 
                    smsShortMessage.getProtocolId(), 
                    smsShortMessage.getPriorityFlag(), 
                    smsShortMessage.getScheduleDeliveryTime(), 
                    smsShortMessage.getValidityPeriod(), 
                    smsShortMessage.getRegisteredDelivery(), 
                    smsShortMessage.getReplaceIfPresentFlag(), 
                    smsShortMessage.getDataCoding(), 
                    smsShortMessage.getDefaultMessageId(), 
                    smsShortMessage.getShortMessage().getBytes());
            
            logger.info("Message segment " + smsShortMessage.getMessageSegmentNumber() 
                    + " out of " + smsShortMessage.getTotalNumberOfMessageSegments() 
                    + " segments sent to " + smsShortMessage.getDestinationAddress() 
                    +  ", SMS gateway message ID is " + messageId);
        } 
        
        catch (PDUException e) {
            // Invalid PDU parameter - mostly resulting from failed validation
            logger.error("Invalid PDU parameter", e);
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
            logger.error("IO error occur", e);
        }
        
        return new SmsGatewayMessage(smsShortMessage.getMessageId(), messageId, 
                smsShortMessage.getSourceAddress(), smsShortMessage.getDestinationAddress(), 
                smsShortMessage.getShortMessage());
    }
    
    /** 
     * Send segmented SMS messages to the SMS gateway
     * 
     * @param smsGatewayMessage SmsGatewayMessage object
     * 
     * @return {@link SmsGatewayMessage} object
     **/
    public SmsGatewayMessage submitSegmentedShortMessages(SmsGatewayMessage smsGatewayMessage) {
        String message = smsGatewayMessage.getMessage();
        
        Alphabet alphabet = null;
        int maximumSingleMessageSize = 0;
        byte[] originalMessageBytes = null;
        
        if (message != null && message.length() > StringParameter.SHORT_MESSAGE.getMax()) {
            try {
                if (Gsm0338.isEncodeableInGsm0338(message)) {
                    originalMessageBytes = message.getBytes();
                    alphabet = Alphabet.ALPHA_DEFAULT;
                    maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_7BIT;
                } 
                
                else {
                    originalMessageBytes = message.getBytes("UTF-16BE");
                    alphabet = Alphabet.ALPHA_UCS2;
                    maximumSingleMessageSize = MAX_SINGLE_MSG_SEGMENT_SIZE_UCS2;
                }

                // check if message needs splitting and set required sending parameters
                byte[][] segmentedMessagesBytes = null;
                ESMClass esmClass = null;
                
                if (message.length() > maximumSingleMessageSize) {
                    byte[] referenceNumber = new byte[1];
                    new Random().nextBytes(referenceNumber);
                    
                    segmentedMessagesBytes = createConcatenatedBinaryShortMessages(originalMessageBytes, 
                            referenceNumber[0]);
                    
                    // set UDHI so PDU will decode the header
                    esmClass = new ESMClass(MessageMode.DEFAULT, MessageType.DEFAULT, GSMSpecificFeature.UDHI);
                } 
                
                else {
                    segmentedMessagesBytes = new byte[][] { originalMessageBytes };
                    esmClass = new ESMClass();
                }
                
                // submit all messages
                for (int i = 0; i < segmentedMessagesBytes.length; i++) {
                    int segmentNumber = i + 1;
                    
                    // create a new SmsShortMessage object
                    final SmsShortMessage smsShortMessage = SmsShortMessage.newSmsShortMessage(
                            smsGatewayMessage.getId(), smppShortMessageProperties.getServiceType(), 
                            smppShortMessageProperties.getSourceAddressTypeOfNumber(), 
                            smppShortMessageProperties.getSourceAddressNumberingPlanIndicator(), 
                            smsGatewayMessage.getSourceAddress(), 
                            smppShortMessageProperties.getDestinationAddressTypeOfNumber(), 
                            smppShortMessageProperties.getDestinationAddressNumberingPlanIndicator(), 
                            smsGatewayMessage.getMobileNumber(), 
                            esmClass, smppShortMessageProperties.getProtocolId(), 
                            smppShortMessageProperties.getPriorityFlag(), 
                            smppShortMessageProperties.getScheduleDeliveryTime(), 
                            smppShortMessageProperties.getValidityPeriod(), 
                            smppShortMessageProperties.getRegisteredDelivery(), 
                            smppShortMessageProperties.getReplaceIfPresentFlag(), 
                            new GeneralDataCoding(alphabet, esmClass), smppShortMessageProperties.getSmDefaultMsgId(), 
                            segmentedMessagesBytes[i], segmentNumber, segmentedMessagesBytes.length);
                    
                    // send short message to SMSC (short message service center)
                    smsGatewayMessage = this.submitShortMessage(smsShortMessage);
                }
            }
            
            catch (IOException e) {
                logger.error("IO error occur", e);
            }
        }
        
        return smsGatewayMessage;
    }
}

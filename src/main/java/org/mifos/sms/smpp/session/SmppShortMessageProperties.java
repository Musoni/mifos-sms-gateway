package org.mifos.sms.smpp.session;

import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;

public class SmppShortMessageProperties {
    private final int maxLengthSmsMessages = 140; 
    private final String serviceType = "CMT";
    private final TypeOfNumber sourceAddressTypeOfNumber = TypeOfNumber.ALPHANUMERIC;
    private final NumberingPlanIndicator sourceAddressNumberingPlanIndicator = NumberingPlanIndicator.UNKNOWN;
    private final TypeOfNumber destinationAddressTypeOfNumber = TypeOfNumber.INTERNATIONAL;
    private final NumberingPlanIndicator destinationAddressNumberingPlanIndicator = NumberingPlanIndicator.UNKNOWN; 
    private final ESMClass esmClass = new ESMClass();
    private final byte protocolId = (byte)0;
    private final byte priorityFlag = (byte)0;
    private final String scheduleDeliveryTime = null;
    private final String validityPeriod = null;
    private final RegisteredDelivery registeredDelivery = new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE);
    private final byte replaceIfPresentFlag = (byte)0;
    private final DataCoding dataCoding = new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false);
    private final byte smDefaultMsgId = (byte)0;
    
    /**
     * @return the maxLengthSmsMessages
     */
    public int getMaxLengthSmsMessages() {
        return maxLengthSmsMessages;
    }
    
    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }
    
    /**
     * @return the sourceAddressTypeOfNumber
     */
    public TypeOfNumber getSourceAddressTypeOfNumber() {
        return sourceAddressTypeOfNumber;
    }
    
    /**
     * @return the sourceAddressNumberingPlanIndicator
     */
    public NumberingPlanIndicator getSourceAddressNumberingPlanIndicator() {
        return sourceAddressNumberingPlanIndicator;
    }
    
    /**
     * @return the destinationAddressTypeOfNumber
     */
    public TypeOfNumber getDestinationAddressTypeOfNumber() {
        return destinationAddressTypeOfNumber;
    }
    
    /**
     * @return the destinationAddressNumberingPlanIndicator
     */
    public NumberingPlanIndicator getDestinationAddressNumberingPlanIndicator() {
        return destinationAddressNumberingPlanIndicator;
    }
    
    /**
     * @return the esmClass
     */
    public ESMClass getEsmClass() {
        return esmClass;
    }
    
    /**
     * @return the protocolId
     */
    public byte getProtocolId() {
        return protocolId;
    }
    
    /**
     * @return the priorityFlag
     */
    public byte getPriorityFlag() {
        return priorityFlag;
    }
    
    /**
     * @return the scheduleDeliveryTime
     */
    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }
    
    /**
     * @return the validityPeriod
     */
    public String getValidityPeriod() {
        return validityPeriod;
    }
    
    /**
     * @return the registeredDelivery
     */
    public RegisteredDelivery getRegisteredDelivery() {
        return registeredDelivery;
    }
    
    /**
     * @return the replaceIfPresentFlag
     */
    public byte getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }
    
    /**
     * @return the dataCoding
     */
    public DataCoding getDataCoding() {
        return dataCoding;
    }
    
    /**
     * @return the smDefaultMsgId
     */
    public byte getSmDefaultMsgId() {
        return smDefaultMsgId;
    }
}

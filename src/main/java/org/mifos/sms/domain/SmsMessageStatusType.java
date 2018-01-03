package org.mifos.sms.domain;

import org.mifos.sms.data.SmsDeliveryStatus;

/** 
 * SMS message delivery status predefined enum constants
 * 
 * @author Emmanuel Nnaa
 **/
public enum SmsMessageStatusType {
	INVALID(0, "smsMessageStatusType.invalid"), // unknown status type
    PENDING(100, "smsMessageStatusType.pending"), // message received from mifostenant and added to table
    SENT(200, "smsMessageStatusType.sent"), // message sent to the SMS gateway
    DELIVERED(300, "smsMessageStatusType.delivered"), // SMS gateway's attempt to deliver message to recipient's phone was successful
    FAILED(400, "smsMessageStatusType.failed"); // SMS gateway's attempt to deliver message to recipient's phone failed
	
	private final Integer value;
    private final String code;

    /** 
     * get enum constant by value
     * 
     * @param statusValue the value of the enum constant
     * @return enum constant
     **/
    public static SmsMessageStatusType fromInt(final Integer statusValue) {

        SmsMessageStatusType enumeration = SmsMessageStatusType.INVALID;
        
        for (SmsMessageStatusType instance : SmsMessageStatusType.values()) {
            if (statusValue == instance.value) {
                enumeration = instance;
                
                break;
            }
        }
        
        return enumeration;
    }
    
    /**
     * Returns the {@link SmsMessageStatusType} representation of the {@link SmsDeliveryStatus} object
     * 
     * @param smsDeliveryStatus
     * @return {@link SmsDeliveryStatus} object
     */
    public static SmsMessageStatusType instance(final SmsDeliveryStatus smsDeliveryStatus) {
        SmsMessageStatusType enumeration = SmsMessageStatusType.INVALID;
        
        switch (smsDeliveryStatus) {
            case DELIVERED:
                enumeration = SmsMessageStatusType.DELIVERED;
                break;
        
            default:
                enumeration = SmsMessageStatusType.FAILED;
                break;
        }
        
        return enumeration;
    }

    /** 
     * SmsMessageStatusType constructor  
     **/
    private SmsMessageStatusType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    /** 
     * @return enum constant value 
     **/
    public Integer getValue() {
        return this.value;
    }

    /** 
     * @return enum constant 
     **/
    public String getCode() {
        return this.code;
    }
}

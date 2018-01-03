package org.mifos.sms.data;

import org.apache.commons.lang3.StringUtils;
import org.jsmpp.util.DeliveryReceiptState;

public enum SmsDeliveryStatus {
    UNKNOWN(0, "UNKNOWN", "Unknown error occured."),
    ACCEPTED(100, "ACCEPTD", "The SMS was accepted and will be send."),
    ENROUTE(200, "ENROUTE", "The message is enroute."),
    DELIVERED(300, "DELIVRD", "The message was successfully delivered."),
    UNDELIVERED(400, "UNDELIV", "The SMSC was unable to deliver the message. For instance, when the number does not exist."),
    REJECTED(500, "REJECTD", "The message was rejected.The provider could have blocked phonenumbers in this range."),
    EXPIRED(600, "EXPIRED", "The SMSC was unable to deliver the message in a specified amount of time. For instance when the phone was turned off.");
    
    private int id;
    private String code;
    private String description;
    
    /**
     * @param id
     * @param code
     * @param description
     */
    private SmsDeliveryStatus(final int id, final String code, final String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }
    
    /**
     * Creates a new instance of {@link SmsDeliveryStatus} object
     * 
     * @param code
     * @return {@link SmsDeliveryStatus} object
     */
    public static SmsDeliveryStatus instance(final String code) {
        SmsDeliveryStatus deliveryStatus = UNKNOWN;
        
        for (SmsDeliveryStatus instance : SmsDeliveryStatus.values()) {
            if (StringUtils.equalsIgnoreCase(code, instance.code)) {
                deliveryStatus = instance;
                
                break;
            }
        }
        
        return deliveryStatus;
    }
    
    /**
     * Creates a new instance of {@link SmsDeliveryStatus} object
     * 
     * @param id
     * @return {@link SmsDeliveryStatus} object
     */
    public static SmsDeliveryStatus instance(final Integer id) {
        SmsDeliveryStatus deliveryStatus = UNKNOWN;
        
        for (SmsDeliveryStatus instance : SmsDeliveryStatus.values()) {
            if (id == instance.id) {
                deliveryStatus = instance;
                
                break;
            }
        }
        
        return deliveryStatus;
    }
    
    /**
     * Returns a {@link SmsDeliveryStatus} representation of the {@link DeliveryReceiptState} object
     * 
     * @param deliveryReceiptState
     * @return {@link SmsDeliveryStatus} object
     */
    public static SmsDeliveryStatus instance(final DeliveryReceiptState deliveryReceiptState) {
        final String code = deliveryReceiptState.toString();
        
        return instance(code);
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}

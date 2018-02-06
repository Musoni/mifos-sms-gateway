package org.mifos.sms.gateway.infobip;

import org.apache.commons.lang3.builder.ToStringBuilder;

/** 
 * Immutable data object representing a sms gateway message 
 **/
public class SmsGatewayMessage {
	/** 
     * the internal message identifier (mifos sms message table id) 
     **/
    private Long id;
    
    /** 
     * the SMS gateway message identifier
     **/
    private String externalId;
    
    /** 
     * 
     * the sender of the SMS message 
     **/
    private String sourceAddress;
    
    /** 
     * mobile phone number of sms message recipient, must be in international format without the leading
     * "0" or "+", example: 31612345678
     **/
    private String mobileNumber;
    
    /** 
     * the sms message text to be sent out
     **/
    private String message;
    
    /**
     * the number sms segments that was sent out
     */
    private Integer numberOfSegments;
    
    /**
     * SmsGatewayMessage constructor
     * 
     * @param id
     * @param externalId
     * @param sourceAddress
     * @param mobileNumber
     * @param message
     * @param numberOfSegments
     */
    public SmsGatewayMessage(Long id, String externalId, String sourceAddress, String mobileNumber, String message,
            Integer numberOfSegments) {
        this.id = id;
        this.externalId = externalId;
        this.sourceAddress = sourceAddress;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.numberOfSegments = numberOfSegments;
    }
    
    /**
     * Creates a new {@link SmsGatewayMessage} object
     * 
     * @param id
     * @param externalId
     * @param sourceAddress
     * @param mobileNumber
     * @param message
     * @return {@link SmsGatewayMessage} object
     */
    public static SmsGatewayMessage getInstance(Long id, String externalId, String sourceAddress, 
            String mobileNumber, String message) {
        return new SmsGatewayMessage(id, externalId, sourceAddress, mobileNumber, message, null);
    }

    /**
     * @return the id
     **/
    public Long getId() {
        return id;
    }
    
    /** 
     * @return the mobileNumber 
     **/
    public String getMobileNumber() {
        return mobileNumber;
    }
    
    /** 
     * @return the message 
     **/
    public String getMessage() {
        return message;
    }
    
    /** 
     * @return the externalId 
     **/
    public String getExternalId() {
        return externalId;
    }
    
    /**
     * @return the numberOfSegments
     */
    public Integer getNumberOfSegments() {
        return numberOfSegments;
    }

    /** 
     * @return String representation of the SmsGatewayMessage class
     **/
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

	/**
	 * @return the sourceAddress
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}

    /**
     * @param numberOfSegments the numberOfSegments to set
     */
    public void setNumberOfSegments(Integer numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }
}

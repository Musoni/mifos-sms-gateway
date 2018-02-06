package org.mifos.sms.data;

import java.util.Date;

import org.mifos.sms.domain.SmsMessageEnumerations;
import org.mifos.sms.domain.SmsOutboundMessage;

/** 
 * Immutable data object representing a SMS message
 * 
 * @author Emmanuel Nnaa 
 **/
public class SmsOutboundMessageData {
	
	private Long id;
	private String externalId;
	private Long internalId;
	private String mifosTenantIdentifier;
	private Date createdOnDate;
	private Date submittedOnDate;
	private Date addedOnDate;
	private Date deliveredOnDate;
	private EnumOptionData deliveryStatus;
	private String mobileNumber;
	private String message;
	private Integer numberOfSegments;
	private Integer smsErrorCodeId;
	
	/**
     * @param id
     * @param externalId
     * @param internalId
     * @param mifosTenantIdentifier
     * @param createdOnDate
     * @param submittedOnDate
     * @param addedOnDate
     * @param deliveredOnDate
     * @param deliveryStatus
     * @param mobileNumber
     * @param message
     * @param numberOfSegments
     * @param smsErrorCodeId
     */
    private SmsOutboundMessageData(Long id, String externalId, Long internalId, String mifosTenantIdentifier,
            Date createdOnDate, Date submittedOnDate, Date addedOnDate, Date deliveredOnDate,
            EnumOptionData deliveryStatus, String mobileNumber, String message, Integer numberOfSegments,
            Integer smsErrorCodeId) {
        this.id = id;
        this.externalId = externalId;
        this.internalId = internalId;
        this.mifosTenantIdentifier = mifosTenantIdentifier;
        this.createdOnDate = createdOnDate;
        this.submittedOnDate = submittedOnDate;
        this.addedOnDate = addedOnDate;
        this.deliveredOnDate = deliveredOnDate;
        this.deliveryStatus = deliveryStatus;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.numberOfSegments = numberOfSegments;
        this.smsErrorCodeId = smsErrorCodeId;
    }
	
	/** 
	 * Default SmsOutboundMessageData constructor 
	 * 
	 * @return void
	 **/
	protected SmsOutboundMessageData() {}
	
	/**
	 * Creates a new {@link SmsOutboundMessageData} object
	 * 
	 * @param smsOutboundMessage
	 * @return {@link SmsOutboundMessageData} object
	 */
	public static SmsOutboundMessageData getInstance(SmsOutboundMessage smsOutboundMessage) {
	    final EnumOptionData deliveryStatus = SmsMessageEnumerations.status(smsOutboundMessage.getDeliveryStatus());
	    
	    return new SmsOutboundMessageData(smsOutboundMessage.getId(), smsOutboundMessage.getExternalId(), 
	            smsOutboundMessage.getInternalId(), smsOutboundMessage.getMifosTenantIdentifier(), 
	            smsOutboundMessage.getCreatedOnDate(), smsOutboundMessage.getSubmittedOnDate(), 
	            smsOutboundMessage.getAddedOnDate(), smsOutboundMessage.getDeliveredOnDate(), 
	            deliveryStatus, smsOutboundMessage.getMobileNumber(), smsOutboundMessage.getMessage(), 
	            smsOutboundMessage.getNumberOfSegments(), smsOutboundMessage.getSmsErrorCodeId());
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
	/** 
	 * @return the external ID 
	 **/
	public String getExternalId() {
		return externalId;
	}
	
	/** 
	 * @return the internal ID 
	 **/
	public Long getInternalId() {
		return internalId;
	}
	
	/** 
	 * @return the mifos tenant identifier 
	 **/
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
	
	/** 
	 * @return the created on date 
	 **/
	public Date getCreatedOnDate() {
		return createdOnDate;
	}
	
	/** 
	 * @return the submitted on date 
	 **/
	public Date getSubmittedOnDate() {
		return submittedOnDate;
	}
	
	/** 
	 * @return the added on date 
	 **/
	public Date getAddedOnDate() {
		return addedOnDate;
	}
	
	/** 
	 * @return the delivered on date 
	 **/
	public Date getDeliveredOnDate() {
		return deliveredOnDate;
	}
	
	/** 
	 * @return the delivery status 
	 **/
	public EnumOptionData getDeliveryStatus() {
		return deliveryStatus;
	}
	
	/** 
	 * @return the mobile phone number 
	 **/
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	/** 
	 * @return the sms message 
	 **/
	public String getMessage() {
		return message;
	}

    /**
     * @return the numberOfSegments
     */
    public Integer getNumberOfSegments() {
        return numberOfSegments;
    }

    /**
     * @return the smsErrorCodeId
     */
    public Integer getSmsErrorCodeId() {
        return smsErrorCodeId;
    }
}

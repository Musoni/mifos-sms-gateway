package org.mifos.sms.data;

import java.util.Date;
import org.mifos.sms.data.EnumOptionData;

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
	private String deliveryErrorMessage;
	private String mobileNumber;
	private String message;
	
	/** 
	 * SmsOutboundMessageData constructor 
	 * 
	 * @return void
	 **/
	private SmsOutboundMessageData(final Long id, final String externalId, final Long internalId, final String mifosTenantIdentifier, 
			final Date createdOnDate, final Date submittedOnDate, final Date addedOnDate, final Date deliveredOnDate, 
			final EnumOptionData deliveryStatus, final String deliveryErrorMessage, final String mobileNumber, final String message) {
		
		this.id = id;
		this.externalId = externalId;
		this.internalId = internalId;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
		this.createdOnDate = createdOnDate;
		this.submittedOnDate = submittedOnDate;
		this.addedOnDate = addedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus;
		this.deliveryErrorMessage = deliveryErrorMessage;
		this.mobileNumber = mobileNumber;
		this.message = message;
	}
	
	/** 
	 * Default SmsOutboundMessageData constructor 
	 * 
	 * @return void
	 **/
	protected SmsOutboundMessageData() {}
	
	/** 
	 * get an instance of the SmsOutboundMessageData class
	 * 
	 * @return SmsOutboundMessageData object
	 **/
	public static SmsOutboundMessageData getInstance(final Long id, final String externalId, final Long internalId, final String mifosTenantIdentifier, 
			final Date createdOnDate, final Date submittedOnDate, final Date addedOnDate, final Date deliveredOnDate, 
			final EnumOptionData deliveryStatus, final String deliveryErrorMessage, final String mobileNumber, final String message) {
		
		return new SmsOutboundMessageData(id, externalId, internalId, mifosTenantIdentifier, createdOnDate, submittedOnDate, addedOnDate, deliveredOnDate, 
				deliveryStatus, deliveryErrorMessage, mobileNumber, message);
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
	 * @return the delivery error message 
	 **/
	public String getDeliveryErrorMessage() {
		return deliveryErrorMessage;
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
}

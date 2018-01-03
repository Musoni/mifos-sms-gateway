package org.mifos.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.joda.time.LocalDate;
import org.mifos.sms.data.EnumOptionData;
import org.mifos.sms.data.SmsOutboundMessageData;
import org.springframework.data.jpa.domain.AbstractPersistable;

/** 
 * The SmsOutboundMessage entity class represents the SmsOutboundMessage table
 * 
 * @author Emmanuel Nnaa
 **/
@Entity
@Table(name = "smsOutboundMessage")
public class SmsOutboundMessage extends AbstractPersistable<Long> {
	
	private static final long serialVersionUID = -1934303270279746623L;

    @Column(name = "externalId", nullable = true)
    private String externalId;
	
	@Column(name = "internalId", nullable = false)
    private Long internalId;
	
	@Column(name = "mifosTenantIdentifier", nullable = false)
    private String mifosTenantIdentifier;
	
	@Column(name = "createdOnDate", nullable = true)
	@Temporal(TemporalType.DATE)
    private Date createdOnDate;
	
	@Column(name = "submittedOnDate", nullable = true)
	@Temporal(TemporalType.DATE)
    private Date submittedOnDate;
	
	@Column(name = "addedOnDate", nullable = false)
	@Temporal(TemporalType.DATE)
    private Date addedOnDate;
	
	@Column(name = "deliveredOnDate", nullable = true)
	@Temporal(TemporalType.DATE)
    private Date deliveredOnDate;
	
	@Column(name = "deliveryStatus", nullable = false)
    private Integer deliveryStatus = SmsMessageStatusType.PENDING.getValue();
	
	@Column(name = "deliveryErrorMessage", nullable = true)
    private String deliveryErrorMessage;
	
	@Column(name = "sourceAddress", nullable = false)
    private String sourceAddress;
	
	@Column(name = "mobileNumber", nullable = false)
    private String mobileNumber;
	
	@Column(name = "message", nullable = false)
    private String message;
	
	/** 
	 * SmsOutboundMessage constructor
	 * 
	 * @return void 
	 **/
	private SmsOutboundMessage(final String externalId, final Long internalId, final String mifosTenantIdentifier, 
			final Date createdOnDate, final Date submittedOnDate, final Date addedOnDate, final Date deliveredOnDate, 
			final SmsMessageStatusType deliveryStatus, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message) {
		this.externalId = externalId;
		this.internalId = internalId;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
		this.createdOnDate = createdOnDate;
		this.submittedOnDate = submittedOnDate;
		this.addedOnDate = addedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus.getValue();
		this.deliveryErrorMessage = deliveryErrorMessage;
		this.sourceAddress = sourceAddress;
		this.mobileNumber = mobileNumber;
		this.message = message;
	}
	
	/** 
	 * Default SmsOutboundMessage constructor
	 * 
	 * @return void
	 **/
	protected SmsOutboundMessage() {}
	
	/** 
	 * get messages with delivery status 100 
	 * 
	 * @return SmsOutboundMessage object
	 **/
	public static SmsOutboundMessage getPendingMessages(final String externalId, final Long internalId, final String mifosTenantIdentifier, 
			final Date createdOnDate, final Date submittedOnDate, final Date addedOnDate, final Date deliveredOnDate, 
			final String deliveryErrorMessage, final String sourceAddress, final String mobileNumber, final String message) {
		
		return new SmsOutboundMessage(externalId, internalId, mifosTenantIdentifier, createdOnDate, submittedOnDate, addedOnDate, 
				deliveredOnDate, SmsMessageStatusType.PENDING, deliveryErrorMessage, sourceAddress, mobileNumber, message);
	}
	
	/**  
	 * @return an instance of the SmsOutboundMessage class
	 **/
	public SmsOutboundMessage getInstance(final String externalId, final Long internalId, final String mifosTenantIdentifier, 
			final Date createdOnDate, final Date submittedOnDate, final Date addedOnDate, final Date deliveredOnDate, final SmsMessageStatusType deliveryStatus, 
			final String deliveryErrorMessage, final String sourceAddress, final String mobileNumber, final String message) {
		
		return new SmsOutboundMessage(externalId, internalId, mifosTenantIdentifier, createdOnDate, submittedOnDate, addedOnDate, 
				deliveredOnDate, deliveryStatus, deliveryErrorMessage, sourceAddress, mobileNumber, message);
	}
	
	/** 
	 * @return the sms gateway message identifier 
	 **/
	public String getExternalId() {
		return externalId;
	}
	
	/** 
	 * set the value of the sms gateway message identifier 
	 **/
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	/** 
	 * @return the mifostenant sms message identifier 
	 **/
	public Long getInternalId() {
		return internalId;
	}
	
	/** 
	 * @return the mifos tenant identifier, e.g. tugende 
	 **/
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
	
	/** 
	 * @return the date the message was added to the mifostenant sms table 
	 **/
	public Date getCreatedOnDate() {
		return createdOnDate;
	}
	
	/** 
	 * @return the date the message was submitted to the sms gateway 
	 **/
	public Date getSubmittedOnDate() {
		return submittedOnDate;
	}
	
	/** 
	 * set the value of the date the message was submitted to the sms gateway
	 **/
	public void setSubmittedOnDate(Date submittedOnDate) {
		this.submittedOnDate = submittedOnDate;
	}
	
	/** 
	 * @return the date the message was added to the local sms table 
	 **/
	public LocalDate getAddedOnDate() {
		return new LocalDate(this.addedOnDate);
	}
	
	/** 
	 * set the value of the date the message was added to the local sms table
	 **/
	public void setAddedOnDate(Date addedOnDate) {
		this.addedOnDate = addedOnDate;
	}
	
	/** 
	 * @return the date the message was delivered to the recipient phone 
	 **/
	public LocalDate getDeliveredOnDate() {
	    return new LocalDate(this.deliveredOnDate);
	}
	
	/** 
	 * set the value of the date the message was delivered to the recipient phone 
	 **/
	public void setDeliveredOnDate(Date deliveredOnDate) {
		this.deliveredOnDate = deliveredOnDate;
	}
	
	/** 
	 * @return the current delivery status 
	 **/
	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}
	
	/** 
     * Set the value of this.statusType
     * 
     * @param deliveryStatus message delivery status enum
     * @return void
     **/
    public void setDeliveryStatus(SmsMessageStatusType deliveryStatus) {
        if (deliveryStatus != null) {
            this.deliveryStatus = deliveryStatus.getValue();
        }
    }
	
	/** 
	 * @return the delivery error message 
	 **/
	public String getDeliveryErrorMessage() {
		return deliveryErrorMessage;
	}
	
	/** 
	 * set the value of the delivery error message
	 * 
	 * @param deliveryErrorMessage the delivery error message
	 * @return void
	 **/
	public void setDeliveryErrorMessage(String deliveryErrorMessage) {
		this.deliveryErrorMessage = deliveryErrorMessage;
	}
	
	/** 
	 * @return the sms message recipient mobile number
	 **/
	public String getMobileNumber() {
		return mobileNumber;
	}
	
	/** 
	 * @return the sender of the sms message
	 **/
	public String getSourceAddress() {
		return sourceAddress;
	}
	
	/** 
	 * @return the sms message text 
	 **/
	public String getMessage() {
		return message;
	}
	
	/** 
	 * convert SmsOutboundMessage to SmsOutboundMessageData
	 * 
	 * @return object of type SmsOutboundMessageData
	 **/
	public SmsOutboundMessageData toData() {
	    final EnumOptionData deliveryStatus = SmsMessageEnumerations.status(this.deliveryStatus);
	    
	    return SmsOutboundMessageData.getInstance(this.getId(), this.externalId, this.internalId, this.mifosTenantIdentifier, this.createdOnDate, 
	            this.submittedOnDate, this.addedOnDate, this.deliveredOnDate, deliveryStatus, this.deliveryErrorMessage, this.mobileNumber, 
	            this.deliveryErrorMessage);
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SmsOutboundMessage [externalId=" + externalId + ", internalId=" + internalId
                + ", mifosTenantIdentifier=" + mifosTenantIdentifier + ", createdOnDate=" + createdOnDate
                + ", submittedOnDate=" + submittedOnDate + ", addedOnDate=" + addedOnDate + ", deliveredOnDate="
                + deliveredOnDate + ", deliveryStatus=" + deliveryStatus + ", deliveryErrorMessage="
                + deliveryErrorMessage + ", sourceAddress=" + sourceAddress + ", mobileNumber=" + mobileNumber
                + ", message=" + message + "]";
    }
}

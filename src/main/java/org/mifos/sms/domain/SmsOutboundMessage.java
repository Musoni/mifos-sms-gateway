package org.mifos.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.LocalDate;
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
	
	@Column(name = "sourceAddress", nullable = false)
    private String sourceAddress;
	
	@Column(name = "mobileNumber", nullable = false)
    private String mobileNumber;
	
	@Column(name = "message", nullable = false)
    private String message;
	
	@Column(name = "numberOfSegments", nullable = true)
	private Integer numberOfSegments;
	
	@Column(name = "smsErrorCodeId", nullable = true)
	private Integer smsErrorCodeId;
	
	/**
	 * no-args constructor
	 */
	protected SmsOutboundMessage() { }
	
	/**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the internalId
     */
    public Long getInternalId() {
        return internalId;
    }

    /**
     * @return the mifosTenantIdentifier
     */
    public String getMifosTenantIdentifier() {
        return mifosTenantIdentifier;
    }

    /**
     * @return the createdOnDate
     */
    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    /**
     * @return the submittedOnDate
     */
    public Date getSubmittedOnDate() {
        return submittedOnDate;
    }

    /**
     * @return the addedOnDate
     */
    public Date getAddedOnDate() {
        return addedOnDate;
    }

    /**
     * @return the deliveredOnDate
     */
    public Date getDeliveredOnDate() {
        return deliveredOnDate;
    }

    /**
     * @return the deliveryStatus
     */
    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * @return the sourceAddress
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @return the mobileNumber
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @return the message
     */
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

    /**
     * @param externalId the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @param internalId the internalId to set
     */
    public void setInternalId(Long internalId) {
        this.internalId = internalId;
    }

    /**
     * @param mifosTenantIdentifier the mifosTenantIdentifier to set
     */
    public void setMifosTenantIdentifier(String mifosTenantIdentifier) {
        this.mifosTenantIdentifier = mifosTenantIdentifier;
    }

    /**
     * @param createdOnDate the createdOnDate to set
     */
    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    /**
     * @param submittedOnDate the submittedOnDate to set
     */
    public void setSubmittedOnDate(Date submittedOnDate) {
        this.submittedOnDate = submittedOnDate;
    }

    /**
     * @param addedOnDate the addedOnDate to set
     */
    public void setAddedOnDate(Date addedOnDate) {
        this.addedOnDate = addedOnDate;
    }

    /**
     * @param deliveredOnDate the deliveredOnDate to set
     */
    public void setDeliveredOnDate(Date deliveredOnDate) {
        this.deliveredOnDate = deliveredOnDate;
    }

    /**
     * @param deliveryStatus the deliveryStatus to set
     */
    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    /**
     * @param sourceAddress the sourceAddress to set
     */
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * @param mobileNumber the mobileNumber to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param numberOfSegments the numberOfSegments to set
     */
    public void setNumberOfSegments(Integer numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }

    /**
     * @param smsErrorCodeId the smsErrorCodeId to set
     */
    public void setSmsErrorCodeId(Integer smsErrorCodeId) {
        this.smsErrorCodeId = smsErrorCodeId;
    }

    /** 
	 * convert SmsOutboundMessage to SmsOutboundMessageData
	 * 
	 * @return object of type SmsOutboundMessageData
	 **/
	public SmsOutboundMessageData toData() {
	    return SmsOutboundMessageData.getInstance(this);
	}

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Returns the addedOnDate as a {@link LocalDate} object
     * 
     * @return the addedOnDate as a {@link LocalDate} object
     */
    public LocalDate getAddedOnDateAsLocalDate() {
        return (addedOnDate != null) ? new LocalDate(addedOnDate) : null;
    }

    /**
     * Returns the deliveredOnDate as a {@link LocalDate} object
     * 
     * @return the deliveredOnDate as a {@link LocalDate} object
     */
    public LocalDate getDeliveredOnDateAsLocalDate() {
        return (deliveredOnDate != null) ? new LocalDate(deliveredOnDate) : null;
    }
}

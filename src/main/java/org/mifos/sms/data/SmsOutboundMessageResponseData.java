package org.mifos.sms.data;

import org.joda.time.LocalDate;

/** 
 * Immutable data object representing an outbound SMS message API response data
 * 
 * @author Emmanuel Nnaa 
 **/
public class SmsOutboundMessageResponseData {
	
	private Long id;
	private Long externalId;
	private LocalDate addedOnDate;
	private LocalDate deliveredOnDate;
	private Integer deliveryStatus;
	private Boolean hasError;
	private String errorMessage;
	
	/** 
	 * SmsOutboundMessageResponseData constructor
	 * 
	 * @return void 
	 **/
	private SmsOutboundMessageResponseData(Long id, Long externalId, LocalDate addedOnDate, LocalDate deliveredOnDate, 
			Integer deliveryStatus, Boolean hasError, String errorMessage) {
		this.id = id;
		this.externalId = externalId;
		this.addedOnDate = addedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus;
		this.hasError = hasError;
		this.errorMessage = errorMessage;
	}
	
	/** 
	 * Default SmsOutboundMessageResponseData constructor 
	 * 
	 * @return void
	 **/
	protected SmsOutboundMessageResponseData() {}
	
	/** 
	 * @return an instance of the SmsOutboundMessageResponseData class
	 **/
	public static SmsOutboundMessageResponseData getInstance(Long id, Long externalId, LocalDate addedOnDate, LocalDate deliveredOnDate, 
			Integer deliveryStatus, Boolean hasError, String errorMessage) {
		
		return new SmsOutboundMessageResponseData(id, externalId, addedOnDate, deliveredOnDate, deliveryStatus, hasError, errorMessage);
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the externalId
	 */
	public Long getExternalId() {
		return externalId;
	}

	/**
	 * @return the addedOnDate
	 */
	public LocalDate getAddedOnDate() {
		return addedOnDate;
	}

	/**
	 * @return the deliveredOnDate
	 */
	public LocalDate getDeliveredOnDate() {
		return deliveredOnDate;
	}

	/**
	 * @return the deliveryStatus
	 */
	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}

	/**
	 * @return the hasError
	 */
	public Boolean getHasError() {
		return hasError;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
}

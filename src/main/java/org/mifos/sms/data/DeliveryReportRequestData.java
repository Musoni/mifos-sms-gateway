package org.mifos.sms.data;

import java.util.List;

/** 
 * Immutable data object representing the request body of a delivery report request 
 * 
 * @author Emmanuel Nnaa
 **/
public class DeliveryReportRequestData {
	
	private List<Long> externalIds;
	private String mifosTenantIdentifier;
	
	/** 
	 * DeliveryReportRequestData constructor
	 * 
	 * @return void 
	 **/
	private DeliveryReportRequestData(List<Long> externalIds, String mifosTenantIdentifier) {
		this.externalIds = externalIds;
		this.mifosTenantIdentifier = mifosTenantIdentifier;
	}
	
	/** 
	 * DeliveryReportRequestData constructor
	 * 
	 * @return void 
	 **/
	protected DeliveryReportRequestData() {}
	
	/** 
	 * get an instance of the DeliveryReportRequestData class
	 * 
	 * @return instance of DeliveryReportRequestData
	 **/
	public static DeliveryReportRequestData getInstance(List<Long> externalIds, String mifosTenantIdentifier) {
		return new DeliveryReportRequestData(externalIds, mifosTenantIdentifier);
	}

	/**
	 * @return the externalIds
	 */
	public List<Long> getExternalIds() {
		return externalIds;
	}

	/**
	 * @return the mifosTenantIdentifier
	 */
	public String getMifosTenantIdentifier() {
		return mifosTenantIdentifier;
	}
}

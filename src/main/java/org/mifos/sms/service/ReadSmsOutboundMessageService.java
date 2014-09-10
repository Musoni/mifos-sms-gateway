package org.mifos.sms.service;

import java.util.Collection;
import java.util.List;

import org.mifos.sms.data.SmsOutboundMessageResponseData;

/** 
 * Read outbound SMS message service interface 
 * 
 * @author Emmanuel Nnaa
 **/
public interface ReadSmsOutboundMessageService {
	
	/**
	 * @param externalIds the list of external IDs
	 * @param mifosTenantIdentifier mifos tenant identifier string
	 * @return collection of SmsOutboundMessageResponseData objects whose id is in the externalIds list and 
	 * mifosTenantIdentifier matches the one provided
	 **/
	public Collection<SmsOutboundMessageResponseData> getAll(List<Long> externalIds, String mifosTenantIdentifier);
}

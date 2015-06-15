package org.mifos.sms.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SmsOutboundMessageRepository extends JpaRepository<SmsOutboundMessage, Long>, JpaSpecificationExecutor<SmsOutboundMessage> {
	
    /** 
     * find {@link SmsMessageStatusType} objects by delivery status
     * 
     * @param deliveryStatus -- {@link SmsMessageStatusType} deliveryStatus
     * @param pageable -- Abstract interface for pagination information.
     * @return List of {@link SmsMessageStatusType} list
     **/
    List<SmsOutboundMessage> findByDeliveryStatus(Integer deliveryStatus, Pageable pageable);
	
	/** 
	 * find {@link SmsMessageStatusType} object by externalId
	 * 
	 * @param externalId -- {@link SmsMessageStatusType} externalId
	 * @return {@link SmsMessageStatusType}
	 **/
	SmsOutboundMessage findByExternalId(String externalId);
	
	/** 
	 * find {@link SmsMessageStatusType} objects with id in "idList" and mifosTenantIdentifier equal to "mifosTenantIdentifier"
	 * 
	 * @param idList -- {@link SmsMessageStatusType} id list
	 * @param mifosTenantIdentifier -- Mifos X tenant identifier e.g. demo
	 * @return List of {@link SmsMessageStatusType} objects
	 **/
	List<SmsOutboundMessage> findByIdInAndMifosTenantIdentifier(List<Long> idList, String mifosTenantIdentifier);
}

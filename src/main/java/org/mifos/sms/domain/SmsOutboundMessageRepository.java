package org.mifos.sms.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SmsOutboundMessageRepository extends JpaRepository<SmsOutboundMessage, Long>, JpaSpecificationExecutor<SmsOutboundMessage> {
	@Query("from SmsOutboundMessage msg where msg.deliveryStatus = :deliveryStatus")
	List<SmsOutboundMessage> findByDeliveryStatus(@Param("deliveryStatus") Integer deliveryStatus, Pageable pageable);
	
	@Query("from SmsOutboundMessage msg where msg.externalId = :externalId")
	SmsOutboundMessage findByExternalId(@Param("externalId") String externalId);
}

package org.mifos.sms.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConfigurationRepository extends JpaRepository<Configuration, String>, JpaSpecificationExecutor<Configuration> {

}

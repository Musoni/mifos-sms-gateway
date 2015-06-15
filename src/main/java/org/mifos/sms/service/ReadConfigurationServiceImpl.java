package org.mifos.sms.service;

import java.util.ArrayList;
import java.util.Collection;

import org.mifos.sms.data.ConfigurationData;
import org.mifos.sms.domain.Configuration;
import org.mifos.sms.domain.ConfigurationRepository;
import org.mifos.sms.domain.ConfigurationRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 
 * Implementation of the read configuration service interface.
 * Fetches configuration data object(s) from the configuration table 
 * 
 * @author Emmanuel Nnaa
 **/
@Service
public class ReadConfigurationServiceImpl implements ReadConfigurationService {
	private final ConfigurationRepository configurationRepository;
	private final ConfigurationRepositoryWrapper configurationRepositoryWrapper;
	
	/** 
	 * ReadConfigurationServiceImpl constructor 
	 * 
	 * @param jdbcTemplate JdbcTemplate object
	 * @return void
	 **/
	@Autowired
	public ReadConfigurationServiceImpl(ConfigurationRepositoryWrapper configurationRepositoryWrapper) {
		this.configurationRepository = configurationRepositoryWrapper.getConfigurationRepository();
		this.configurationRepositoryWrapper = configurationRepositoryWrapper;
	}
	
	@Override
	public Collection<ConfigurationData> findAll() {
	    Collection<Configuration> configurationCollection = this.configurationRepository.findAll();
	    Collection<ConfigurationData> configurationDataCollection = new ArrayList<>();
	    
		for (Configuration configuration : configurationCollection) {
		    configurationDataCollection.add(configuration.toData());
		}
	    
		return configurationDataCollection;
	}

	@Override
	public ConfigurationData findOne(final String name) {
		final Configuration configuration = this.configurationRepositoryWrapper.findOneThrowExceptionIfNotFound(name);
		
		return configuration.toData();
	}
}

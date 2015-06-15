package org.mifos.sms.domain;

import org.mifos.sms.exception.ConfigurationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationRepositoryWrapper {
    private final ConfigurationRepository configurationRepository;
    
    @Autowired
    public ConfigurationRepositoryWrapper(final ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }
    
    public Configuration findOneThrowExceptionIfNotFound(String name) {
        final Configuration configuration = this.configurationRepository.findOne(name);
        
        if (configuration == null) {
            throw new ConfigurationNotFoundException("Configuration with name " + name + " not found.");
        }
        
        return configuration;
    }
    
    public ConfigurationRepository getConfigurationRepository() {
        return this.configurationRepository;
    }
}

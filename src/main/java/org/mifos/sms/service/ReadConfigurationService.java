package org.mifos.sms.service;

import java.util.Collection;

import org.mifos.sms.data.ConfigurationData;

/** 
 * Read configuration service interface 
 * 
 * @author Emmanuel Nnaa
 **/
public interface ReadConfigurationService {
	
	/** 
	 * Get all ConfigurationData objects from the configuration table
	 * 
	 * @return collection of ConfigurationData objects
	 **/
	public Collection<ConfigurationData> getAll();
	
	/**
	 * get ConfigurationData object with name matching the one provided
	 *  
	 * @param name the name of the configuration
	 * @return ConfigurationData object 
	 **/
	public ConfigurationData get(String name);
}

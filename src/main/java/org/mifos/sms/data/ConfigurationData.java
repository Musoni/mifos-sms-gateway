package org.mifos.sms.data;

/** 
 * Immutable data object representing a configuration data
 * 
 * @author Emmanuel Nnaa 
 **/
public class ConfigurationData {
	
	private String name;
	private String value;

	/** 
	 * ConfigurationData constructor
	 * 
	 * @return void
	 **/
	protected ConfigurationData() {}
	
	/** 
	 * ConfigurationData constructor
	 * 
	 * @return void
	 **/
	private ConfigurationData(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/** 
	 * @return an instance of the ConfigurationData class
	 **/
	public static ConfigurationData getInstance(String name, String value) {
		return new ConfigurationData(name, value);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
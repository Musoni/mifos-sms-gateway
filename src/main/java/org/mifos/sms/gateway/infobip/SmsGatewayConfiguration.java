package org.mifos.sms.gateway.infobip;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mifos.sms.data.ConfigurationData;

/** 
 * Class methods each return an SMS gateway configuration property stored in the "configuration" table 
 **/
public class SmsGatewayConfiguration {
	private Boolean developmentMode;
	private String systemId;
	private String hostname;
	private Integer portNumber;
	private String password;
	private Boolean enableOutboundMessageScheduler;
	
	/** 
	 * SmsGatewayConfiguration constructor
	 * 
	 * @param configurationData collection of ConfigurationData objects
	 * @return void
	 **/
	public SmsGatewayConfiguration(Collection<ConfigurationData> configurationDataSet) {
		Map<String, String> configurationMap = new HashMap<>();
		Iterator<ConfigurationData> iterator = configurationDataSet.iterator();
		
		while(iterator.hasNext()) {
			ConfigurationData configurationData = iterator.next();
			
    		configurationMap.put(configurationData.getName(), configurationData.getValue());
    	}
		
		developmentMode = Boolean.valueOf(configurationMap.get("DEVELOPMENT_MODE"));
		systemId = configurationMap.get("SMS_GATEWAY_SYSTEM_ID");
		hostname = configurationMap.get("SMS_GATEWAY_HOSTNAME");
		portNumber = Integer.parseInt(configurationMap.get("SMS_GATEWAY_PORT"));
		password = configurationMap.get("SMS_GATEWAY_PASSWORD");
		enableOutboundMessageScheduler = Boolean.valueOf(configurationMap.get("ENABLE_OUTBOUND_MESSSAGE_SCHEDULER"));
	}
	
	/** 
	 * @return SMS gateway configuration "development mode" property 
	 **/
	public Boolean getDevelopmentMode() {
		return developmentMode;
	}
	
	/** 
	 * @return SMS gateway configuration "system ID" property 
	 **/
	public String getSystemId() {
		return systemId;
	}
	
	/** 
	 * @return SMS gateway configuration "hostname" property 
	 **/
	public String getHostname() {
		return hostname;
	}
	
	/** 
	 * @return SMS gateway configuration "password" property 
	 **/
	public String getPassword() {
		return password;
	}
	
	/** 
	 * @return SMS gateway configuration "port number" property 
	 **/
	public Integer getPortNumber() {
		return portNumber;
	}
	
	/** 
	 * @return SMS gateway configuration "enable outbound message schedule" property 
	 **/
	public Boolean getEnableOutboundMessageScheduler() {
		return enableOutboundMessageScheduler;
	}
}

package org.mifos.sms.gateway.infobip;

/** 
 * SMPP server simulator predefined enum constants (connection credentials)
 * 
 * @author Emmanuel Nnaa 
 **/
public enum SMPPServerSimulatorConfiguration {
	SMS_GATEWAY_SYSTEM_ID("test", "SMPPServerSimulatorConfiguration.smsGatewaySystemId"),
	SMS_GATEWAY_HOSTNAME("localhost", "SMPPServerSimulatorConfiguration.smsGatewayHostname"),
	SMS_GATEWAY_PORT(8056, "SMPPServerSimulatorConfiguration.smsGatewayPort"),
	SMS_GATEWAY_PASSWORD("test", "SMPPServerSimulatorConfiguration.smsGatewayPassword");

	private final Object value;
    private final String code;
    
    /** 
     * SMPPServerSimulatorConfiguration constructor  
     **/
    private SMPPServerSimulatorConfiguration(final Object value, final String code) {
        this.value = value;
        this.code = code;
    }

    /** 
     * @return enum constant value 
     **/
    public Object getValue() {
        return this.value;
    }

    /** 
     * @return enum constant 
     **/
    public String getCode() {
        return this.code;
    }
}

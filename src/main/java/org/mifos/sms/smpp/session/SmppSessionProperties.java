package org.mifos.sms.smpp.session;

import org.jsmpp.bean.BindType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.TypeOfNumber;
import org.mifos.sms.gateway.infobip.SmsGatewayConfiguration;
import org.mifos.sms.smpp.server.SmppServerSimulatorConfiguration;

public class SmppSessionProperties {
    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType = null;
    
    private final BindType bindType = BindType.BIND_TRX;
    private final TypeOfNumber addrTon = TypeOfNumber.UNKNOWN;
    private final NumberingPlanIndicator addrNpi = NumberingPlanIndicator.UNKNOWN;
    private final String addressRange = null;
    
    /**
     * {@link SmppSessionProperties} constructor
     * 
     * @param smsGatewayConfiguration
     */
    public SmppSessionProperties(final SmsGatewayConfiguration smsGatewayConfiguration) {
        this.port = smsGatewayConfiguration.getPortNumber();
        this.host = smsGatewayConfiguration.getHostname();
        this.systemId = smsGatewayConfiguration.getSystemId();
        this.password = smsGatewayConfiguration.getPassword();
        
        if (smsGatewayConfiguration.inDebugMode()) {
            final SmppServerSimulatorConfiguration smppServerSimulatorConfiguration = new SmppServerSimulatorConfiguration();
            
            this.port = smppServerSimulatorConfiguration.getPort();
            this.host = smppServerSimulatorConfiguration.getHost();
            this.systemId = smppServerSimulatorConfiguration.getSystemId();
            this.password = smppServerSimulatorConfiguration.getPassword();
            this.systemType = smppServerSimulatorConfiguration.getSystemType();
        }
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the systemId
     */
    public String getSystemId() {
        return systemId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the systemType
     */
    public String getSystemType() {
        return systemType;
    }

    /**
     * @return the bindType
     */
    public BindType getBindType() {
        return bindType;
    }

    /**
     * @return the addrTon
     */
    public TypeOfNumber getAddrTon() {
        return addrTon;
    }

    /**
     * @return the addrNpi
     */
    public NumberingPlanIndicator getAddrNpi() {
        return addrNpi;
    }

    /**
     * @return the addressRange
     */
    public String getAddressRange() {
        return addressRange;
    }
}

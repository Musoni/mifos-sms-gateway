package org.mifos.sms.smpp.server;

public class SmppServerSimulatorConfiguration {
    private final String host = "localhost";
    private final int port = 8056;
    private final String systemId = "test";
    private final String password = "test";
    private final String systemType = "cp";
    
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
}

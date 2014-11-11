package org.mifos.sms.service;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.mifos.sms.gateway.infobip.SmsGatewayHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Service
public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ContextClosedEventListener.class);
    private final SmsGatewayHelper smsGatewayHelper;
    
    @Autowired
    public ContextClosedEventListener(final SmsGatewayHelper smsGatewayHelper) {
        this.smsGatewayHelper = smsGatewayHelper;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        shutDowncleanUpThreadAndDeregisterJDBCDrivers();
        unbindAndCloseSMPPSession();
    }
    
    /** 
     * perform a clean shutdown of JDBC connection threads
     * 
     * @return void
     **/
    private void shutDowncleanUpThreadAndDeregisterJDBCDrivers() {
        try {
            AbandonedConnectionCleanupThread.shutdown();
            logger.info("Shutdown of AbandonedConnectionCleanupThread successful");
        }
        
        catch(Throwable throwable) {
            logger.error("Exception occurred while shut-down of AbandonedConnectionCleanupThread", throwable);
        }
        
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            
            try {
                java.sql.DriverManager.deregisterDriver(driver);
                logger.info("JDBC driver deregistered successfully");
            } 
            
            catch (Throwable throwable) {
                logger.error("Exception occured while deregistering jdbc driver", throwable);
            }
        }
        
        try {
            Thread.sleep(2000L);
        } 
        
        catch (Exception exception) {
            
        }
    }
    
    /** 
     * unbind and close currently active SMPP session, do not allow reconnection 
     * 
     * @return void
     **/
    private void unbindAndCloseSMPPSession() {
        this.smsGatewayHelper.reconnect = false;
        this.smsGatewayHelper.unbindAndCloseSession();
    }
}

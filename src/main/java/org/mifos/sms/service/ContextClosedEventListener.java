package org.mifos.sms.service;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

@Service
public class ContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {
    private final static Logger logger = LoggerFactory.getLogger(ContextClosedEventListener.class);
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        this.shutDowncleanUpThreadAndDeregisterJDBCDrivers();
        this.stopSmppSessionLifecycle();
    }
    
    /** 
     * perform a clean shutdown of JDBC connection threads
     * 
     * @return void
     **/
    private void shutDowncleanUpThreadAndDeregisterJDBCDrivers() {
        try {
            AbandonedConnectionCleanupThread.shutdown();
            
            logger.info("Abandoned connection cleanup thread successfully shutdown");
            
        } catch(Throwable throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
        
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            
            try {
                java.sql.DriverManager.deregisterDriver(driver);
                
                logger.info("JDBC driver deregistered successfully");
                
            } catch (Throwable throwable) {
                logger.error("Exception occured while deregistering jdbc driver", throwable);
            }
        }
        
        try {
            Thread.sleep(2000L);
            
        } catch (Exception exception) {
            
        }
    }
    
    /**
     * Stops the SmppSessionLifecycle class
     */
    private void stopSmppSessionLifecycle() {
        SmppSessionLifecycle.getInstance().stop();
    }
}

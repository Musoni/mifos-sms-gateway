package org.mifos.sms.exception;

/** 
 * Exception is thrown during the normal operation of the Java Virtual Machine when a query for configuration
 * returns an empty result set
 * 
 * @author Emmanuel Nnaa
 **/
public class ConfigurationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6480693364997530945L;

    /** 
	 * ConfigurationNotFoundException constructor
	 * 
	 * @param message the exception message
	 * @return void
	 **/
	public ConfigurationNotFoundException(String message) {
		super(message);
	}
}

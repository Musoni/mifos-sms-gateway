package org.mifos.sms.helper;

/** 
 * HTTP response status code predefined enum constants
 * 
 * @author Emmanuel Nnaa
 **/
public enum HttpResponseStatusCode {
	UNKNOWN(0, "httpResponseStatusCode.unknown"),
	BAD_REQUEST(400, "httpResponseStatusCode.badRequest"),
	UNAUTHORIZED(401, "httpResponseStatusCode.unauthorized"),
	FORBIDDEN(403, "httpResponseStatusCode.forbidden"),
	NOT_FOUND(404, "httpResponseStatusCode.notFound"),
	INTERNAL_SERVER_ERROR(500, "httpResponseStatusCode.internalServerError"),
	OK(200, "httpResponseStatusCode.ok");
	
	private final Integer value;
    private final String code;
    
    /** 
     * get enum constant by value
     * 
     * @param statusValue the value of the enum constant
     * @return enum constant
     **/
    public static HttpResponseStatusCode fromInt(final Integer statusValue) {

    	HttpResponseStatusCode enumeration = HttpResponseStatusCode.UNKNOWN;
    	
        switch (statusValue) {
            case 200:
                enumeration = HttpResponseStatusCode.OK;
            break;
            case 400:
                enumeration = HttpResponseStatusCode.BAD_REQUEST;
            break;
            case 401:
                enumeration = HttpResponseStatusCode.UNAUTHORIZED;
            break;
            case 403:
                enumeration = HttpResponseStatusCode.FORBIDDEN;
            break;
            case 404:
                enumeration = HttpResponseStatusCode.NOT_FOUND;
            break;
            case 500:
                enumeration = HttpResponseStatusCode.INTERNAL_SERVER_ERROR;
            break;
        }
        
        return enumeration;
    }
    
    /** 
     * HttpResponseStatusCode constructor  
     **/
    private HttpResponseStatusCode(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    /** 
     * @return enum constant value 
     **/
    public Integer getValue() {
        return this.value;
    }

    /** 
     * @return enum constant 
     **/
    public String getCode() {
        return this.code;
    }
}

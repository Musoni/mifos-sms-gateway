package org.mifos.sms.data;

import java.util.Collection;

public class JsonDeserializationData {
    private final Collection<Object> objects;
    private final Object object;
    private final boolean hasError;
    private final String errorMessage;
    
    private JsonDeserializationData(final Collection<Object> objects, final Object object, final boolean hasError, 
            final String errorMessage) {
        this.objects = objects;
        this.hasError = hasError;
        this.errorMessage = errorMessage;
        this.object = object;
    }

    public static JsonDeserializationData instance(final Collection<Object> objects, final Object object, final boolean hasError, 
            final String errorMessage) {
        return new JsonDeserializationData(objects, object, hasError, errorMessage);
    }

    /**
     * @return the objects
     */
    public Collection<Object> getObjects() {
        return objects;
    }


    /**
     * @return the hasError
     */
    public boolean hasError() {
        return hasError;
    }


    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }


    /**
     * @return the object
     */
    public Object getObject() {
        return object;
    }
}

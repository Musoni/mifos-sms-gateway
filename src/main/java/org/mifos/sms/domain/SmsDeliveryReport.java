package org.mifos.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.mifos.sms.data.SmsDeliveryStatus;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "smsDeliveryReport")
public class SmsDeliveryReport extends AbstractPersistable<Long> {
    private static final long serialVersionUID = 1439495714190252775L;
    
    @Column(name = "messageId")
    private String messageId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "doneOnDate")
    private Date doneOnDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "submittedOnDate")
    private Date submittedOnDate;
    
    @Column(name = "statusId")
    private Integer statusId;
    
    @Column(name = "errorCode")
    private String errorCode;
    
    /**
     * no-args constructor
     */
    protected SmsDeliveryReport() { }

    /**
     * @param messageId
     * @param doneOnDate
     * @param submittedOnDate
     * @param statusId
     * @param errorCode
     */
    private SmsDeliveryReport(final String messageId, final Date doneOnDate, final Date submittedOnDate, 
            final Integer statusId, final String errorCode) {
        this.messageId = messageId;
        this.doneOnDate = doneOnDate;
        this.submittedOnDate = submittedOnDate;
        this.statusId = statusId;
        this.errorCode = errorCode;
    }
    
    /**
     * Creates a new {@link SmsDeliveryReport} object
     * 
     * @param messageId
     * @param doneOnDate
     * @param submittedOnDate
     * @param statusId
     * @param errorCode
     * @return {@link SmsDeliveryReport} object
     */
    public static SmsDeliveryReport instance(final String messageId, final Date doneOnDate, final Date submittedOnDate, 
            final Integer statusId, final String errorCode) {
        return new SmsDeliveryReport(messageId, doneOnDate, submittedOnDate, statusId, errorCode);
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }
    
    /**
     * @return {@link SmsDeliveryStatus} representation of the status
     */
    public SmsDeliveryStatus getSmsDeliveryStatus() {
        return SmsDeliveryStatus.instance(this.statusId);
    }

    /**
     * @return the doneOnDate
     */
    public Date getDoneOnDate() {
        return doneOnDate;
    }

    /**
     * @return the submittedOnDate
     */
    public Date getSubmittedOnDate() {
        return submittedOnDate;
    }

    /**
     * @return the statusId
     */
    public Integer getStatusId() {
        return statusId;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }
}

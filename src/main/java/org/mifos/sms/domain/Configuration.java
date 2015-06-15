package org.mifos.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifos.sms.data.ConfigurationData;

@Entity
@Table(name = "configuration")
public class Configuration {

    @Id
    private String name;
    
    @Column(name = "value", nullable = false)
    private String value;

    
    protected Configuration() { }
    
    public Configuration(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public ConfigurationData toData() {
        return ConfigurationData.getInstance(this.name, this.value);
    }
}

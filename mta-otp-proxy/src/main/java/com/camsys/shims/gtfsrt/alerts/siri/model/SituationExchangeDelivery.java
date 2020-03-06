package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SituationExchangeDelivery", propOrder = {
        "responseTimestamp",
        "status",
        "situations"
})
public class SituationExchangeDelivery {

    @XmlElement(name = "ResponseTimestamp")
    private Date responseTimestamp;
    @XmlElement(name = "Status")
    private String status;
    @XmlElement(name = "Situations")
    private Situations situations = new Situations();

    public Date getResponseTimestamp() {
        return responseTimestamp;
    }

    public void setResponseTimestamp(Date responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Situations getSituations() {
        return situations;
    }

    public void setSituations(Situations situations) {
        this.situations = situations;
    }
}

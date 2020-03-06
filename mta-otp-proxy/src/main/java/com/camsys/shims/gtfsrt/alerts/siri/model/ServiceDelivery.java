package com.camsys.shims.gtfsrt.alerts.siri.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceDelivery", propOrder = {
        "responseTimestamp",
        "situationExchangeDelivery"
})
public class ServiceDelivery {

    @XmlElement(name = "ResponseTimestamp")
    private Date responseTimestamp;
    @XmlElement(name = "SituationExchangeDelivery")
    private List<SituationExchangeDelivery> situationExchangeDelivery = new ArrayList<SituationExchangeDelivery>();

    public void setResponseTimestamp(Date responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
    }
    public Date getResponseTimestamp() { return responseTimestamp; }

    public void setSituationExchangeDelivery(List<SituationExchangeDelivery> sed) {
        situationExchangeDelivery = sed;
    }
    public List<SituationExchangeDelivery> getSituationExchangeDelivery() {
        return situationExchangeDelivery;
    }
}

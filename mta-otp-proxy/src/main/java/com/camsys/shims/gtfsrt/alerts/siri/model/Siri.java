package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * GMS Siri goes off-script and adds some custom elements.
 *
 *  Because of that we need to model Siri here instead of via JAXB
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Siri")
public class Siri {

    @XmlElement(name = "ServiceDelivery")
    private ServiceDelivery serviceDelivery;

    public void setServiceDelivery(ServiceDelivery serviceDelivery) {
        this.serviceDelivery = serviceDelivery;
    }

    public ServiceDelivery getServiceDelivery() {
        return serviceDelivery;
    }
}

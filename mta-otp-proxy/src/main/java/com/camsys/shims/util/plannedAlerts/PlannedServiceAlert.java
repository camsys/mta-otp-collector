package com.camsys.shims.util.plannedAlerts;


import uk.org.siri.siri.ServiceRequest;
import uk.org.siri.siri.ServiceRequestStructure;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "",
        propOrder = {"getstatus4Response"}
)
@XmlRootElement(
        name = "body"
)

public class PlannedServiceAlert {
    @XmlElement(
            name = "Getstatus4Response"
    )
    protected Getstatus4ResponseDeliveryStructure getstatus4Response;

    public PlannedServiceAlert() {
    }

    public Getstatus4ResponseDeliveryStructure getGetstatus4ResponseDeliveryStructure() {
        return this.getstatus4Response;
    }

    public void setGetstatus4ResponseDeliveryStructure(Getstatus4ResponseDeliveryStructure value) {
        this.getstatus4Response = value;
    }

}
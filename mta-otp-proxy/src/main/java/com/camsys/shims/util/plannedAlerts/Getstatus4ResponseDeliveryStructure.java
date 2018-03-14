package com.camsys.shims.util.plannedAlerts;

import uk.org.siri.siri.AbstractServiceDeliveryStructure;
import uk.org.siri.siri.ProductionTimetableDeliveryStructure;
import uk.org.siri.siri.ServiceDelivery;
import uk.org.siri.siri.ServiceDeliveryStructure;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Getstatus4Response",
        propOrder = {"statuses", "routes", "stops"}
)
@XmlRootElement(
        name = "Getstatus4Response"
)
public class Getstatus4ResponseDeliveryStructure extends AbstractServiceDeliveryStructure {

    @XmlElement(
            name = "Statuses"
    )
    protected List<PlannedServiceAlertStatusesStructure> statusesStructure;

    @XmlElement(
            name = "Routes"
    )
    protected List<PlannedServiceAlertRoutesStructure> routesStructure;

    @XmlElement(
            name = "Stops"
    )
    protected List<PlannedServiceAlertStopsStructure> stopsStructure;

    public Getstatus4ResponseDeliveryStructure() {
    }

    public List<PlannedServiceAlertStatusesStructure> getStatusesStructure() {
        return this.statusesStructure;
    }

    public void setStatusesStructure(List<PlannedServiceAlertStatusesStructure> value) {
        this.statusesStructure = value;
    }

    public List<PlannedServiceAlertRoutesStructure> getRoutesStructure() {
        return this.routesStructure;
    }

    public void setRoutesStructure(List<PlannedServiceAlertRoutesStructure> value) {
        this.routesStructure = value;
    }

    public List<PlannedServiceAlertStopsStructure> getStopsStructure() {
        return this.stopsStructure;
    }

    public void gsetStopsStructure(List<PlannedServiceAlertStopsStructure> value) {
        this.stopsStructure = value;
    }
}

package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Affects {

    @XmlElement(name = "VehicleJourneys")
    private List<VehicleJourneys> vehicleJourneys = new ArrayList<VehicleJourneys>();

    public List<VehicleJourneys> getVehicleJourneys() {
        return vehicleJourneys;
    }

    public void setVehicleJourneys(List<VehicleJourneys> vehicleJourneys) {
        this.vehicleJourneys = vehicleJourneys;
    }

}

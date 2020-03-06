package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleJourneys {

    @XmlElement(name = "AffectedVehicleJourney")
    private List<AffectedVehicleJourney> affectedVehicleJourney = new ArrayList<AffectedVehicleJourney>();

    public List<AffectedVehicleJourney> getAffectedVehicleJourney() {
        return affectedVehicleJourney;
    }

    public void setAffectedVehicleJourney(List<AffectedVehicleJourney> affectedVehicleJourney) {
        this.affectedVehicleJourney = affectedVehicleJourney;
    }


}

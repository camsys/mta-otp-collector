package com.camsys.shims.gtfsrt.alerts.siri.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AffectedVehicleJourney", propOrder = {
        "lineRef",
        "directionRef",
        "stopRef"
})
public class AffectedVehicleJourney {

    @XmlElement(name = "LineRef", required=false)
    private String lineRef;
    @XmlElement(name = "DirectionRef")
    private String directionRef;
    @XmlElement(name = "StopRef", required=false)
    private String stopRef;

    public String getLineRef() {
        return lineRef;
    }

    public void setLineRef(String lineRef) {
        this.lineRef = lineRef;
    }

    public String getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(String directionRef) {
        this.directionRef = directionRef;
    }

    public String getStopRef() {
        return stopRef;
    }

    public void setStopRef(String stopRef) {
        this.stopRef = stopRef;
    }


}

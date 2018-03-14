package com.camsys.shims.util.plannedAlerts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Stopinfo",
        propOrder = {"id",  "stop", "state", "special", "route", "direction"}
)
public class PlannedServiceAlertStopsStructure {
    @XmlElement(
            name = "Id",
            defaultValue = "true"
    )
    protected int stopId;

    @XmlElement(
            name = "Stop",
            defaultValue = "true"
    )
    protected String stop;

    @XmlElement(
            name = "State",
            defaultValue = "true"
    )
    protected String state;

    @XmlElement(
            name = "Special",
            defaultValue = "true"
    )
    protected String special;

    @XmlElement(
            name = "Route",
            defaultValue = "true"
    )
    protected String route;

    @XmlElement(
            name = "Direction",
            defaultValue = "true"
    )
    protected String direction;

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public String getStop() {
        return stop;
    }

    public void setStop(String stop) {
        this.stop = stop;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

}

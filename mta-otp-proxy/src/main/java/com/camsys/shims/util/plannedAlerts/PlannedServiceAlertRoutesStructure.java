package com.camsys.shims.util.plannedAlerts;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "Routeinfo",
        propOrder = {"id", "route", "direction"}
)
public class PlannedServiceAlertRoutesStructure {
    @XmlElement(
            name = "Id",
            defaultValue = "true"
    )
    protected int routeId;

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

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
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

package com.camsys.shims.service_status.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.util.List;

@JsonPropertyOrder({"lastUpdated", "routeDetails"})
public class ServiceStatus {

    private Date _lastUpdated;

    private List<RouteDetail> _routeDetailList;

    public ServiceStatus(){}

    public ServiceStatus(Date lastUpdated, List<RouteDetail> routeDetailList ){
        _lastUpdated = lastUpdated;
        _routeDetailList = routeDetailList;
    }

    @JsonProperty("lastUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "America/New_York")
    public Date getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        _lastUpdated = lastUpdated;
    }

    @JsonProperty("routeDetails")
    public List<RouteDetail> getRouteDetailList() {
        return _routeDetailList;
    }

    public void setRouteDetailList(List<RouteDetail> routeDetailList) {
        _routeDetailList = routeDetailList;
    }

}

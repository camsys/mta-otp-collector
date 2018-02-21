package com.camsys.shims.service_status.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"lastUpdated", "routeDetails"})
public class ServiceStatus {

    private String _lastUpdated;

    private List<RouteDetail> _routeDetailList;

    public ServiceStatus(){}

    public ServiceStatus(String lastUpdated, List<RouteDetail> routeDetailList ){
        _lastUpdated = lastUpdated;
        _routeDetailList = routeDetailList;
    }

    @JsonProperty("lastUpdated")
    public String getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
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

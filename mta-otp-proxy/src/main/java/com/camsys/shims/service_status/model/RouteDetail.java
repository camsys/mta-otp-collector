package com.camsys.shims.service_status.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteDetail {


    private String routeName;

    private String color;

    private String mode;

    private String agency;

    private String routeId;

    private Integer routeSortOrder;

    private Boolean inService;

    @JsonIgnore
    private Date lastUpdated;

    private List<StatusDetail> statusDetailsList;

    @JsonProperty("route")
    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @JsonProperty("color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @JsonProperty("agency")
    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    @JsonProperty("routeId")
    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @JsonProperty("inService")
    public Boolean isInService() {
        return inService;
    }

    public void setInService(Boolean inService) {
        this.inService = inService;
    }

    @JsonProperty("statusDetails")
    public List<StatusDetail> getStatusDetailsList() {
        return statusDetailsList;
    }

    public void setStatusDetailsList(List<StatusDetail> statusDetailsList) {
        this.statusDetailsList = statusDetailsList;
    }

    @JsonProperty("routeSortOrder")
    public Integer getRouteSortOrder() {
        return routeSortOrder;
    }

    public void setRouteSortOrder(Integer routeSortOrder) {
        this.routeSortOrder = routeSortOrder;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }


}

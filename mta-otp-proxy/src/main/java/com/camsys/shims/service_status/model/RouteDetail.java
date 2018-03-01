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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RouteDetail that = (RouteDetail) o;

        if (routeName != null ? !routeName.equals(that.routeName) : that.routeName != null) return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (mode != null ? !mode.equals(that.mode) : that.mode != null) return false;
        if (agency != null ? !agency.equals(that.agency) : that.agency != null) return false;
        if (routeId != null ? !routeId.equals(that.routeId) : that.routeId != null) return false;
        if (routeSortOrder != null ? !routeSortOrder.equals(that.routeSortOrder) : that.routeSortOrder != null)
            return false;
        if (inService != null ? !inService.equals(that.inService) : that.inService != null) return false;
        return statusDetailsList != null ? statusDetailsList.equals(that.statusDetailsList) : that.statusDetailsList == null;
    }

    @Override
    public int hashCode() {
        int result = routeName != null ? routeName.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (agency != null ? agency.hashCode() : 0);
        result = 31 * result + (routeId != null ? routeId.hashCode() : 0);
        result = 31 * result + (routeSortOrder != null ? routeSortOrder.hashCode() : 0);
        result = 31 * result + (inService != null ? inService.hashCode() : 0);
        result = 31 * result + (statusDetailsList != null ? statusDetailsList.hashCode() : 0);
        return result;
    }
}

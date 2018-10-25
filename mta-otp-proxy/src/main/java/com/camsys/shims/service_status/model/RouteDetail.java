package com.camsys.shims.service_status.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>RouteDetail class.</p>
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteDetail {


    private String routeName;

    private String color;

    private String mode;

    private String agency;

    private String routeId;

    private Integer routeSortOrder;

    private Boolean inService;

    private Integer routeType;

    @JsonIgnore
    private Date lastUpdated;

    private Set<StatusDetail> statusDetails;

    /**
     * <p>Getter for the field <code>routeName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("route")
    public String getRouteName() {
        return routeName;
    }

    /**
     * <p>Setter for the field <code>routeName</code>.</p>
     *
     * @param routeName a {@link java.lang.String} object.
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("color")
    public String getColor() {
        return color;
    }

    /**
     * <p>Setter for the field <code>color</code>.</p>
     *
     * @param color a {@link java.lang.String} object.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * <p>Getter for the field <code>mode</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("mode")
    public String getMode() {
        return mode;
    }

    /**
     * <p>Setter for the field <code>mode</code>.</p>
     *
     * @param mode a {@link java.lang.String} object.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * <p>Getter for the field <code>agency</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("agency")
    public String getAgency() {
        return agency;
    }

    /**
     * <p>Setter for the field <code>agency</code>.</p>
     *
     * @param agency a {@link java.lang.String} object.
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    /**
     * <p>Getter for the field <code>routeId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonProperty("routeId")
    public String getRouteId() {
        return routeId;
    }

    /**
     * <p>Setter for the field <code>routeId</code>.</p>
     *
     * @param routeId a {@link java.lang.String} object.
     */
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    /**
     * <p>isInService.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    @JsonProperty("inService")
    public Boolean isInService() {
        return inService;
    }

    /**
     * <p>Setter for the field <code>inService</code>.</p>
     *
     * @param inService a {@link java.lang.Boolean} object.
     */
    public void setInService(Boolean inService) {
        this.inService = inService;
    }

    /**
     * <p>Getter for the field <code>statusDetails</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @JsonProperty("statusDetails")
    public Set<StatusDetail> getStatusDetails() {
        return statusDetails;
    }

    /**
     * <p>Setter for the field <code>statusDetails</code>.</p>
     *
     * @param statusDetails a {@link java.util.Set} object.
     */
    public void setStatusDetails(Set<StatusDetail> statusDetails) {
        this.statusDetails = statusDetails;
    }

    /**
     * <p>Getter for the field <code>routeSortOrder</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @JsonProperty("routeSortOrder")
    public Integer getRouteSortOrder() {
        return routeSortOrder;
    }

    /**
     * <p>Setter for the field <code>routeSortOrder</code>.</p>
     *
     * @param routeSortOrder a {@link java.lang.Integer} object.
     */
    public void setRouteSortOrder(Integer routeSortOrder) {
        this.routeSortOrder = routeSortOrder;
    }

    /**
     * <p>Getter for the field <code>lastUpdated</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * <p>Setter for the field <code>lastUpdated</code>.</p>
     *
     * @param lastUpdated a {@link java.util.Date} object.
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * <p>Getter for the field <code>routeType</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @JsonProperty("routeType")
    public Integer getRouteType() {
        return routeType;
    }

    /**
     * <p>Setter for the field <code>routeType</code>.</p>
     *
     * @param routeType a {@link java.lang.Integer} object.
     */
    public void setRouteType(Integer routeType) {
        this.routeType = routeType;
    }

    /** {@inheritDoc} */
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
        if (routeType != null ? !routeType.equals(that.routeType) : that.routeType != null)
        return false;
        if (inService != null ? !inService.equals(that.inService) : that.inService != null) return false;
        return statusDetails != null ? statusDetails.equals(that.statusDetails) : that.statusDetails == null;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int result = routeName != null ? routeName.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (mode != null ? mode.hashCode() : 0);
        result = 31 * result + (agency != null ? agency.hashCode() : 0);
        result = 31 * result + (routeId != null ? routeId.hashCode() : 0);
        result = 31 * result + (routeSortOrder != null ? routeSortOrder.hashCode() : 0);
        result = 31 * result + (routeType != null ? routeType.hashCode() : 0);
        result = 31 * result + (inService != null ? inService.hashCode() : 0);
        result = 31 * result + (statusDetails != null ? statusDetails.hashCode() : 0);
        return result;
    }
}

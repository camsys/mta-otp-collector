package com.camsys.shims.service_status.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Date;
import java.util.List;

/**
 * <p>ServiceStatus class.</p>
 *
 */
@JsonPropertyOrder({"lastUpdated", "routeDetails"})
public class ServiceStatus {

    private Date _lastUpdated;

    private List<RouteDetail> _routeDetails;

    /**
     * <p>Constructor for ServiceStatus.</p>
     */
    public ServiceStatus(){}

    /**
     * <p>Constructor for ServiceStatus.</p>
     *
     * @param lastUpdated a {@link java.util.Date} object.
     * @param routeDetails a {@link java.util.List} object.
     */
    public ServiceStatus(Date lastUpdated, List<RouteDetail> routeDetails ){
        _lastUpdated = lastUpdated;
        _routeDetails = routeDetails;
    }

    /**
     * <p>getLastUpdated.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    @JsonProperty("lastUpdated")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "America/New_York")
    public Date getLastUpdated() {
        return _lastUpdated;
    }

    /**
     * <p>setLastUpdated.</p>
     *
     * @param lastUpdated a {@link java.util.Date} object.
     */
    public void setLastUpdated(Date lastUpdated) {
        _lastUpdated = lastUpdated;
    }

    /**
     * <p>getRouteDetails.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @JsonProperty("routeDetails")
    public List<RouteDetail> getRouteDetails() {
        return _routeDetails;
    }

    /**
     * <p>setRouteDetails.</p>
     *
     * @param routeDetails a {@link java.util.List} object.
     */
    public void setRouteDetails(List<RouteDetail> routeDetails) {
        _routeDetails = routeDetails;
    }

}

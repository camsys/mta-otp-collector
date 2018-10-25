package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class Status {
    @JsonProperty("Description")
    private String description;

    @JsonProperty("LiftType")
    private Integer liftType;

    @JsonProperty("StationID")
    private String stationID;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusID")
    private String statusID;

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>liftType</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getLiftType() {
        return liftType;
    }

    /**
     * <p>Setter for the field <code>liftType</code>.</p>
     *
     * @param liftType a {@link java.lang.Integer} object.
     */
    public void setLiftType(Integer liftType) {
        this.liftType = liftType;
    }

    /**
     * <p>Getter for the field <code>stationID</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStationID() {
        return stationID;
    }

    /**
     * <p>Setter for the field <code>stationID</code>.</p>
     *
     * @param stationID a {@link java.lang.String} object.
     */
    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status a {@link java.lang.String} object.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * <p>Getter for the field <code>statusID</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatusID() {
        return statusID;
    }

    /**
     * <p>Setter for the field <code>statusID</code>.</p>
     *
     * @param statusID a {@link java.lang.String} object.
     */
    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }
}

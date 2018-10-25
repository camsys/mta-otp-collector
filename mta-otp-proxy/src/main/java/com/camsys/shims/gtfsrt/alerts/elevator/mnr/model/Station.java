package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class Station {
    @JsonProperty("BranchID")
    private Integer branchID;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("StationID")
    private String stationID;

    @JsonProperty("ZoneStationID")
    private Integer zoneStationID;

    /**
     * <p>Getter for the field <code>branchID</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getBranchID() {
        return branchID;
    }

    /**
     * <p>Setter for the field <code>branchID</code>.</p>
     *
     * @param branchID a {@link java.lang.Integer} object.
     */
    public void setBranchID(Integer branchID) {
        this.branchID = branchID;
    }

    /**
     * <p>Getter for the field <code>stationName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStationName() {
        return stationName;
    }

    /**
     * <p>Setter for the field <code>stationName</code>.</p>
     *
     * @param stationName a {@link java.lang.String} object.
     */
    public void setStationName(String stationName) {
        this.stationName = stationName;
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
     * <p>Getter for the field <code>zoneStationID</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getZoneStationID() {
        return zoneStationID;
    }

    /**
     * <p>Setter for the field <code>zoneStationID</code>.</p>
     *
     * @param zoneStationID a {@link java.lang.Integer} object.
     */
    public void setZoneStationID(Integer zoneStationID) {
        this.zoneStationID = zoneStationID;
    }
}

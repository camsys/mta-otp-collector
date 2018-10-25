package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
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

    public Integer getBranchID() {
        return branchID;
    }

    public void setBranchID(Integer branchID) {
        this.branchID = branchID;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public Integer getZoneStationID() {
        return zoneStationID;
    }

    public void setZoneStationID(Integer zoneStationID) {
        this.zoneStationID = zoneStationID;
    }
}

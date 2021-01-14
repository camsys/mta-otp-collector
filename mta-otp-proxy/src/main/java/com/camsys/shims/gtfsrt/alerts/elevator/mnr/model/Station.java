package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class Station {
    @JsonProperty("BranchID")
    private Integer branchID;

    @JsonProperty("BranchName")
    private String branchName;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("StationID")
    private String stationID;

    @JsonProperty("Elevators")
    private Status[] elevators;

    @JsonProperty("Escalators")
    private Status[] escalators;

    public Integer getBranchID() {
        return branchID;
    }

    public void setBranchID(Integer branchID) {
        this.branchID = branchID;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
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

    public Status[] getElevators() { return elevators; }

    public void setElevators(Status[] elevators) { this.elevators = elevators; }

    public Status[] getEscalators() { return escalators; }

    public void setEscalators(Status[] escalators) { this.escalators = escalators; }
}

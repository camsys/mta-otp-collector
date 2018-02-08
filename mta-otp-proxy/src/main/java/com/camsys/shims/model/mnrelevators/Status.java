package com.camsys.shims.model.mnrelevators;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getLiftType() {
        return liftType;
    }

    public void setLiftType(Integer liftType) {
        this.liftType = liftType;
    }

    public String getStationID() {
        return stationID;
    }

    public void setStationID(String stationID) {
        this.stationID = stationID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }
}

package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 2/1/18.
 */
public class StatusResults {
    @JsonProperty("GetElevatorJsonResult")
    Status[] getElevatorJsonResult;

    public Status[] getGetElevatorJsonResult() {
        return getElevatorJsonResult;
    }

    public void setGetElevatorJsonResult(Status[] getElevatorJsonResult) {
        this.getElevatorJsonResult = getElevatorJsonResult;
    }
}
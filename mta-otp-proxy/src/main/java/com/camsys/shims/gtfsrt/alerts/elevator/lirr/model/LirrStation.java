package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class LirrStation {
    @JsonProperty("8")
    private Integer stationId;

    @JsonProperty("escalator")
    private Elevator[] escalator;

    @JsonProperty("elevator")
    private Elevator[] elevator;

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public Elevator[] getEscalators() {
        return escalator;
    }

    public void setEscalators(Elevator[] escalator) {
        this.escalator = escalator;
    }

    public Elevator[] getElevators() {
        return elevator;
    }

    public void setElevators(Elevator[] elevator) {
        this.elevator = elevator;
    }
}

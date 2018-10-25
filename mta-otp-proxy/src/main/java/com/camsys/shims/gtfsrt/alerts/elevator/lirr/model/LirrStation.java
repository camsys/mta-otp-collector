package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class LirrStation {

    @JsonProperty("escalator")
    private Elevator[] escalator;

    @JsonProperty("elevator")
    private Elevator[] elevator;

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

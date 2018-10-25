package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class LirrStation {

    @JsonProperty("escalator")
    private Elevator[] escalator;

    @JsonProperty("elevator")
    private Elevator[] elevator;

    /**
     * <p>getEscalators.</p>
     *
     * @return an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.Elevator} objects.
     */
    public Elevator[] getEscalators() {
        return escalator;
    }

    /**
     * <p>setEscalators.</p>
     *
     * @param escalator an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.Elevator} objects.
     */
    public void setEscalators(Elevator[] escalator) {
        this.escalator = escalator;
    }

    /**
     * <p>getElevators.</p>
     *
     * @return an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.Elevator} objects.
     */
    public Elevator[] getElevators() {
        return elevator;
    }

    /**
     * <p>setElevators.</p>
     *
     * @param elevator an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.Elevator} objects.
     */
    public void setElevators(Elevator[] elevator) {
        this.elevator = elevator;
    }
}

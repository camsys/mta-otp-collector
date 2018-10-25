package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 2/1/18.
 *
 */
public class StatusResults {
    @JsonProperty("GetElevatorJsonResult")
    Status[] getElevatorJsonResult;

    /**
     * <p>Getter for the field <code>getElevatorJsonResult</code>.</p>
     *
     * @return an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Status} objects.
     */
    public Status[] getGetElevatorJsonResult() {
        return getElevatorJsonResult;
    }

    /**
     * <p>Setter for the field <code>getElevatorJsonResult</code>.</p>
     *
     * @param getElevatorJsonResult an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Status} objects.
     */
    public void setGetElevatorJsonResult(Status[] getElevatorJsonResult) {
        this.getElevatorJsonResult = getElevatorJsonResult;
    }
}

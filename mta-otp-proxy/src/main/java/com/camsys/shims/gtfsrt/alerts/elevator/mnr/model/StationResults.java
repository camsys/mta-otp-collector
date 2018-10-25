package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class StationResults {
    @JsonProperty("GetStationsJsonResult")
    Station[] getStationsJsonResult;

    /**
     * <p>Getter for the field <code>getStationsJsonResult</code>.</p>
     *
     * @return an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Station} objects.
     */
    public Station[] getGetStationsJsonResult() {
        return getStationsJsonResult;
    }

    /**
     * <p>Setter for the field <code>getStationsJsonResult</code>.</p>
     *
     * @param getStationsJsonResult an array of {@link com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Station} objects.
     */
    public void setGetStationsJsonResult(Station[] getStationsJsonResult) {
        this.getStationsJsonResult = getStationsJsonResult;
    }
}

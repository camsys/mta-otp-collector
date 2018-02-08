package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class LirrStations {
    @JsonProperty("Stations")
    LirrStation[] stations;

    public LirrStation[] getGetStationsJsonResult() {
        return this.stations;
    }

    public void setStations(LirrStation[] stations) {
        this.stations = stations;
    }
}

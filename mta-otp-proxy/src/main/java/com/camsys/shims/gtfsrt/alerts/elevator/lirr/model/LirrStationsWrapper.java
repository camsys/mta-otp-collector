package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class LirrStationsWrapper {
    @JsonProperty("Stations")
    LirrStationIds stations;

    public LirrStationIds getStationIds() {
        return this.stations;
    }

    public void setStationsIds(LirrStationIds stations) {
        this.stations = stations;
    }
}

package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class LirrStationsWrapper {
    @JsonProperty("Stations")
    LirrStationIds stations;

    /**
     * <p>getStationIds.</p>
     *
     * @return a {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStationIds} object.
     */
    public LirrStationIds getStationIds() {
        return this.stations;
    }

    /**
     * <p>setStationsIds.</p>
     *
     * @param stations a {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStationIds} object.
     */
    public void setStationsIds(LirrStationIds stations) {
        this.stations = stations;
    }
}

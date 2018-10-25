package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lcaraballo on 1/31/18.
 *
 */
public class LirrStationIds {
    private Map<String, LirrStation> properties = new HashMap<>();

    /**
     * <p>set.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @param value a {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStation} object.
     */
    @JsonAnySetter
    public void set(String fieldName, LirrStation value){
        this.properties.put(fieldName, value);
    }

    /**
     * <p>get.</p>
     *
     * @param fieldName a {@link java.lang.String} object.
     * @return a {@link com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStation} object.
     */
    public LirrStation get(String fieldName){
        return this.properties.get(fieldName);
    }

    /**
     * <p>getStations.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, LirrStation> getStations() {
        return  this.properties;
    }

}

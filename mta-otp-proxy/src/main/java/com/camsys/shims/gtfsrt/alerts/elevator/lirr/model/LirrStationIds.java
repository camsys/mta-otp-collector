package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class LirrStationIds {
    private Map<String, LirrStation> properties = new HashMap<>();

    @JsonAnySetter
    public void set(String fieldName, LirrStation value){
        this.properties.put(fieldName, value);
    }

    public LirrStation get(String fieldName){
        return this.properties.get(fieldName);
    }

    public Map<String, LirrStation> getStations() {
        return  this.properties;
    }

}

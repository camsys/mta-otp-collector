package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 2/8/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Elevator {
    @JsonProperty("LOC")
    private String location;

    @JsonProperty("STAT")
    private String status;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.camsys.shims.gtfsrt.alerts.elevator.lirr.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 2/8/18.
 *
 */
public class Elevator {
    @JsonProperty("LOC")
    private String location;

    @JsonProperty("STAT")
    private String status;

    /**
     * <p>Getter for the field <code>location</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLocation() {
        return location;
    }

    /**
     * <p>Setter for the field <code>location</code>.</p>
     *
     * @param location a {@link java.lang.String} object.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * <p>Getter for the field <code>status</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStatus() {
        return status;
    }

    /**
     * <p>Setter for the field <code>status</code>.</p>
     *
     * @param status a {@link java.lang.String} object.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}

package com.camsys.shims.gtfsrt.tripUpdates.lirr.service;

import org.onebusaway.gtfs.model.AgencyAndId;

import java.io.Serializable;

/**
 * Key for Sign (Solari) update.
 */
public class StopTimeUpdateKey implements Serializable {
    private AgencyAndId tripId;
    private AgencyAndId stopId;

    public StopTimeUpdateKey(AgencyAndId tripId, AgencyAndId stopId) {
        this.tripId = tripId;
        this.stopId = stopId;
    }

    public AgencyAndId getTripId() {
        return tripId;
    }

    public AgencyAndId getStopId() {
        return stopId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof  StopTimeUpdateKey))
            return false;
        StopTimeUpdateKey key = (StopTimeUpdateKey) obj;
        return tripId.equals(key.getTripId())
                && stopId.equals(key.getStopId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * tripId.hashCode();
        result += prime * stopId.hashCode();
        return result;
    }
}

package com.camsys.shims.gtfsrt.tripUpdates.lirr.service;

import org.onebusaway.gtfs.model.AgencyAndId;

/**
 * Bean for Sign (Solari) update.
 */
public class StopTimeUpdateAddon {

    private AgencyAndId tripId;
    private AgencyAndId stopId;
    private String tripHeadsign;
    private String track;
    private int peakCode;
    private String status;

    private long scheduledDeparture;

    private long predictedDeparture;

    public AgencyAndId getTripId() {
        return tripId;
    }

    public void setTripId(AgencyAndId tripId) {
        this.tripId = tripId;
    }

    public AgencyAndId getStopId() {
        return stopId;
    }

    public void setStopId(AgencyAndId stopId) {
        this.stopId = stopId;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public void setTripHeadsign(String tripHeadsign) {
        this.tripHeadsign = tripHeadsign;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public int getPeakCode() {
        return peakCode;
    }

    public void setPeakCode(int peakCode) {
        this.peakCode = peakCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(long scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public long getPredictedDeparture() {
        return predictedDeparture;
    }

    public void setPredictedDeparture(long predictedDeparture) {
        this.predictedDeparture = predictedDeparture;
    }
}

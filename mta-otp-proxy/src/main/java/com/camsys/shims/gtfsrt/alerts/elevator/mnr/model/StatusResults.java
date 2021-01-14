package com.camsys.shims.gtfsrt.alerts.elevator.mnr.model;


/**
 * Created by lcaraballo on 2/1/18.
 */
public class StatusResults {
    Status[] getLiftJsonResult;
    String stationID;

    public Status[] getGetLiftJsonResult() {
        return getLiftJsonResult;
    }

    public void setGetLiftJsonResult(Status[] getLiftJsonResult) {
        this.getLiftJsonResult = getLiftJsonResult;
    }

    public String getStationID() { return stationID; }
    public void setStationID(String stationID) { this.stationID = stationID; }
}
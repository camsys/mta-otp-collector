package com.camsys.shims.model.mnrelevators;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lcaraballo on 1/31/18.
 */
public class StationResults {
    @JsonProperty("GetStationsJsonResult")
    Station[] getStationsJsonResult;

    public Station[] getGetStationsJsonResult() {
        return getStationsJsonResult;
    }

    public void setGetStationsJsonResult(Station[] getStationsJsonResult) {
        this.getStationsJsonResult = getStationsJsonResult;
    }
}

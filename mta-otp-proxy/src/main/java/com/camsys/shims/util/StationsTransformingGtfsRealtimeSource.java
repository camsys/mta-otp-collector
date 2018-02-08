package com.camsys.shims.util;

import com.camsys.shims.model.mnrelevators.Station;
import com.camsys.shims.model.mnrelevators.StationResults;
import com.camsys.shims.model.mnrelevators.Status;
import com.camsys.shims.model.mnrelevators.StatusResults;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcaraballo on 2/1/18.
 */
public class StationsTransformingGtfsRealtimeSource<T> extends TransformingGtfsRealtimeSource{

    private String _statusSourceUrl;

    private Deserializer<T> _statusDeserializer;

    private static final String STATUS_WORKING = "working";

    public void setStatusSourceUrl(String statusSourceUrl) {_statusSourceUrl = statusSourceUrl;}

    public void setStatusDeserializer(Deserializer<T> deserializer) {
        _statusDeserializer = deserializer;
    }

    @Override
    public void update() {
        List<StatusResults> statusResultsList = new ArrayList<>();
        StationResults stationResults = (StationResults)getMessage(_sourceUrl, _deserializer);

        if (stationResults != null) {
            Station[] stations = stationResults.getGetStationsJsonResult();
            for(Station station : stations){
                if(station.getStationID() != null) {
                    StatusResults statusResults = (StatusResults) getMessage(getStatusSourceUrlWithStation(station.getStationID()),_statusDeserializer);
                    if (statusResults != null) {
                        statusResultsList.add(statusResults);
                    }
                }
            }
            if (statusResultsList.size() > 0) {
                _feedMessage = _transformer.transform(statusResultsList);
            }
        }
    }

    private String getStatusSourceUrlWithStation(String stationId){
        return String.format(_statusSourceUrl, stationId);
    }

}

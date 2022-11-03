package com.camsys.shims.gtfsrt.alerts.elevator.mnr.source;


import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Station;
import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.StationResults;
import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.StatusResults;
import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.source.TransformingGtfsRealtimeSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcaraballo on 2/1/18.
 */
public class MetroNorthStationsToGtfsRealtimeSources<T> extends TransformingGtfsRealtimeSource {

    private final Logger _log = LoggerFactory.getLogger(MetroNorthStationsToGtfsRealtimeSources.class);
    private String _statusSourceUrl;

    public void setStatusSourceUrl(String statusSourceUrl) {_statusSourceUrl = statusSourceUrl;}

    @Override
    // this method occasionally backs up -- make sure it doesn't run concurrently
    public synchronized void update() {
        try {
            List<StatusResults> statusResultsList = new ArrayList<>();
            StationResults stationResults = (StationResults) getMessage(_statusSourceUrl, _deserializer);

            if (stationResults != null) {
                for (Station station : stationResults.getGetStationsJsonResult()) {
                    if (station.getStationID() != null) {
                        StatusResults elevatorResults = new StatusResults();
                        elevatorResults.setStationID(station.getStationID());
                        elevatorResults.setGetLiftJsonResult(station.getElevators());
                        if (station.getElevators() != null) {
                            statusResultsList.add(elevatorResults);
                        }

                        StatusResults escalatorResults = new StatusResults();
                        escalatorResults.setStationID(station.getStationID());
                        escalatorResults.setGetLiftJsonResult(station.getEscalators());
                        if (station.getEscalators() != null) {
                            statusResultsList.add(escalatorResults);
                        }
                    }
                }
                if (statusResultsList.size() > 0) {
                    _feedMessage = _transformer.transform(statusResultsList);
                }
            }
        } catch (Throwable t) {
            _log.error("update failed:", t);
        }
    }

    private String getStatusSourceUrlWithStation(String stationId){
        return String.format(_statusSourceUrl, stationId);
    }

}

package com.camsys.shims.gtfsrt.alerts.elevator.mnr.source;


import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Station;
import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.StationResults;
import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.StatusResults;
import com.camsys.shims.util.deserializer.Deserializer;
import com.camsys.shims.util.source.TransformingGtfsRealtimeSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcaraballo on 2/1/18.
 *
 */
public class MetroNorthStationsToGtfsRealtimeSources<T> extends TransformingGtfsRealtimeSource {

    private String _statusSourceUrl;

    private Deserializer<T> _statusDeserializer;

    /**
     * <p>setStatusSourceUrl.</p>
     *
     * @param statusSourceUrl a {@link java.lang.String} object.
     */
    public void setStatusSourceUrl(String statusSourceUrl) {_statusSourceUrl = statusSourceUrl;}

    /**
     * <p>setStatusDeserializer.</p>
     *
     * @param deserializer a {@link com.camsys.shims.util.deserializer.Deserializer} object.
     */
    public void setStatusDeserializer(Deserializer<T> deserializer) {
        _statusDeserializer = deserializer;
    }

    /** {@inheritDoc} */
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

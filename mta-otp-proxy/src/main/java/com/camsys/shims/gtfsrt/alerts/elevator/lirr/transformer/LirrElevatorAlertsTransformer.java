/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.gtfsrt.alerts.elevator.lirr.transformer;

import com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.Elevator;
import com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStation;
import com.camsys.shims.gtfsrt.alerts.elevator.lirr.model.LirrStationsWrapper;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

public class LirrElevatorAlertsTransformer implements GtfsRealtimeTransformer<LirrStationsWrapper> {

    private static Logger _log = LoggerFactory.getLogger(LirrElevatorAlertsTransformer.class);

    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    private static final String STATUS_WORKING = "Working";

    private static final String ELEVATOR_AT = "Elevator at ";

    private static final String OUT_OF_SERVICE = " is out of service.";

    private static final String LIRR_AGENCY = "LI";

    @Override
    public FeedMessage transform(LirrStationsWrapper stationsWrapper) {
        FeedMessage.Builder message = FeedMessage.newBuilder();

        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        Map<String, LirrStation > stations = stationsWrapper.getStationIds().getStations();

        for (Map.Entry<String, LirrStation> entry : stations.entrySet()){
            String stationId = entry.getKey();
            LirrStation station = entry.getValue();

            if (station.getElevators()!= null) {
                for(Elevator elevator : station.getElevators()){
                    if(!elevator.getStatus().equalsIgnoreCase(STATUS_WORKING)) {
                        FeedEntity fe = statusToEntity(stationId, elevator);
                        message.addEntity(fe);
                    }
                }
            }
        }

        return message.build();
    }

    private FeedEntity statusToEntity(String stationId, Elevator elevator) {
        Alert.Builder alert = Alert.newBuilder();
        EntitySelector.Builder informedEntity = EntitySelector.newBuilder();
        informedEntity.setAgencyId(LIRR_AGENCY);
        informedEntity.setStopId(stationId);
        alert.addInformedEntity(informedEntity);
        String desc = ELEVATOR_AT + elevator.getLocation() + OUT_OF_SERVICE;
        alert.setHeaderText(translatedString(desc));
        alert.setDescriptionText(translatedString(desc));
        FeedEntity.Builder builder = FeedEntity.newBuilder()
                .setAlert(alert)
                .setId(stationId);
        return builder.build();
    }


    private long stringToDate(String s) {
        try {
            return _dateFormat.parse(s).getTime() / 1000;
        } catch(ParseException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    private TranslatedString.Builder translatedString(String str) {
        return TranslatedString.newBuilder()
                .addTranslation(TranslatedString.Translation.newBuilder()
                        .setText(str)
                        .setLanguage("en"));
    }
}

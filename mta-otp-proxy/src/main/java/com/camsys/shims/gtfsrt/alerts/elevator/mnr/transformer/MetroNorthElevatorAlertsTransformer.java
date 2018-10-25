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
package com.camsys.shims.gtfsrt.alerts.elevator.mnr.transformer;

import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.Status;
import com.camsys.shims.gtfsrt.alerts.elevator.mnr.model.StatusResults;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * <p>MetroNorthElevatorAlertsTransformer class.</p>
 *
 */
public class MetroNorthElevatorAlertsTransformer implements GtfsRealtimeTransformer<List<StatusResults>> {

    private static Logger _log = LoggerFactory.getLogger(MetroNorthElevatorAlertsTransformer.class);

    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    private static final String OUT_OF_SERVICE = " Elevator is out of service.";

    private static final String WORKING_STATUS = "1";


    /** {@inheritDoc} */
    @Override
    public FeedMessage transform(List<StatusResults> statusResultsList) {
        FeedMessage.Builder message = FeedMessage.newBuilder();

        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        for(StatusResults statusResults : statusResultsList){
            Status[] statuses = statusResults.getGetElevatorJsonResult();
            for (Status status : statuses) {
                if (status != null && !status.getStatusID().equals(WORKING_STATUS)) {
                    FeedEntity fe = statusToEntity(status);
                    message.addEntity(fe);
                }
            }
        }

        return message.build();
    }

    private FeedEntity statusToEntity(Status status) {
        Alert.Builder alert = Alert.newBuilder();
        alert.addInformedEntity(EntitySelector.newBuilder().setStopId(status.getStationID()));
        String desc = status.getDescription() + OUT_OF_SERVICE;
        alert.setHeaderText(translatedString(desc ));
        alert.setDescriptionText(translatedString(desc ));
        FeedEntity.Builder builder = FeedEntity.newBuilder()
                .setAlert(alert)
                .setId(status.getStationID());
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

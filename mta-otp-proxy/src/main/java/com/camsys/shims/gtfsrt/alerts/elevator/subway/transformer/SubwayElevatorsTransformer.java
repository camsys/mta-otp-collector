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
package com.camsys.shims.gtfsrt.alerts.elevator.subway.transformer;

import com.camsys.mta.elevators.NYCOutagesType;
import com.camsys.mta.elevators.OutageType;
import com.camsys.shims.gtfsrt.alerts.elevator.subway.stops_provider.ElevatorToStopsProvider;
import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.Alert;
import com.google.transit.realtime.GtfsRealtime.EntitySelector;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.TranslatedString;
import com.google.transit.realtime.GtfsRealtime.TimeRange;
import com.google.transit.realtime.GtfsRealtimeConstants;
import com.google.transit.realtime.GtfsRealtimeOneBusAway;
import com.google.transit.realtime.GtfsRealtimeOneBusAway.OneBusAwayEntitySelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SubwayElevatorsTransformer implements GtfsRealtimeTransformer<NYCOutagesType> {

    private static Logger _log = LoggerFactory.getLogger(SubwayElevatorsTransformer.class);

    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

    private ElevatorToStopsProvider _stopsProvider;

    @Override
    public FeedMessage transform(NYCOutagesType obj) {
        FeedMessage.Builder message = FeedMessage.newBuilder();

        FeedHeader.Builder header = FeedHeader.newBuilder();
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setTimestamp(System.currentTimeMillis() / 1000);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
        message.setHeader(header);

        List<OutageType> outages = obj.getOutage();

        for (OutageType outage : outages) {
            Collection<String> stopIds = _stopsProvider.getStopsForElevator(outage.getEquipment());
            if (stopIds.isEmpty()) {
                _log.info("No stop for elevator={}", outage.getEquipment());
                continue;
            }
            for (String stopId : stopIds) {
                FeedEntity fe = outageToEntity(outage, stopId);
                message.addEntity(fe);
            }
        }

        _log.info("Adding {} elevators", outages.size());

        return message.build();
    }

    private FeedEntity outageToEntity(OutageType outage, String stopId) {
        Alert.Builder alert = Alert.newBuilder();
        OneBusAwayEntitySelector elevatorExtension = OneBusAwayEntitySelector.newBuilder()
                .setElevatorId(outage.getEquipment()).build();
        EntitySelector.Builder informedEntity = EntitySelector.newBuilder()
                .setStopId(stopId)
                .setExtension(GtfsRealtimeOneBusAway.obaEntitySelector, elevatorExtension);
        alert.addInformedEntity(informedEntity);
        String desc = outageDescription(outage);
        alert.setHeaderText(translatedString(desc));
        alert.setDescriptionText(translatedString(desc));
        alert.addActivePeriod(TimeRange.newBuilder()
                .setStart(stringToDate(outage.getOutagedate()))
                .setEnd(stringToDate(outage.getEstimatedreturntoservice())));
        FeedEntity.Builder builder = FeedEntity.newBuilder()
                .setAlert(alert)
                .setId(stopId + "#" + outage.getEquipment());
        return builder.build();
    }

    private String outageDescription(OutageType outage) {
        return String.format("Elevator outage @ %s: %s [%s]", outage.getStation(), outage.getServing(), outage.getReason());
    }

    private long stringToDate(String s) {
        try {
            Date potentialDate = _dateFormat.parse(s);
            if (potentialDate == null)
                return 0;
            return potentialDate.getTime() / 1000;
        } catch(ParseException ex) {
            _log.error("invalid date value " + s);
            return 0;
        } catch (NumberFormatException nfe) {
            _log.error("illegal date value " + s);
            return 0;
        } catch (NullPointerException npe) {
            _log.error("exception parsing date " + s, npe);
            return 0;
        }
    }

    private TranslatedString.Builder translatedString(String str) {
        return TranslatedString.newBuilder()
                .addTranslation(TranslatedString.Translation.newBuilder()
                        .setText(str)
                        .setLanguage("en"));
    }

    public void setStopsProvider(ElevatorToStopsProvider stopsProvider) {
        _stopsProvider = stopsProvider;
    }
}

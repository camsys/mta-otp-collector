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
package com.camsys.shims.gtfsrt.tripUpdates.mnr.transformer;

import com.camsys.shims.util.transformer.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripDescriptor.ScheduleRelationship;
import com.google.transit.realtime.GtfsRealtimeMNR;
import com.google.transit.realtime.GtfsRealtimeMNR.MnrStopTimeUpdate;
import com.google.transit.realtime.GtfsRealtimeNYCT;
import com.google.transit.realtime.GtfsRealtimeNYCT.NyctStopTimeUpdate;
import com.kurtraschke.nyctrtproxy.model.MatchMetrics;
import com.kurtraschke.nyctrtproxy.model.Status;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MetroNorthTripUpdateTransformer extends TripUpdateTransformer {

    private static final Pattern _sdPattern = Pattern.compile("^(\\d{2})(\\d{2})(\\d{4})$");

    private GtfsRelationalDao _dao;

    private CalendarServiceData _csd;

    private String _agencyId = "MNR";

    private static final Logger _log = LoggerFactory.getLogger(MetroNorthTripUpdateTransformer.class);

    public void setDao(GtfsRelationalDao dao) {
        _dao = dao;
        _csd = new CalendarServiceDataFactoryImpl(_dao).createData();
    }

    public void setAgencyId(String agencyId) {
        _agencyId = agencyId;
    }

    @Override
    public TripUpdate.Builder transformTripUpdate(FeedEntity fe, MatchMetrics matchMetrics) {
        TripUpdate tu = fe.getTripUpdate();
        TripUpdate.Builder tub = TripUpdate.newBuilder();
        String routeId = null, tripShortName = null, startDate = null;
        if (tu.hasTrip() && tu.getTrip().hasRouteId()) {
            routeId = tu.getTrip().getRouteId();
        }
        if (tu.hasTrip() && tu.getTrip().hasStartDate()) {
            startDate = tu.getTrip().getStartDate();
        }
        if (fe.hasVehicle() && fe.getVehicle().hasVehicle() && fe.getVehicle().getVehicle().hasLabel()) {
            tripShortName = fe.getVehicle().getVehicle().getLabel();
        }
        if (routeId == null || tripShortName == null || startDate == null) {
            _log.info("not enough info for tripUpdate routeId={} tripShortName={} startDate={}", routeId, tripShortName, startDate);
            matchMetrics.addStatus(Status.BAD_TRIP_ID);
            return null;
        }
        ServiceDate sd = parseDate(startDate);
        Trip trip = findCorrectTrip(routeId, tripShortName, sd);
        Set<String> stopIds = null;
        if (trip == null) {
            matchMetrics.addStatus(Status.NO_MATCH);
            tub.getTripBuilder().setTripId(tripShortName + "_" + sd.getAsString());
            tub.getTripBuilder().setScheduleRelationship(ScheduleRelationship.ADDED);
        } else {
            tub.getTripBuilder().setTripId(trip.getId().getId());
            stopIds = _dao.getStopTimesForTrip(trip).stream()
                    .map(st -> st.getStop().getId().getId()).collect(Collectors.toSet());
        }

        tub.getTripBuilder().setStartDate(formatDate(sd));
        tub.getTripBuilder().setRouteId(routeId);

        int delay = 0;

        for (StopTimeUpdate stu : tu.getStopTimeUpdateList()) {
            if (stopIds != null && !stopIds.contains(stu.getStopId())) {
                continue;
            }
            StopTimeUpdate.Builder stub = stu.toBuilder();
            MnrStopTimeUpdate ext = stub.getExtension(GtfsRealtimeMNR.mnrStopTimeUpdate);
            if (ext.hasTrack()) {
                NyctStopTimeUpdate.Builder nyctExt = NyctStopTimeUpdate.newBuilder();
                nyctExt.setActualTrack(ext.getTrack());
                stub.setExtension(GtfsRealtimeNYCT.nyctStopTimeUpdate, nyctExt.build());
            }
            if (stub.hasDeparture() && !stub.hasArrival()) {
                stub.setArrival(stub.getDeparture());
            }
            tub.addStopTimeUpdate(stub);
            if (stub.hasDeparture() && stub.getDeparture().hasDelay())
                delay = stub.getDeparture().getDelay();
        }

        tub.setDelay(delay);

        return tub;
    }

    private Trip findCorrectTrip(String route, String tripShortName, ServiceDate sd) {
        if (sd == null)
            return null;
        Route r = _dao.getRouteForId(new AgencyAndId(_agencyId, route));
        Set<AgencyAndId> serviceIds = _csd.getServiceIdsForDate(sd);
        List<Trip> candidates = new ArrayList<>();
        for (Trip t : _dao.getTripsForRoute(r)) {
            if (serviceIds.contains(t.getServiceId()) && t.getTripShortName().equals(tripShortName)) {
                candidates.add(t);
            }
        }
        if (candidates.size() == 1)
            return candidates.get(0);
        else if (candidates.size() == 0)
            _log.error("not enough trips for shortName {}", tripShortName);
        else
            _log.error("Too many trips for shortName {} ({})", tripShortName, candidates);
        return null;
    }

    private ServiceDate parseDate(String date) {
        Matcher matcher = _sdPattern.matcher(date);
        if(!matcher.matches()) {
            _log.info("error parsing date: " + date);
            return null;
        } else {
            int year = Integer.parseInt(matcher.group(3));
            int month = Integer.parseInt(matcher.group(1));
            int day = Integer.parseInt(matcher.group(2));
            return new ServiceDate(year, month, day);
        }
    }

    private String formatDate(ServiceDate sd) {
        return String.format("%04d%02d%02d", sd.getYear(), sd.getMonth(), sd.getDay());
    }

}

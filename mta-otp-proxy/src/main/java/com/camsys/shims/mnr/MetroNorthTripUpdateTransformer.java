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
package com.camsys.shims.mnr;

import com.camsys.shims.util.TripUpdateTransformer;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class MetroNorthTripUpdateTransformer extends TripUpdateTransformer {

    private GtfsRelationalDao _dao;

    private CalendarServiceData _csd;

    private static final Logger _log = LoggerFactory.getLogger(MetroNorthTripUpdateTransformer.class);

    public void setDao(GtfsRelationalDao dao) {
        _dao = dao;
        _csd = new CalendarServiceDataFactoryImpl(_dao).createData();
    }

    @Override
    public TripUpdate.Builder transformTripUpdate(TripUpdate tu) {
        TripUpdate.Builder tub = tu.toBuilder();
        String routeId = null, tripShortName = null, startDate = null;
        if (tub.hasTrip() && tu.getTrip().hasRouteId()) {
            routeId = tu.getTrip().getRouteId();
        }
        if (tub.hasTrip() && tu.getTrip().hasStartDate()) {
            startDate = tu.getTrip().getStartDate();
        }
        if (tub.hasVehicle()) {
            tripShortName = tub.getVehicle().getLabel();
        }
        if (routeId == null || tripShortName == null || startDate == null) {
            _log.info("not enough info for tripUpdate {}", tu);
            return tub;
        }
        Trip trip = findCorrectTrip(routeId, tripShortName, startDate);
        if (trip == null) {
            // error message in findCorrectTrip
            return tub;
        }
        tub.getTripBuilder().setTripId(trip.getId().getId());
        return tub;
    }

    private Trip findCorrectTrip(String route, String tripShortName, String startDate) {
        ServiceDate sd = parseDate(startDate);
        if (sd == null)
            return null;
        Route r = _dao.getRouteForId(new AgencyAndId("MNR", route));
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
        try {
            Date d = new SimpleDateFormat("MMddyyyy").parse(date);
            return new ServiceDate(d);
        } catch(ParseException e) {
            _log.error("Error parsing date " + date);
        }
        return null;
    }

}

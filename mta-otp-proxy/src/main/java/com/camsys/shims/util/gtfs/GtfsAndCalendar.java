package com.camsys.shims.util.gtfs;

import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface GtfsAndCalendar {
    Collection<Route> getAllRoutes();

    Route getRouteForId(AgencyAndId agencyAndId);

    List<Trip> getTripsForRoute(Route route);

    List<Stop> getStopsForTrip(Trip trip);

    Collection<Stop> getAllStops();

    Set<AgencyAndId> getServiceIdsForDate(ServiceDate serviceDate);

    LineString getGeometryForTrip(Trip trip);

    Trip getTripForId(AgencyAndId id);
}

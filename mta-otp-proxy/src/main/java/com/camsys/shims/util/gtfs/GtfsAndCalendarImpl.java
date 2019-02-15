package com.camsys.shims.util.gtfs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GtfsAndCalendarImpl implements GtfsDaoDependency, GtfsAndCalendar {

    private static GeometryFactory _geometryFactory = new GeometryFactory();

    private GtfsRelationalDao _gtfsDao;
    private CalendarServiceData _calendarData;
    private List<String> _routeIdWhiteList = new ArrayList<>();

    public void setGtfsDao(GtfsRelationalDao gtfsDao) {
        _gtfsDao = gtfsDao;
        _calendarData = new CalendarServiceDataFactoryImpl(_gtfsDao).createData();
    }

    public void setRouteIdWhiteList(List<String> routeIdWhiteList) {
        _routeIdWhiteList = routeIdWhiteList;
    }

    public void setRouteIdWhiteListStr(String value) {
        _routeIdWhiteList = Arrays.asList(value.split(","));
    }

    @Override
    public Collection<Route> getAllRoutes(){
        if(_routeIdWhiteList.size() > 0) {
            return _gtfsDao.getAllRoutes().stream()
                    .filter(route -> _routeIdWhiteList.contains(route.getId().toString()))
                    .collect(Collectors.toList());
        }
        return _gtfsDao.getAllRoutes();
    }

    @Override
    public Route getRouteForId(AgencyAndId agencyAndId){
        return _gtfsDao.getRouteForId(agencyAndId);
    }

    @Override
    public List<Trip> getTripsForRoute(Route route){
        return _gtfsDao.getTripsForRoute(route);
    }

    @Override
    public List<Stop> getStopsForTrip(Trip trip) {
        return _gtfsDao.getStopTimesForTrip(trip)
                .stream()
                .map(StopTime::getStop)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Stop> getAllStops() {
        return _gtfsDao.getAllStops();
    }

    @Override
    public Set<AgencyAndId> getServiceIdsForDate(ServiceDate serviceDate){
        return _calendarData.getServiceIdsForDate(serviceDate);
    }

    @Override
    public LineString getGeometryForTrip(Trip trip) {
        List<ShapePoint> points = _gtfsDao.getShapePointsForShapeId(trip.getShapeId());
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            ShapePoint point = points.get(i);
            coords[i] = new Coordinate(point.getLon(), point.getLat());
        }
        LineString geometry = _geometryFactory.createLineString(coords);
        return geometry;
    }

    @Override
    public Trip getTripForId(AgencyAndId id) {
        return _gtfsDao.getTripForId(id);
    }
}

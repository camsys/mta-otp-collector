package com.camsys.shims.util.gtfs;

import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
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

/**
 * <p>GtfsAndCalendar class.</p>
 *
 */
public class GtfsAndCalendar implements GtfsDaoDependency {

    private GtfsRelationalDao _gtfsDao;
    private CalendarServiceData _calendarData;
    private List<String> _routeIdWhiteList = new ArrayList<>();

    /** {@inheritDoc} */
    public void setGtfsDao(GtfsRelationalDao gtfsDao) {
        _gtfsDao = gtfsDao;
        _calendarData = new CalendarServiceDataFactoryImpl(_gtfsDao).createData();
    }

    /**
     * <p>setRouteIdWhiteList.</p>
     *
     * @param routeIdWhiteList a {@link java.util.List} object.
     */
    public void setRouteIdWhiteList(List<String> routeIdWhiteList) {
        _routeIdWhiteList = routeIdWhiteList;
    }

    /**
     * <p>setRouteIdWhiteListStr.</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public void setRouteIdWhiteListStr(String value) {
        _routeIdWhiteList = Arrays.asList(value.split(","));
    }

    /**
     * <p>getAllRoutes.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Route> getAllRoutes(){
        if(_routeIdWhiteList.size() > 0) {
            return _gtfsDao.getAllRoutes().stream()
                    .filter(route -> _routeIdWhiteList.contains(route.getId().toString()))
                    .collect(Collectors.toList());
        }
        return _gtfsDao.getAllRoutes();
    }

    /**
     * <p>getRouteForId.</p>
     *
     * @param agencyAndId a {@link org.onebusaway.gtfs.model.AgencyAndId} object.
     * @return a {@link org.onebusaway.gtfs.model.Route} object.
     */
    public Route getRouteForId(AgencyAndId agencyAndId){
        return _gtfsDao.getRouteForId(agencyAndId);
    }

    /**
     * <p>getTripsForRoute.</p>
     *
     * @param route a {@link org.onebusaway.gtfs.model.Route} object.
     * @return a {@link java.util.List} object.
     */
    public List<Trip> getTripsForRoute(Route route){
        return _gtfsDao.getTripsForRoute(route);
    }

    /**
     * <p>getStopTimesForTrip.</p>
     *
     * @param trip a {@link org.onebusaway.gtfs.model.Trip} object.
     * @return a {@link java.util.List} object.
     */
    public List<StopTime> getStopTimesForTrip(Trip trip) {
        return _gtfsDao.getStopTimesForTrip(trip);
    }

    /**
     * <p>getAllStops.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Stop> getAllStops() {
        return _gtfsDao.getAllStops();
    }

    /**
     * <p>getServiceIdsForDate.</p>
     *
     * @param serviceDate a {@link org.onebusaway.gtfs.model.calendar.ServiceDate} object.
     * @return a {@link java.util.Set} object.
     */
    public Set<AgencyAndId> getServiceIdsForDate(ServiceDate serviceDate){
        return _calendarData.getServiceIdsForDate(serviceDate);
    }

}

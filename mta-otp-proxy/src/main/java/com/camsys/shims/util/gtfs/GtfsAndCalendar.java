package com.camsys.shims.util.gtfs;

import org.onebusaway.gtfs.impl.calendar.CalendarServiceDataFactoryImpl;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GtfsAndCalendar {

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

    public Collection<Route> getAllRoutes(){
        if(_routeIdWhiteList.size() > 0) {
            return _gtfsDao.getAllRoutes().stream()
                    .filter(route -> _routeIdWhiteList.contains(route.getId().toString()))
                    .collect(Collectors.toList());
        }
        return _gtfsDao.getAllRoutes();
    }

    public Route getRouteForId(AgencyAndId agencyAndId){
        return _gtfsDao.getRouteForId(agencyAndId);
    }

    public List<Trip> getTripsForRoute(Route route){
        return _gtfsDao.getTripsForRoute(route);
    }

    public Set<AgencyAndId> getServiceIdsForDate(ServiceDate serviceDate){
        return _calendarData.getServiceIdsForDate(serviceDate);
    }

}

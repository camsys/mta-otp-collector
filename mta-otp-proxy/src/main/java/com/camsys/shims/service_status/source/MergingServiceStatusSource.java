package com.camsys.shims.service_status.source;

import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.ServiceStatus;
import com.camsys.shims.service_status.model.StatusDetail;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class MergingServiceStatusSource implements ServiceStatusSource
{

    private static final Logger _log = LoggerFactory.getLogger(MergingServiceStatusSource.class);

    private static final String SUBWAY_MODE = "subway";
    private static final String SUBWAY_AGENCY = "MTASBWY";

    private static final String BUS_MODE = "bus";
    private static final String BUS_AGENCY = "MTA NYCT";

    private static final String RAIL_MODE = "rail";
    private static final String LIRR_AGENCY = "LI";
    private static final String MNR_AGENCY = "MNR";


    private List<ServiceStatusSource> _sources;

    private ServiceStatus _serviceStatus;

    private GtfsDataService _busGtfsDataService;
    private GtfsDataService _subwayGtfsDataService;
    private GtfsDataService _lirrGtfsDataService;
    private GtfsDataService _mnrGtfsDataService;
    private String[] _bannedRouteIds = new String[] {
            "MTASBWY_FS"
    };


    public void setSubwayGtfsDataService(GtfsDataService service) {
        _subwayGtfsDataService = service;
    }

    public void setBusGtfsDataService(GtfsDataService service) {
        _busGtfsDataService = service;
    }

    public void setLirrGtfsDataService(GtfsDataService service) {
        _lirrGtfsDataService = service;
    }

    public void setMnrGtfsDataService(GtfsDataService service) {
        _mnrGtfsDataService = service;
    }

    public void setBannedRouteIds(String[] banned) {
         _bannedRouteIds = banned;
    };

    public MergingServiceStatusSource(List<ServiceStatusSource> sources) {
        _sources = sources;
    }

    @Override
    public void update() {
        List<RouteDetail> allRouteDetails = new ArrayList<>();
        for (ServiceStatusSource source : _sources) {
            source.update();
            if (source.getStatus(null) == null || source.getStatus(null).getRouteDetails() == null)
                continue;
            merge(allRouteDetails, source.getStatus(null).getRouteDetails());
        }
        _serviceStatus = new ServiceStatus(new Date(), filterHiddenRoutes(ensureDates(addGoodService(allRouteDetails))));

    }

    // we may already have an existing RouteDetail object.  If so merge carefully!
    private void merge(List<RouteDetail> existingRouteDetails, List<RouteDetail> newRouteDetails) {
        for (RouteDetail newRouteDetail : newRouteDetails) {
            RouteDetail existingRouteDetail = find(existingRouteDetails, newRouteDetail);
            if (existingRouteDetail == null) {
                existingRouteDetails.add(newRouteDetail);
            } else {
                merge(existingRouteDetail, newRouteDetail);
            }

        }
    }

    private RouteDetail merge(RouteDetail existingRouteDetail, RouteDetail newRouteDetail) {
        if (newRouteDetail.getStatusDetails().isEmpty()) return existingRouteDetail;
            if (newRouteDetail.getStatusDetails() != null) {
                if (existingRouteDetail.getStatusDetails() ==  null) {
                    Set<StatusDetail> srd = new HashSet<>();
                    existingRouteDetail.setStatusDetails(srd);
                }
                existingRouteDetail.getStatusDetails().addAll(newRouteDetail.getStatusDetails());
            }
        return existingRouteDetail;
    }

    private RouteDetail find(List<RouteDetail> existingRouteDetails, RouteDetail newRouteDetail) {
        if (existingRouteDetails == null || newRouteDetail == null) return null;
        for (RouteDetail rd : existingRouteDetails) {
            if (rd.getRouteId() != null && rd.getRouteId().equals(newRouteDetail.getRouteId()))
                return rd;
        }
        return null;
    }

    private List<RouteDetail> ensureDates(List<RouteDetail> routeDetails) {
        for (RouteDetail rd : routeDetails) {
            if (rd.getLastUpdated() == null)
                rd.setLastUpdated(new Date());
        }
        return routeDetails;
    }

    private List<RouteDetail> filterHiddenRoutes(List<RouteDetail> allService) {
        List<RouteDetail> filtered = new ArrayList<>();
        List<String> bannedRoutes = Arrays.asList(_bannedRouteIds);
        Set<String> bannedSet = new HashSet<>(bannedRoutes);
        for (RouteDetail rd : allService) {
            if (bannedSet.contains(rd.getRouteId())) {
                _log.debug("banning route " + rd.getRouteId());
            } else {
               filtered.add(rd);
            }
        }
        return filtered;
    }


    private List<RouteDetail> addGoodService(List<RouteDetail> input) {
        List<RouteDetail> allRouteDetails = new ArrayList<>();
        if (input == null) return allRouteDetails;
        // build up a hash of routeIds that have service status
        Map<String, RouteDetail> routeIdToRouteMap = new HashMap<>();
        for (RouteDetail rd : input) {
            routeIdToRouteMap.put(rd.getRouteId(), rd);
            allRouteDetails.add(rd);
        }

        int routeSortOrder = 1;
        ArrayList<Route> subwayRoutes = new ArrayList<>(getSubwayRoutes());
        Collections.sort(subwayRoutes, new RouteSortOrderComparator());
        // for each route from data source not listed, generate good status
        for (Route route : subwayRoutes) {
            if (!routeIdToRouteMap.containsKey(translateSubwayRouteId(route))) {
                allRouteDetails.add(generateSubwayGoodStatus(route, routeSortOrder));
            } else {
                routeIdToRouteMap.get(translateSubwayRouteId(route)).setRouteSortOrder(routeSortOrder);
            }
            routeSortOrder++;
        }

        for (Route route : getBusRoutes()) {
            if (!routeIdToRouteMap.containsKey(translateBusRouteId(route))) {
                allRouteDetails.add(generateBusGoodStatus(route));
            }
        }

        for (Route route : getLirrRoutes()) {
            if (!routeIdToRouteMap.containsKey(translateLirrRouteId(route))) {
                allRouteDetails.add(generateLirrGoodStatus(route));
            }
        }

        for (Route route : getMnrRoutes()) {
            if (!routeIdToRouteMap.containsKey(translateMnrRouteId(route))) {
                allRouteDetails.add(generateMnrGoodStatus(route));
            }
        }

        return allRouteDetails;
    }

    private RouteDetail generateSubwayGoodStatus(Route route, int routeSortOrder) {
        RouteDetail rd = new RouteDetail();
        rd.setRouteName(route.getShortName());
        rd.setInService(hasSubwayService(route));
        rd.setRouteId(route.getId().toString());
        rd.setAgency(SUBWAY_AGENCY);
        rd.setMode(SUBWAY_MODE);
        rd.setRouteType(route.getType());
        rd.setColor(route.getColor());
        rd.setRouteSortOrder(routeSortOrder);
        return rd;
    }

    private Boolean hasService(Route route, GtfsDataService gtfsDataServce) {
        ServiceDate date = new ServiceDate();
        Set<AgencyAndId> serviceIdsToday = gtfsDataServce.getServiceIdsOnDate(date);
        for (Trip t : gtfsDataServce.getTripsForRoute(route)) {
            if (serviceIdsToday.contains(t.getServiceId())) {
                return true;
            }
        }
        return false;

    }

    private Boolean hasSubwayService(Route route) {
        return hasService(route, _subwayGtfsDataService);
    }

    private Boolean hasBusService(Route route) {
        return hasService(route, _busGtfsDataService);
    }

    private Boolean hasLirrService(Route route) {
        return hasService(route, _lirrGtfsDataService);
    }

    private Boolean hasMnrService(Route route) {
        return hasService(route, _mnrGtfsDataService);
    }

    private RouteDetail generateBusGoodStatus(Route route) {
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(route.getShortName());
        routeDetail.setInService(hasBusService(route));
        routeDetail.setRouteId(route.getId().toString());
        routeDetail.setAgency(route.getAgency().getId());
        routeDetail.setMode(BUS_MODE);
        routeDetail.setRouteType(route.getType());
        routeDetail.setColor(route.getColor());
        return routeDetail;
    }


    private RouteDetail generateLirrGoodStatus(Route route) {
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(route.getLongName());
        routeDetail.setInService(hasLirrService(route));
        routeDetail.setRouteId(route.getId().toString());
        routeDetail.setAgency(LIRR_AGENCY);
        routeDetail.setMode(RAIL_MODE);
        routeDetail.setRouteType(route.getType());
        routeDetail.setColor(route.getColor());
        return routeDetail;
    }

    private RouteDetail generateMnrGoodStatus(Route route) {
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(route.getLongName());
        routeDetail.setInService(hasMnrService(route));
        routeDetail.setRouteId(route.getId().toString());
        routeDetail.setAgency(MNR_AGENCY);
        routeDetail.setMode(RAIL_MODE);
        routeDetail.setRouteType(route.getType());
        routeDetail.setColor(route.getColor());
        return routeDetail;
    }

    private String translateSubwayRouteId(Route route) {
        return route.getId().toString().replaceAll("MTA NYCT", SUBWAY_AGENCY);
    }

    private String translateBusRouteId(Route route) {
        return route.getId().toString().replaceAll("MTA NYCT", BUS_AGENCY);
    }
    private String translateLirrRouteId(Route route) {
        return route.getId().toString().replaceAll("LI", LIRR_AGENCY);
    }

    private String translateMnrRouteId(Route route) {
        return route.getId().toString().replaceAll("MNR", MNR_AGENCY);
    }

    @Override
    public ServiceStatus getStatus(String updatesSince) {
        if(updatesSince != null)
            return getFilteredServiceStatus(updatesSince);
        return _serviceStatus;
    }

    private ServiceStatus getFilteredServiceStatus(String updatesSince){
        try {
            final Date updatesSinceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(updatesSince);
            List<RouteDetail> routeDetails = _serviceStatus.getRouteDetails().stream()
                    .filter(routeDetail -> updatesSinceDate.compareTo(routeDetail.getLastUpdated()) <= 0)
                    .collect(Collectors.toList());

            return new ServiceStatus(_serviceStatus.getLastUpdated(), routeDetails);

        } catch (ParseException pe) {
            _log.error("Unable to parse updatesSince date param {}", updatesSince);
        }
        return _serviceStatus;
    }

    private Collection<Route> getSubwayRoutes() {
        return _subwayGtfsDataService.getAllRoutes();
    }

    private Collection<Route> getBusRoutes() {
        return _busGtfsDataService.getAllRoutes();
    }

    private Collection<Route> getLirrRoutes() {
        return _lirrGtfsDataService.getAllRoutes();
    }

    private Collection<Route> getMnrRoutes() {
        return _mnrGtfsDataService.getAllRoutes();
    }

    private class RouteSortOrderComparator implements Comparator{
        @Override
        public int compare(Object o1, Object o2) {
            Route r1 = (Route) o1;
            Route r2 = (Route) o2;
            return r1.getId().compareTo(r2.getId());
        }
    }
}

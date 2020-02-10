package com.camsys.shims.service_status.transformer;

import com.camsys.mta.gms_service_status.Line;
import com.camsys.mta.gms_service_status.Service;
import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;
import com.camsys.shims.util.HtmlCleanupUtil;
import org.apache.commons.lang.WordUtils;
import org.onebusaway.gtfs.services.GtfsDataService;

import java.util.*;

/**
 * Created by lcaraballo on 4/5/18.
 */
public class GmsServiceStatusTransformer implements ServiceStatusTransformer<Service> {
    private static final String STATUS_GOOD_SERVICE = "GOOD SERVICE";
    private static final String DEFAULT_DIRECTION = "0";

    private static final String SUBWAY_MODE = "subway";
    private static final String SUBWAY_AGENCY = "MTASBWY";

    private static final String BUS_MODE = "bus";
    private static final String BUS_AGENCY = "MTA NYCT";

    private static final String RAIL_MODE = "rail";
    private static final String LIRR_AGENCY = "LI";
    private static final String MNR_AGENCY = "MNR";

    private String[] subwayRoutes = new String[] {"SIR"};

    private HtmlCleanupUtil _htmlCleanupUtil;

    public void setHtmlCleanupUtil(HtmlCleanupUtil htmlCleanupUtil) {
        _htmlCleanupUtil = htmlCleanupUtil;
    }

    @Override
    public List<RouteDetail> transform(Service service, String mode, GtfsDataService gtfsDataService, GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> _routeDetailsMap) {
        List<RouteDetail> routeDetails = new ArrayList<>();
        for(Line subwayLine : service.getSubway().getLine()){
            if(!subwayLine.getStatus().equalsIgnoreCase(STATUS_GOOD_SERVICE)){
                routeDetails.add(generateSubwayRouteDetail(subwayLine));
            }
        }
        for(Line busLine : service.getBus().getLine()){
            if(!busLine.getStatus().equalsIgnoreCase(STATUS_GOOD_SERVICE)){
                routeDetails.add(generateBusRouteDetail(busLine));
            }
        }
        for(Line lirrLine : service.getLIRR().getLine()){
            if(!lirrLine.getStatus().equalsIgnoreCase(STATUS_GOOD_SERVICE)){
                routeDetails.add(generateLirrRouteDetail(lirrLine));
            }
        }
        for(Line mnrLine : service.getMetroNorth().getLine()){
            if(!mnrLine.getStatus().equalsIgnoreCase(STATUS_GOOD_SERVICE)){
                routeDetails.add(generateMnrRouteDetail(mnrLine));
            }
        }

        return routeDetails;
    }

    private RouteDetail generateSubwayRouteDetail(Line line){
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(getSubwayRoute(line.getName()));
        routeDetail.setInService(Boolean.TRUE);
        routeDetail.setRouteId(getRouteId(SUBWAY_AGENCY, routeDetail.getRouteName()));
        routeDetail.setAgency(SUBWAY_AGENCY);
        routeDetail.setMode(SUBWAY_MODE);
        routeDetail.setStatusDetails(generateStatusDetails(line));
        return routeDetail;
    }

    private String getSubwayRoute(String route){
        if(Arrays.asList(subwayRoutes).contains(route))
            return route;
        return route.substring(0,1);
    }

    private RouteDetail generateBusRouteDetail(Line line){
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(getBusRoute(line.getName()));
        routeDetail.setInService(Boolean.TRUE);
        routeDetail.setRouteId(getRouteId(BUS_AGENCY, routeDetail.getRouteName()));
        routeDetail.setAgency(BUS_AGENCY);
        routeDetail.setMode(BUS_MODE);
        routeDetail.setStatusDetails(generateStatusDetails(line));
        return routeDetail;
    }

    private String getBusRoute(String route) {
        if (route.contains(" ")) {
            return route.substring(0, route.indexOf(" "));
        }
        return route;
    }

    private RouteDetail generateLirrRouteDetail(Line line){
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(line.getName());
        routeDetail.setInService(Boolean.TRUE);
        routeDetail.setRouteId(getRouteId(LIRR_AGENCY, "1"));
        routeDetail.setAgency(LIRR_AGENCY);
        routeDetail.setMode(RAIL_MODE);
        routeDetail.setStatusDetails(generateStatusDetails(line));
        return routeDetail;
    }

    private RouteDetail generateMnrRouteDetail(Line line){
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(line.getName());
        routeDetail.setInService(Boolean.TRUE);
        routeDetail.setRouteId(getRouteId(MNR_AGENCY, "1"));
        routeDetail.setAgency(MNR_AGENCY);
        routeDetail.setMode(RAIL_MODE);
        routeDetail.setStatusDetails(generateStatusDetails(line));
        return routeDetail;
    }

    private Set<StatusDetail> generateStatusDetails(Line line){
        Set<StatusDetail> statusDetails = new HashSet<>();
        StatusDetail statusDetail = new StatusDetail();
        statusDetail.setId(line.getId());
        statusDetail.setStatusSummary(WordUtils.capitalize(line.getStatus()));
        statusDetail.setDirection(DEFAULT_DIRECTION);
        statusDetail.setStatusDescription(_htmlCleanupUtil.filterHtml(line.getText()));
        statusDetails.add(statusDetail);
        return statusDetails;
    }

    private String getRouteId(String agency, String route){
        return agency + "_" + route;
    }
}

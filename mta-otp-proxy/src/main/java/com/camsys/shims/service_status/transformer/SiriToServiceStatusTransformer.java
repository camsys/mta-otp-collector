package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;
import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import org.onebusaway.gtfs.services.GtfsDataService;
import org.onebusaway.nyc.transit_data_manager.model.ExtendedServiceAlertBean;
import org.onebusaway.nyc.transit_data_manager.util.NycSiriUtil;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
import org.onebusaway.transit_data.model.service_alerts.TimeRangeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.siri.siri.Siri;

import java.util.*;

import static com.camsys.shims.util.TimeUtils.getServiceDate;

public class SiriToServiceStatusTransformer implements ServiceStatusTransformer<Siri>{

    private static final Logger _log = LoggerFactory.getLogger(SiriToServiceStatusTransformer.class);

    @Override
    public List<RouteDetail> transform(Siri siri, String mode, GtfsDataService gtfsDataService,
                                       GtfsRouteAdapter gtfsAdapter, Map<String, RouteDetail> routeDetailsMap) {
        List<ExtendedServiceAlertBean> serviceAlerts = NycSiriUtil.getSiriAsExtendedServiceAlertBeans(siri);

        return  getRouteDetails(serviceAlerts, mode, gtfsDataService, gtfsAdapter, routeDetailsMap);
    }

    protected List<RouteDetail> getRouteDetails(List<ExtendedServiceAlertBean> serviceAlerts, String mode,
                                                GtfsDataService gtfsDataService, GtfsRouteAdapter gtfsAdapter,
                                                Map<String, RouteDetail> routeDetailsMap){

        Map<String, RouteDetail> tempRouteDetailsMap = new HashMap<String, RouteDetail>(400);
        Date lastUpdated = new Date();

        generateRouteDetailsForAlerts(tempRouteDetailsMap, serviceAlerts, mode, gtfsDataService, gtfsAdapter, lastUpdated);
        generateRouteDetailsForAllRoutes(tempRouteDetailsMap, mode, gtfsDataService, lastUpdated);
        updateRouteDetailsMap(tempRouteDetailsMap, routeDetailsMap);

        List<RouteDetail> routeDetails = new ArrayList<>(routeDetailsMap.values());

        return routeDetails;
    }

    protected void generateRouteDetailsForAlerts(Map<String, RouteDetail> tempRouteDetailsMap,
                                               List<ExtendedServiceAlertBean> serviceAlerts,
                                               String mode,
                                               GtfsDataService gtfsDataService,
                                               GtfsRouteAdapter gtfsAdapter,
                                               Date lastUpdated){

        for(ExtendedServiceAlertBean alert : serviceAlerts){
            for(SituationAffectsBean affectsBean : alert.getAllAffects()){
                String routeId = gtfsAdapter.getGtfsRouteId(affectsBean);
                if (routeId == null)
                    continue;
                Route route = getRoute(routeId, gtfsDataService);
                if(route != null) {
                    StatusDetail statusDetail = generateStatusDetail(alert, affectsBean);
                    if (!tempRouteDetailsMap.containsKey(routeId)) {
                        Set<StatusDetail> statusDetails = new LinkedHashSet<>();
                        statusDetails.add(statusDetail);
                        RouteDetail routeDetail = generateRouteDetail(route, mode, gtfsDataService, lastUpdated);
                        routeDetail.setStatusDetails(statusDetails);
                        tempRouteDetailsMap.put(routeId, routeDetail);
                    } else {
                        tempRouteDetailsMap.get(routeId).getStatusDetails().add(statusDetail);
                    }
                }
            }
        }
    }

    protected void generateRouteDetailsForAllRoutes(Map<String, RouteDetail> tempRouteDetailsMap,
                                                  String mode, GtfsDataService gtfsDataService,
                                                  Date lastUpdated){

        for (Route route : gtfsDataService.getAllRoutes()) {
            String routeId = route.getId().toString();
            if (!tempRouteDetailsMap.containsKey(routeId)) {
                RouteDetail routeDetail = generateRouteDetail(route, mode, gtfsDataService, lastUpdated);
                tempRouteDetailsMap.put(routeId, routeDetail);
            }
        }
    }

    private RouteDetail generateRouteDetail(Route route,
                                            String mode,
                                            GtfsDataService gtfsDataService,
                                            Date lastUpdated){

        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(getRouteName(route));
        routeDetail.setColor(route.getColor());
        routeDetail.setInService(isRouteInService(route, gtfsDataService));
        routeDetail.setRouteId(route.getId().toString());
        routeDetail.setAgency(route.getId().getAgencyId());
        routeDetail.setMode(mode);
        routeDetail.setLastUpdated(lastUpdated);
        routeDetail.setRouteSortOrder(route.getSortOrder() >= 0 ? route.getSortOrder() : null);
        routeDetail.setRouteType(route.getType());
        return routeDetail;
    }

    private StatusDetail generateStatusDetail(ExtendedServiceAlertBean alertBean, SituationAffectsBean affectsBean){

        StatusDetail statusDetail = new StatusDetail();
        if(StringUtils.isNotBlank(alertBean.getReason())){
            statusDetail.setStatusSummary(alertBean.getReason());
        }
        if(alertBean.getDescriptions() != null && alertBean.getDescriptions().size() > 0){
            statusDetail.setStatusDescription(alertBean.getDescriptions().get(0).getValue());
        }
        if(alertBean.getCreationTime() > 0){
            statusDetail.setCreationDate(new Date(alertBean.getCreationTime()));
        }
        if(alertBean.getPublicationWindows() != null && alertBean.getPublicationWindows().size() > 0){
            TimeRangeBean publicationWindow = alertBean.getPublicationWindows().get(0);
            if(publicationWindow.getFrom() > 0)
                statusDetail.setStartDate(new Date(publicationWindow.getFrom()));
            if(publicationWindow.getTo() > 0)
                statusDetail.setEndDate(new Date(publicationWindow.getTo()));
        }
        if(alertBean.getMessagePriority() != null){
            statusDetail.setPriority(alertBean.getMessagePriority());
        }
        statusDetail.setDirection(affectsBean.getDirectionId());
        return statusDetail;
    }

    private Route getRoute(String routeId, GtfsDataService gtfsDataService){
        try {
            AgencyAndId agencyAndRouteId = AgencyAndId.convertFromString(routeId);
            return gtfsDataService.getRouteForId(agencyAndRouteId);
        } catch (IllegalArgumentException iae) {
            _log.error("Unable to get agencyAndId from route {}", routeId, iae);
        }
        return null;
    }

    private String getRouteName(Route route){
        if(route.getShortName() != null)
            return route.getShortName();
        if(route.getLongName() != null)
            return route.getLongName();
        return route.getId().getId();
    }

    private Boolean isRouteInService(Route route, GtfsDataService gtfsDataService) {
        ServiceDate serviceDate = getServiceDate();
        if (serviceDate == null){
            _log.error("Unable to generate service date");
            return null;
        }
        Set<AgencyAndId> serviceIds = gtfsDataService.getServiceIdsOnDate(serviceDate);
        for (Trip t : gtfsDataService.getTripsForRoute(route)) {
            if (serviceIds.contains(t.getServiceId())) {
                return true;
            }
        }
        return false;
    }

    protected void updateRouteDetailsMap(Map<String, RouteDetail> tempRouteDetailsMap,
                                       Map<String, RouteDetail> routeDetailMap){

        for(Map.Entry<String, RouteDetail> entry : tempRouteDetailsMap.entrySet()){
            if(routeDetailMap.get(entry.getKey()) == null ||
                    !routeDetailMap.get(entry.getKey()).equals(entry.getValue())){
                routeDetailMap.put(entry.getKey(), entry.getValue());
            }
        }
        tempRouteDetailsMap = null;
    }
}

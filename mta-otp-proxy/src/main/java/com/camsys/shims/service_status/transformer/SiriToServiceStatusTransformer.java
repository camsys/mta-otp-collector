package com.camsys.shims.service_status.transformer;

import com.camsys.shims.service_status.model.RouteDetail;
import com.camsys.shims.service_status.model.StatusDetail;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.CalendarServiceData;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.nyc.transit_data_manager.model.ExtendedServiceAlertBean;
import org.onebusaway.nyc.transit_data_manager.util.NycSiriUtil;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
import org.onebusaway.transit_data.model.service_alerts.TimeRangeBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.org.siri.siri.Siri;

import java.util.*;
import static com.camsys.shims.util.TimeUtils.*;

public class SiriToServiceStatusTransformer implements ServiceStatusTransformer<Siri>{

    private static final Logger _log = LoggerFactory.getLogger(SiriToServiceStatusTransformer.class);

    @Override
    public List<RouteDetail> transform(Siri siri, String mode, GtfsRelationalDao dao, CalendarServiceData csd) {
        List<ExtendedServiceAlertBean> serviceAlerts = NycSiriUtil.getSiriAsExtendedServiceAlertBeans(siri);
        return  getRouteDetails(serviceAlerts, mode, dao, csd);
    }

    private List<RouteDetail> getRouteDetails(List<ExtendedServiceAlertBean> serviceAlerts, String mode, GtfsRelationalDao dao, CalendarServiceData csd){

        Map<String, RouteDetail> routeDetailsMap = new HashMap<>();

        generateRouteDetailsForAlerts(routeDetailsMap, serviceAlerts, mode, dao, csd);
        generateRouteDetailsForAllRoutes(routeDetailsMap, mode, dao, csd);

        List<RouteDetail> routeDetails = new ArrayList<>(routeDetailsMap.values());

        return routeDetails;
    }

    private void generateRouteDetailsForAlerts(Map<String, RouteDetail> routeDetailsMap, List<ExtendedServiceAlertBean> serviceAlerts, String mode, GtfsRelationalDao dao, CalendarServiceData csd){
        for(ExtendedServiceAlertBean alert : serviceAlerts){
            for(SituationAffectsBean affectsBean : alert.getAllAffects()){
                String routeId = affectsBean.getRouteId();
                Route route = getRoute(routeId, dao);

                if(route != null) {
                    StatusDetail statusDetail = generateStatusDetail(alert, affectsBean);

                    if (!routeDetailsMap.containsKey(routeId)) {
                        List<StatusDetail> statusDetails = new ArrayList<>();
                        statusDetails.add(statusDetail);
                        RouteDetail routeDetail = generateRouteDetail(route, mode, dao, csd);
                        routeDetail.setStatusDetailsList(statusDetails);
                        routeDetailsMap.put(routeId, routeDetail);
                    } else {
                        routeDetailsMap.get(routeId).getStatusDetailsList().add(statusDetail);
                    }
                }
            }
        }
    }

    private void generateRouteDetailsForAllRoutes(Map<String, RouteDetail> routeDetailsMap, String mode, GtfsRelationalDao dao, CalendarServiceData csd){
        for(Route route : dao.getAllRoutes()){
            String routeId = route.getId().toString();
            if(!routeDetailsMap.containsKey(routeId)){
                RouteDetail routeDetail = generateRouteDetail(route, mode, dao, csd);
                routeDetailsMap.put(routeId, routeDetail);
            }
        }
    }

    private RouteDetail generateRouteDetail(Route route, String mode, GtfsRelationalDao dao, CalendarServiceData csd){
        RouteDetail routeDetail = new RouteDetail();
        routeDetail.setRouteName(getRouteName(route));
        routeDetail.setColor(route.getColor());
        routeDetail.setInService(isRouteInService(route, dao, csd));
        routeDetail.setRouteId(route.getId().toString());
        routeDetail.setAgency(route.getId().getAgencyId());
        routeDetail.setMode(mode);
        return routeDetail;
    }

    private StatusDetail generateStatusDetail(ExtendedServiceAlertBean alertBean, SituationAffectsBean affectsBean){
        StatusDetail statusDetail = new StatusDetail();
        if(alertBean.getSummaries() != null && alertBean.getSummaries().size() > 0) {
            statusDetail.setStatusSummary(alertBean.getSummaries().get(0).getValue());
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

    private Route getRoute(String routeId, GtfsRelationalDao dao){
        try {
            AgencyAndId agencyAndRouteId = AgencyAndId.convertFromString(routeId);
            return dao.getRouteForId(agencyAndRouteId);
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

    private Boolean isRouteInService(Route route, GtfsRelationalDao dao, CalendarServiceData csd) {
        ServiceDate serviceDate = getServiceDate();
        if (serviceDate == null){
            _log.error("Unable to generate service date");
            return null;
        }
        Set<AgencyAndId> serviceIds = csd.getServiceIdsForDate(serviceDate);
        for (Trip t : dao.getTripsForRoute(route)) {
            if (serviceIds.contains(t.getServiceId())) {
                return true;
            }
        }
        return false;
    }
}

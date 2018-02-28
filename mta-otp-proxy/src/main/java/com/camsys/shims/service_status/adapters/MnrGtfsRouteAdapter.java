package com.camsys.shims.service_status.adapters;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

public class MnrGtfsRouteAdapter implements GtfsRouteAdapter {
    private GtfsRelationalDao _dao;

    public void setGtfsDao(GtfsRelationalDao dao) {
        _dao = dao;
    }

    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        AgencyAndId route = AgencyAndId.convertFromString(affectsBean.getRouteId());
        String routeId = route.getId();
        Route gtfsRoute = getRouteFromGtfs(routeId);
        if(gtfsRoute != null && gtfsRoute.getId() != null) {
            return gtfsRoute.getId().toString();
        }
        return null;
    }

    private Route getRouteFromGtfs(String routeId){
        return _dao.getAllRoutes().stream()
                .filter(route -> route.getLongName() != null && route.getLongName().equalsIgnoreCase(routeId))
                .findFirst()
                .orElse(null);
    }

}

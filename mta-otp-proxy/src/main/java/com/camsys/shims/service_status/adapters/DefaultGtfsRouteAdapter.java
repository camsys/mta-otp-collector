package com.camsys.shims.service_status.adapters;

import org.onebusaway.gtfs.model.Route;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

public class DefaultGtfsRouteAdapter implements GtfsRouteAdapter {

    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        return affectsBean.getRouteId();
    }

    @Override
    public boolean shouldIncludeRoute(Route route) {
        return true;
    }
}

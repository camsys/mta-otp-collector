package com.camsys.shims.service_status.adapters;

import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

public class DefaultGtfsRouteAdapter implements GtfsRouteAdapter{

    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        return affectsBean.getRouteId();
    }
}

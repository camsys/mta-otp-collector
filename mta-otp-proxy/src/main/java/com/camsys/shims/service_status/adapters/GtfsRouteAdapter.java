package com.camsys.shims.service_status.adapters;

import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

public interface GtfsRouteAdapter {
    String getGtfsRouteId(SituationAffectsBean affectsBean);
}

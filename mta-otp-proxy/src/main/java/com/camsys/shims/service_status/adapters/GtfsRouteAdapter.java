package com.camsys.shims.service_status.adapters;

import org.onebusaway.gtfs.model.Route;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

// Handle the mapping between GMS route ID and GTFS route ID
public interface GtfsRouteAdapter {
    String getGtfsRouteId(SituationAffectsBean affectsBean);

    boolean shouldIncludeRoute(Route route);
}

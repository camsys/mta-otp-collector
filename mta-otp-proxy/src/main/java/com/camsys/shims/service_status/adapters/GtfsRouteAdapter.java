package com.camsys.shims.service_status.adapters;

import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

/**
 * <p>GtfsRouteAdapter interface.</p>
 *
 */
public interface GtfsRouteAdapter {
    /**
     * <p>getGtfsRouteId.</p>
     *
     * @param affectsBean a {@link org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean} object.
     * @return a {@link java.lang.String} object.
     */
    String getGtfsRouteId(SituationAffectsBean affectsBean);
}

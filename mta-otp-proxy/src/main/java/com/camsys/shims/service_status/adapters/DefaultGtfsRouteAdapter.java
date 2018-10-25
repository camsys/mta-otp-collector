package com.camsys.shims.service_status.adapters;

import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

/**
 * <p>DefaultGtfsRouteAdapter class.</p>
 *
 */
public class DefaultGtfsRouteAdapter implements GtfsRouteAdapter{

    /** {@inheritDoc} */
    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        return affectsBean.getRouteId();
    }
}

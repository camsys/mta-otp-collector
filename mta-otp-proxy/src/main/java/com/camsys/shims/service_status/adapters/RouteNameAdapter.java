package com.camsys.shims.service_status.adapters;

import com.camsys.shims.atis.AtisGtfsMap;
import com.camsys.shims.util.gtfs.RouteNameGtfsMap;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * <p>RouteNameAdapter class.</p>
 *
 */
public class RouteNameAdapter implements GtfsRouteAdapter {
    private static final Logger _log = LoggerFactory.getLogger(RouteNameAdapter.class);

    private RouteNameGtfsMap _routeNameGtfsMap;

    private List<String> _gtfsAgencyId;

    /** {@inheritDoc} */
    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        String routeId = affectsBean.getRouteId();
        AgencyAndId id = _routeNameGtfsMap.getId(routeId);
        if (id == null) {
            _log.error("missing ID {}" + routeId);
            return null;
        }
        if (_gtfsAgencyId.contains(id.getAgencyId())) {
            return id.getAgencyId() + AgencyAndId.ID_SEPARATOR + id.getId();
        }
        else {
            _log.error("unexpected agency ID: {}", id.getAgencyId());
            return null;
        }
    }


    /**
     * <p>setGtfsMap.</p>
     *
     * @param routeNameGtfsMap a {@link com.camsys.shims.util.gtfs.RouteNameGtfsMap} object.
     */
    public void setGtfsMap(RouteNameGtfsMap routeNameGtfsMap) {
        _routeNameGtfsMap = routeNameGtfsMap;
    }

    /**
     * <p>setGtfsAgencyId.</p>
     *
     * @param gtfsAgencyId a {@link java.util.List} object.
     */
    public void setGtfsAgencyId(List<String> gtfsAgencyId) {
        _gtfsAgencyId = gtfsAgencyId;
    }
}

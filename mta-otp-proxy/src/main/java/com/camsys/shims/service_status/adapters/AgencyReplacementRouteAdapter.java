package com.camsys.shims.service_status.adapters;

import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.services.GtfsRelationalDao;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

/**
 * <p>AgencyReplacementRouteAdapter class.</p>
 *
 */
public class AgencyReplacementRouteAdapter implements GtfsRouteAdapter {

    private String _replacementAgencyId;

    /** {@inheritDoc} */
    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        AgencyAndId route = AgencyAndId.convertFromString(affectsBean.getRouteId());
        if(route != null) {
            return _replacementAgencyId + AgencyAndId.ID_SEPARATOR + route.getId();
        }
        return null;
    }

    /**
     * <p>setAgencyId.</p>
     *
     * @param agencyId a {@link java.lang.String} object.
     */
    public void setAgencyId(String agencyId) {
        _replacementAgencyId = agencyId;
    }

}

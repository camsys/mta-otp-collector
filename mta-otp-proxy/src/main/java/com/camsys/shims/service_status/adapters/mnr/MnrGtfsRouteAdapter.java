package com.camsys.shims.service_status.adapters.mnr;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.transit_data.model.service_alerts.SituationAffectsBean;

import java.util.HashMap;
import java.util.Map;

public class MnrGtfsRouteAdapter implements GtfsRouteAdapter {

    @Override
    public String getGtfsRouteId(SituationAffectsBean affectsBean) {
        return null;
    }
}

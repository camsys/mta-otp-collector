package com.camsys.shims.service_status.adapters.lirr;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import org.onebusaway.gtfs.model.AgencyAndId;

import java.util.HashMap;
import java.util.Map;

public class LirrGtfsRouteAdapter implements GtfsRouteAdapter {
    Map<String, String> siriToGtfsAgencyMap = new HashMap<String, String>();
    private static final String DEFAULT_AGENCY = "LI";

    public String getGtfsAgency(String siriLineRef){
        AgencyAndId lineRef = AgencyAndId.convertFromString(siriLineRef);
        String siriAgency = lineRef.getAgencyId();
        if(siriAgency != null){
            return siriToGtfsAgencyMap.get(siriAgency);
        }
        return DEFAULT_AGENCY;
    }

    public Map<String, String> getSiriToGtfsAgencyMap() {
        return siriToGtfsAgencyMap;
    }

    public void setSiriToGtfsAgencyMap(Map<String, String> siriToGtfsAgencyMap) {
        this.siriToGtfsAgencyMap = siriToGtfsAgencyMap;
    }
}

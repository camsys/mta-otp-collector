package com.camsys.shims.service_status.adapters.mnr;

import com.camsys.shims.service_status.adapters.GtfsRouteAdapter;
import org.onebusaway.gtfs.model.AgencyAndId;

import java.util.HashMap;
import java.util.Map;

public class MnrGtfsRouteAdapter implements GtfsRouteAdapter {
    Map<String, String> siriToGtfsAgencyMap = new HashMap<String, String>();
    private static final String DEFAULT_AGENCY = "mnr";

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

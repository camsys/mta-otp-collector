package com.camsys.shims.util.gtfs;

import com.camsys.shims.atis.csv.AtisGtfsEntry;
import com.camsys.shims.util.gtfs.csv.RouteNameGtfsEntry;
import org.onebusaway.csv_entities.CsvEntityReader;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteNameGtfsMap {

    private static final Logger _log = LoggerFactory.getLogger(RouteNameGtfsMap.class);

    private Map<String, AgencyAndId> _routeNameGtfsMap;

    public void init() {
        CsvEntityReader reader = new CsvEntityReader();
        final List<RouteNameGtfsEntry> entries = new ArrayList<>();
        reader.addEntityHandler(o -> entries.add((RouteNameGtfsEntry) o));
        try {
            InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream("route-name-gtfs.csv");
            reader.readEntities(RouteNameGtfsEntry.class, is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _routeNameGtfsMap = new HashMap<>();

        for (RouteNameGtfsEntry entry : entries) {
            String routeName = entry.getRouteName();
            AgencyAndId id = AgencyAndId.convertFromString(entry.getGtfsId(), ':');
            _routeNameGtfsMap.put(routeName, id);
        }
    }

    public AgencyAndId getId(String key) {
        AgencyAndId atisId = AgencyAndId.convertFromString(key);

        AgencyAndId result = _routeNameGtfsMap.get(atisId.getId());
        if (result == null) {
            _log.info("atisId " + atisId.getId() + " not found in " + _routeNameGtfsMap.toString());
        }
        return result;
    }

    public Map<String, AgencyAndId> getUnmodifiableMap() {
        return Collections.unmodifiableMap(_routeNameGtfsMap);
    }

}

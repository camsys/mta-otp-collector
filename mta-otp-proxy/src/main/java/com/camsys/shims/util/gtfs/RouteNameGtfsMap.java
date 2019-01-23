package com.camsys.shims.util.gtfs;

import com.camsys.shims.atis.csv.AtisGtfsEntry;
import com.camsys.shims.util.gtfs.csv.RouteNameGtfsEntry;
import org.onebusaway.csv_entities.CsvEntityReader;
import org.onebusaway.gtfs.model.AgencyAndId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteNameGtfsMap {

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
        return _routeNameGtfsMap.get(atisId.getId());
    }

    public Map<String, AgencyAndId> getUnmodifiableMap() {
        return Collections.unmodifiableMap(_routeNameGtfsMap);
    }

}

/* This program is free software: you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public License
 as published by the Free Software Foundation, either version 3 of
 the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.camsys.shims.atis;

import com.camsys.shims.atis.csv.AtisGtfsEntry;
import org.onebusaway.csv_entities.CsvEntityReader;
import org.onebusaway.gtfs.model.AgencyAndId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtisGtfsMap {

    private Map<String, AgencyAndId> _atisGtfsMap;

    public void init() {
        CsvEntityReader reader = new CsvEntityReader();
        final List<AtisGtfsEntry> entries = new ArrayList<>();
        reader.addEntityHandler(o -> entries.add((AtisGtfsEntry) o));
        try {
            InputStream is = this.getClass().getClassLoader()
                    .getResourceAsStream("atis-gtfs-routes.csv");
            reader.readEntities(AtisGtfsEntry.class, is);
        } catch (Exception e) {
            e.printStackTrace();
        }

        _atisGtfsMap = new HashMap<>();

        for (AtisGtfsEntry entry : entries) {
            String atisRoute = entry.getAtisId();
            AgencyAndId id = AgencyAndId.convertFromString(entry.getGtfsId(), ':');
            _atisGtfsMap.put(atisRoute, id);
        }
    }

    public AgencyAndId getId(String key) {
        AgencyAndId atisId = AgencyAndId.convertFromString(key);
        return _atisGtfsMap.get(atisId.getId());
    }

    public AgencyAndId getAgencyAndIdFromAtisIdWithoutAgency(String id) {
        return _atisGtfsMap.get(id);
    }

}

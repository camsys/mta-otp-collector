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
package com.camsys.shims.gtfsrt.tripUpdates.subway;

import com.camsys.shims.s3.AbstractS3CsvProvider;
import com.csvreader.CsvReader;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class S3MapStopIdTransformStrategy extends AbstractS3CsvProvider implements StopIdTransformStrategy  {

    private static final String ROUTE_ID_HEADER = "route_id";

    private static final String DIRECTION_ID_HEADER = "direction_id";

    private static final String FROM_STOP_ID_HEADER = "from_stop_id";

    private static final String TO_STOP_ID_HEADER = "to_stop_id";

    private Map<Triple<String, String, String>, String> _stopRewriteMap = new HashMap<>();

    @Override
    public void processRecord(CsvReader reader) throws IOException {
        String routeId = reader.get(ROUTE_ID_HEADER);
        String directionId = reader.get(DIRECTION_ID_HEADER);
        String direction = directionId.equals("1") ? "S" : "N";
        String fromStop = reader.get(FROM_STOP_ID_HEADER);
        String toStop = reader.get(TO_STOP_ID_HEADER);
        _stopRewriteMap.put(Triple.of(routeId, direction, fromStop), toStop);
    }

    @Override
    public String transform(String route, String direction, String stop) {
        return _stopRewriteMap.getOrDefault(Triple.of(route, direction, stop), stop);
    }
}

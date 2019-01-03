/*
 * Copyright (C) 2018 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.camsys.shims.gtfsrt.tripUpdates.subway;

import com.camsys.shims.s3.AbstractS3CsvProvider;
import com.csvreader.CsvReader;
import com.kurtraschke.nyctrtproxy.transform.StopFilterStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.onebusaway.gtfs.model.AgencyAndId;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Use the SubwayTime replacement spreadsheet on S3 to filter stops.
public class S3SubwayFilterStrategy extends AbstractS3CsvProvider implements StopFilterStrategy  {

    private static final String ROUTE_ID_HEADER = "route_id";

    private static final String STOP_ID_HEADER = "stop_id";

    private static final String STOP_STATUS_HEADER = "stop_status";

    // Status: 0: nothing, -1, station Closed, -2--> South bound Closed, -3--> North bound Closed

    private static final int STATUS_NOTHING = 0;

    private static final int STATUS_CLOSED = -1;

    private static final int STATUS_SOUTHBOUND_CLOSED = -2;

    private static final int STATUS_NORTHBOUND_CLOSED = -3;

    private Set<Pair<String, String>> _closedStops;

    @Override
    public boolean shouldInclude(String route, String stop) {
        return !getClosedStops().contains(key(route, stop));
    }

    @Override
    public void processRecord(CsvReader reader) throws IOException {
        String routeIdFull = reader.get(ROUTE_ID_HEADER);
        String stopIdFull = reader.get(STOP_ID_HEADER);
        String statusStr = reader.get(STOP_STATUS_HEADER);
        int status = StringUtils.isEmpty(statusStr) ? STATUS_NOTHING : Integer.parseInt(statusStr);
        String routeId = AgencyAndId.convertFromString(routeIdFull, ':').getId();
        String stopId = AgencyAndId.convertFromString(stopIdFull, ':').getId();
        if (status == STATUS_CLOSED || status == STATUS_SOUTHBOUND_CLOSED) {
            getClosedStops().add(key(routeId, stopId + "S"));
        }
        if (status == STATUS_CLOSED || status == STATUS_NORTHBOUND_CLOSED) {
            getClosedStops().add(key(routeId, stopId + "N"));
        }
    }

    private Set<Pair<String, String>> getClosedStops() {
        if (_closedStops == null) {
            _closedStops = new HashSet<>();
        }
        return _closedStops;
    }

    private Pair<String, String> key(String route, String stop) {
        return Pair.of(route, stop);
    }

}

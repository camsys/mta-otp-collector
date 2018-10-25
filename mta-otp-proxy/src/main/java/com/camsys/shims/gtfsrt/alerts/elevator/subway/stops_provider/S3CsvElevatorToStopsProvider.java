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
package com.camsys.shims.gtfsrt.alerts.elevator.subway.stops_provider;

import com.camsys.shims.s3.AbstractS3CsvProvider;
import com.csvreader.CsvReader;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.util.Collection;

public class S3CsvElevatorToStopsProvider extends AbstractS3CsvProvider implements ElevatorToStopsProvider {

    private static final String ELEVATOR_ID_HEADER = "Equip ID";

    private static final String STOP_ID_HEADER = "Stop ID";

    private static final String DIRECTION_HEADER = "Direction";

    private Multimap<String, String> _elevatorToStops = ArrayListMultimap.create();

    @Override
    public Collection<String> getStopsForElevator(String elevatorId) {
        return _elevatorToStops.get(elevatorId);
    }

    @Override
    public void processRecord(CsvReader reader) throws IOException {
        String elevatorId = reader.get(ELEVATOR_ID_HEADER);
        String stopId = reader.get(STOP_ID_HEADER);
        String dir = reader.get(DIRECTION_HEADER);
        _elevatorToStops.put(elevatorId, stopId + dir);
    }

}

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

import com.amazonaws.services.s3.AmazonS3;
import com.camsys.shims.util.S3Utils;
import com.csvreader.CsvReader;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

public class S3CsvElevatorToStopsProvider implements ElevatorToStopsProvider {

    private static final String ELEVATOR_ID_HEADER = "Equip ID";

    private static final String STOP_ID_HEADER = "Stop ID";

    private static final String DIRECTION_HEADER = "Direction";

    private Multimap<String, String> _elevatorToStops = ArrayListMultimap.create();

    private String user;

    private String pass;

    private String url;

    public void init() {
        if (!url.startsWith("s3://")) {
            throw new UnsupportedOperationException("protocol in url " + url + " no supported!");
        }
        AmazonS3 s3 = S3Utils.getS3Client(user, pass);
        InputStream stream = S3Utils.getViaS3(s3, url);
        CsvReader reader = new CsvReader(new InputStreamReader(stream));
        try {
            reader.readHeaders();
            while (reader.readRecord()) {
                String elevatorId = reader.get(ELEVATOR_ID_HEADER);
                String stopId = reader.get(STOP_ID_HEADER);
                String dir = reader.get(DIRECTION_HEADER);
                _elevatorToStops.put(elevatorId, stopId + dir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.close();
    }

    @Override
    public Collection<String> getStopsForElevator(String elevatorId) {
        return _elevatorToStops.get(elevatorId);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

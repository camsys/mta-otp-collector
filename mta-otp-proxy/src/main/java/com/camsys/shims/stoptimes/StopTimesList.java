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
package com.camsys.shims.stoptimes;

import com.google.transit.realtime.GtfsRealtime;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StopTimesList {

    private List<StopTimeModel> stopTimes;

    private long timestamp;

    public StopTimesList(GtfsRealtime.FeedMessage message, String routeId, String stopId) {
        List<StopTimeModel> stopTimes = new ArrayList<>();
        for (GtfsRealtime.FeedEntity entity : message.getEntityList()) {
            if (entity.hasTripUpdate()) {
                GtfsRealtime.TripUpdate update = entity.getTripUpdate();
                GtfsRealtime.TripDescriptor td = update.getTrip();
                if (td.getRouteId().equals(routeId)) {
                    for (GtfsRealtime.TripUpdate.StopTimeUpdate stopTimeUpdate : update.getStopTimeUpdateList()) {
                        if (stopTimeUpdate.getStopId().equals(stopId)) {
                            stopTimes.add(new StopTimeModel(td, stopTimeUpdate));
                        }
                    }
                }
            }
        }
        stopTimes.sort(Comparator.comparingLong(StopTimeModel::getDeparture));
        this.stopTimes = stopTimes;
        this.timestamp = message.getHeader().getTimestamp();
    }

    public List<StopTimeModel> getStopTimes() {
        return stopTimes;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

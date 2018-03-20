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

public class StopTimeModel {
    private String tripId;

    private String routeId;

    private long arrival;

    private long departure;

    public StopTimeModel(GtfsRealtime.TripDescriptor trip, GtfsRealtime.TripUpdate.StopTimeUpdate stop) {
        tripId = trip.getTripId();
        routeId = trip.getRouteId();
        arrival = stop.getArrival().getTime();
        departure = stop.getDeparture().getTime();
    }

    public String getTripId() {
        return tripId;
    }

    public String getRouteId() {
        return routeId;
    }

    public long getArrival() {
        return arrival;
    }

    public long getDeparture() {
        return departure;
    }
}

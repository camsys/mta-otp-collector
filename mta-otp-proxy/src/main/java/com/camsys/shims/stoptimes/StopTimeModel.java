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

/**
 * <p>StopTimeModel class.</p>
 *
 */
public class StopTimeModel {
    private String tripId;

    private String routeId;

    private long arrival;

    private long departure;

    /**
     * <p>Constructor for StopTimeModel.</p>
     *
     * @param trip a {@link com.google.transit.realtime.GtfsRealtime.TripDescriptor} object.
     * @param stop a {@link com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate} object.
     */
    public StopTimeModel(GtfsRealtime.TripDescriptor trip, GtfsRealtime.TripUpdate.StopTimeUpdate stop) {
        tripId = trip.getTripId();
        routeId = trip.getRouteId();
        arrival = stop.getArrival().getTime();
        departure = stop.getDeparture().getTime();
    }

    /**
     * <p>Getter for the field <code>tripId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTripId() {
        return tripId;
    }

    /**
     * <p>Getter for the field <code>routeId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getRouteId() {
        return routeId;
    }

    /**
     * <p>Getter for the field <code>arrival</code>.</p>
     *
     * @return a long.
     */
    public long getArrival() {
        return arrival;
    }

    /**
     * <p>Getter for the field <code>departure</code>.</p>
     *
     * @return a long.
     */
    public long getDeparture() {
        return departure;
    }
}

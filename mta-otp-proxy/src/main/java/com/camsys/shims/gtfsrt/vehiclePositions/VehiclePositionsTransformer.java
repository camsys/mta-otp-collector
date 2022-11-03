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
package com.camsys.shims.gtfsrt.vehiclePositions;

import com.camsys.shims.util.transformer.GtfsRealtimeTransformer;
import com.google.transit.realtime.GtfsRealtime.*;
import com.kurtraschke.nyctrtproxy.transform.StopIdTransformStrategy;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import org.onebusaway.geospatial.services.SphericalGeometryLibrary;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.services.GtfsDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VehiclePositionsTransformer implements GtfsRealtimeTransformer<FeedMessage> {

    private static final Logger _log = LoggerFactory.getLogger(VehiclePositionsTransformer.class);

    private static final double DIST_THRESHOLD = 1000.0;

    private static GeometryFactory _geometryFactory = new GeometryFactory();

    private boolean _calculateBearing = false;

    private GtfsDataService _gtfs;

    private String _agencyId;

    private StopIdTransformStrategy _stopIdTransformStrategy;

    public void setCalculateBearing(boolean calculateBearing) {
        _calculateBearing = calculateBearing;
    }

    public void setGtfsDataService(GtfsDataService gtfs) {
        _gtfs = gtfs;
    }

    public void setAgencyId(String agencyId) {
        _agencyId = agencyId;
    }

    @Override
    public FeedMessage transform(FeedMessage message) {
        FeedMessage.Builder builder = message.toBuilder();
        List<FeedEntity.Builder> entities = new ArrayList<>(builder.getEntityBuilderList());
        builder.clearEntity();
        Iterator<FeedEntity.Builder> entitiesIter = entities.iterator();
        while (entitiesIter.hasNext()) {
            FeedEntity.Builder entity = entitiesIter.next();
            if (!entity.hasVehicle()) {
                entitiesIter.remove();
                continue;
            }
            VehiclePosition.Builder vehicle = entity.getVehicleBuilder();
            // get trip ID from TripUpdate
            if (entity.hasTripUpdate()) {
                vehicle.setTrip(entity.getTripUpdate().getTrip());
                entity.clearTripUpdate();
            }
            if (_calculateBearing && vehicle.hasPosition() && !vehicle.getPosition().hasBearing()) {
                Float bearing = calculateBearing(vehicle.getTrip().getTripId(), vehicle.getPosition().getLatitude(), vehicle.getPosition().getLongitude());
                if (bearing != null)
                    vehicle.getPositionBuilder().setBearing(bearing);
            }
            if (_stopIdTransformStrategy != null) {
                if (vehicle.hasStopId()) {
                    String stopId = _stopIdTransformStrategy.transform(null, null, vehicle.getStopId());
                    vehicle.setStopId(stopId);
                }
            }
            builder.addEntity(entity);
        }
        return builder.build();
    }

    private Float calculateBearing(String tripId, float lat, float lon) {
        Trip trip = _gtfs.getTripForId(new AgencyAndId(_agencyId, tripId));
        if (trip == null || trip.getShapeId() == null) {
            return null;
        }
        List<ShapePoint> points = _gtfs.getShapePointsForShapeId(trip.getShapeId());
        Coordinate[] coords = new Coordinate[points.size()];
        for (int i = 0; i < points.size(); i++) {
            ShapePoint point = points.get(i);
            coords[i] = new Coordinate(point.getLon(), point.getLat());
        }
        LineString geometry = _geometryFactory.createLineString(coords);
        return calculateBearing(geometry, lat, lon);
    }

    Float calculateBearing(LineString geometry, float lat, float lon) {
        LocationIndexedLine line = new LocationIndexedLine(geometry);
        Coordinate origPoint = new Coordinate(lon, lat);
        LinearLocation loc = line.project(origPoint);
        Coordinate newPoint = loc.getCoordinate(geometry);
        double distance = SphericalGeometryLibrary.distance(lat, lon, newPoint.y, newPoint.x);
        if (distance < DIST_THRESHOLD) {
            LineSegment segment = loc.getSegment(geometry);
            double bearing = segment.angle() * (180d / Math.PI);
            bearing = 90d - bearing;
            while (bearing < 0) {
                bearing += 360d;
            }
            return (float) bearing;
        }
        return null;
    }

    public void setStopIdTransformStrategy(StopIdTransformStrategy stopIdTransformStrategy) {
        _stopIdTransformStrategy = stopIdTransformStrategy;
    }
}


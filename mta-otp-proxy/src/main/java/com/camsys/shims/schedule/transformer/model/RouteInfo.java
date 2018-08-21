package com.camsys.shims.schedule.transformer.model;

import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.model.EncodedPolylineBean;
import org.onebusaway.geospatial.services.PolylineEncoder;

import java.util.List;
import java.util.stream.Collectors;

public class RouteInfo {
    private List<RouteBranchStop> stops;

    private EncodedPolylineBean geometry;

    public RouteInfo(List<RouteBranchStop> stops, List<RouteShapePoint> shapePoints) {
        this.stops = stops;
        List<CoordinatePoint> points = shapePoints.stream()
                .map(p -> new CoordinatePoint(p.getLat(), p.getLon()))
                .collect(Collectors.toList());
        geometry = PolylineEncoder.createEncodings(points);
    }

    public List<RouteBranchStop> getStops() {
        return stops;
    }

    public EncodedPolylineBean getGeometry() {
        return geometry;
    }
}

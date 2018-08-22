package com.camsys.shims.schedule.transformer.model;

import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.model.EncodedPolylineBean;
import org.onebusaway.geospatial.services.PolylineEncoder;
import org.onebusaway.gtfs.model.Route;

import java.util.List;
import java.util.stream.Collectors;

public class RouteInfo {
    private List<ExtendedRouteBranchStop> stops;

    private EncodedPolylineBean geometry;

    private Route route;

    public RouteInfo(List<ExtendedRouteBranchStop> stops, List<RouteShapePoint> shapePoints, Route route) {
        this.stops = stops;
        List<CoordinatePoint> points = shapePoints.stream()
                .map(p -> new CoordinatePoint(p.getLat(), p.getLon()))
                .collect(Collectors.toList());
        geometry = PolylineEncoder.createEncodings(points);
        this.route = route;
    }

    public List<ExtendedRouteBranchStop> getStops() {
        return stops;
    }

    public EncodedPolylineBean getGeometry() {
        return geometry;
    }

    public Route getRoute() {
        return route;
    }
}

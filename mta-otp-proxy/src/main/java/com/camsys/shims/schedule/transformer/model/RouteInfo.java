package com.camsys.shims.schedule.transformer.model;

import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.model.EncodedPolylineBean;
import org.onebusaway.geospatial.services.PolylineEncoder;
import org.onebusaway.gtfs.model.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RouteInfo {
    private List<ExtendedRouteBranchStop> stops;

    private List<RouteGeometry> geometry = new ArrayList<>();

    private Route route;

    public RouteInfo(List<ExtendedRouteBranchStop> stops, Route route) {
        this.stops = stops;
        this.route = route;
    }

    public List<ExtendedRouteBranchStop> getStops() {
        return stops;
    }

    public List<RouteGeometry> getGeometry() {
        return geometry;
    }

    public Route getRoute() {
        return route;
    }

    public void addGeometry(List<RouteShapePoint> shapePoints) {
        List<CoordinatePoint> points = shapePoints.stream()
                .map(p -> new CoordinatePoint(p.getLat(), p.getLon()))
                .collect(Collectors.toList());
        EncodedPolylineBean bean = PolylineEncoder.createEncodings(points);
        geometry.add(new RouteGeometry(route.getColor(), bean.getPoints()));
    }
}

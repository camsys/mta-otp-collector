package com.camsys.shims.schedule.transformer.model;

import org.geojson.Feature;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.model.EncodedPolylineBean;
import org.onebusaway.geospatial.services.PolylineEncoder;
import org.onebusaway.gtfs.model.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
        addGeometry(shapePoints, p -> new CoordinatePoint(p.getLat(), p.getLon()));
    }

    public void addGeometry(Feature feature) {
        addGeometry(((LineString) feature.getGeometry()).getCoordinates(),
                p -> new CoordinatePoint(p.getLatitude(), p.getLongitude()));
    }

    private <T> void addGeometry(List<T> toTransform, Function<T, CoordinatePoint> transform) {
        List<CoordinatePoint> points = toTransform.stream()
                .map(transform)
                .collect(Collectors.toList());
        EncodedPolylineBean bean = PolylineEncoder.createEncodings(points);
        geometry.add(new RouteGeometry(route.getColor(), bean.getPoints()));
    }
}

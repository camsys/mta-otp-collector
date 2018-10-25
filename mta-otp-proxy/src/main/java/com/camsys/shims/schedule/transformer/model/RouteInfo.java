package com.camsys.shims.schedule.transformer.model;

import org.geojson.Feature;
import org.geojson.GeoJsonObject;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.MultiLineString;
import org.onebusaway.geospatial.model.CoordinatePoint;
import org.onebusaway.geospatial.model.EncodedPolylineBean;
import org.onebusaway.geospatial.services.PolylineEncoder;
import org.onebusaway.gtfs.model.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>RouteInfo class.</p>
 *
 */
public class RouteInfo {
    private List<ExtendedRouteBranchStop> stops;

    private List<RouteGeometry> geometry = new ArrayList<>();

    private Route route;

    /**
     * <p>Constructor for RouteInfo.</p>
     *
     * @param stops a {@link java.util.List} object.
     * @param route a {@link org.onebusaway.gtfs.model.Route} object.
     */
    public RouteInfo(List<ExtendedRouteBranchStop> stops, Route route) {
        this.stops = stops;
        this.route = route;
    }

    /**
     * <p>Getter for the field <code>stops</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<ExtendedRouteBranchStop> getStops() {
        return stops;
    }

    /**
     * <p>Getter for the field <code>geometry</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<RouteGeometry> getGeometry() {
        return geometry;
    }

    /**
     * <p>Getter for the field <code>route</code>.</p>
     *
     * @return a {@link org.onebusaway.gtfs.model.Route} object.
     */
    public Route getRoute() {
        return route;
    }

    /**
     * <p>addGeometry.</p>
     *
     * @param shapePoints a {@link java.util.List} object.
     */
    public void addGeometry(List<RouteShapePoint> shapePoints) {
        addGeometry(shapePoints, p -> new CoordinatePoint(p.getLat(), p.getLon()));
    }

    /**
     * <p>addGeometry.</p>
     *
     * @param feature a {@link org.geojson.Feature} object.
     */
    public void addGeometry(Feature feature) {
        String color = feature.getProperty("Color");
        if (color.startsWith("#")) {
            color = color.substring(1);
        }
        GeoJsonObject geometry = feature.getGeometry();
        if (geometry instanceof LineString) {
            addGeometry(((LineString) geometry).getCoordinates(),
                    p -> new CoordinatePoint(p.getLatitude(), p.getLongitude()), color);
        } else if (geometry instanceof MultiLineString) {
            for (List<LngLatAlt> coords : ((MultiLineString) geometry).getCoordinates()) {
                addGeometry(coords, p -> new CoordinatePoint(p.getLatitude(), p.getLongitude()), color);
            }
        } else {
            throw new RuntimeException("Unknown geometry type! " + geometry.getClass());
        }
    }

    private <T> void addGeometry(List<T> toTransform, Function<T, CoordinatePoint> transform) {
        addGeometry(toTransform, transform, route.getColor());
    }

    private <T> void addGeometry(List<T> toTransform, Function<T, CoordinatePoint> transform, String color) {
        List<CoordinatePoint> points = toTransform.stream()
                .map(transform)
                .collect(Collectors.toList());
        EncodedPolylineBean bean = PolylineEncoder.createEncodings(points);
        geometry.add(new RouteGeometry(color, bean.getPoints()));
    }
}

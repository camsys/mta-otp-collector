package com.camsys.shims.schedule.transformer.model;

public class RouteShapePoint {

    private String routeId;
    private int shapeSequence;
    private double lat;
    private double lon;

    public RouteShapePoint(String routeId, int shapeSequence, double lat, double lon) {
        this.routeId = routeId;
        this.shapeSequence = shapeSequence;
        this.lat = lat;
        this.lon = lon;
    }

    public String getRouteId() {
        return routeId;
    }

    public int getShapeSequence() {
        return shapeSequence;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}

package com.camsys.shims.schedule.transformer.model;

/**
 * <p>RouteShapePoint class.</p>
 *
 */
public class RouteShapePoint {

    private String routeId;
    private int shapeSequence;
    private double lat;
    private double lon;

    /**
     * <p>Constructor for RouteShapePoint.</p>
     *
     * @param routeId a {@link java.lang.String} object.
     * @param shapeSequence a int.
     * @param lat a double.
     * @param lon a double.
     */
    public RouteShapePoint(String routeId, int shapeSequence, double lat, double lon) {
        this.routeId = routeId;
        this.shapeSequence = shapeSequence;
        this.lat = lat;
        this.lon = lon;
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
     * <p>Getter for the field <code>shapeSequence</code>.</p>
     *
     * @return a int.
     */
    public int getShapeSequence() {
        return shapeSequence;
    }

    /**
     * <p>Getter for the field <code>lat</code>.</p>
     *
     * @return a double.
     */
    public double getLat() {
        return lat;
    }

    /**
     * <p>Getter for the field <code>lon</code>.</p>
     *
     * @return a double.
     */
    public double getLon() {
        return lon;
    }
}

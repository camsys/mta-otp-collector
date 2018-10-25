package com.camsys.shims.schedule.transformer.model;

/**
 * <p>RouteGeometry class.</p>
 *
 */
public class RouteGeometry {

    private String color;

    private String points;

    /**
     * <p>Getter for the field <code>color</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getColor() {
        return color;
    }

    /**
     * <p>Getter for the field <code>points</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPoints() {
        return points;
    }

    /**
     * <p>Constructor for RouteGeometry.</p>
     *
     * @param color a {@link java.lang.String} object.
     * @param points a {@link java.lang.String} object.
     */
    public RouteGeometry(String color, String points) {
        this.color = color;
        this.points = points;
    }
}

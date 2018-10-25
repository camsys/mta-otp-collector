package com.camsys.shims.schedule.transformer.model;

public class RouteGeometry {

    private String color;

    private String points;

    public String getColor() {
        return color;
    }

    public String getPoints() {
        return points;
    }

    public RouteGeometry(String color, String points) {
        this.color = color;
        this.points = points;
    }
}

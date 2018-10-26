package com.camsys.shims.schedule.transformer.model;

public class ExtendedRouteBranchStop extends RouteBranchStop {
    public ExtendedRouteBranchStop(RouteBranchStop rbs) {
        super(rbs);
    }

    private double lat;

    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

package com.camsys.shims.schedule.transformer.model;

public class RouteBranchStop {
    private String id; // GTFS Stop Id
    private String locationName; // aka GTFS Stop Name
    private String lineName; // aka branch
    private String routeId; // GTFS Route Id -- the filter param
    private String locationIndex; // optional and can be .5

    public RouteBranchStop(String stopId, String locationName, String lineName, String routeId, String locationIndex) {
        this.id = stopId;
        this.locationName = locationName;
        this.lineName = lineName;
        this.routeId = routeId;
        this.locationIndex = locationIndex;
    }

    public RouteBranchStop(String stopId, String locationName, String lineName, String routeId) {
        this.id = stopId;
        this.locationName = locationName;
        this.lineName = lineName;
        this.routeId = routeId;
    }

    public String getId() {
        return id;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLineName() {
        return lineName;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getLocationIndex() {
        return locationIndex;
    }
}

package com.camsys.shims.schedule.transformer.model;

/**
 * <p>RouteBranchStop class.</p>
 *
 */
public class RouteBranchStop {
    private String id; // GTFS Stop Id
    private String locationName; // aka GTFS Stop Name
    private String lineName; // aka branch
    private String routeId; // GTFS Route Id -- the filter param
    private String locationIndex; // optional and can be .5

    /**
     * <p>Constructor for RouteBranchStop.</p>
     *
     * @param stopId a {@link java.lang.String} object.
     * @param locationName a {@link java.lang.String} object.
     * @param lineName a {@link java.lang.String} object.
     * @param routeId a {@link java.lang.String} object.
     * @param locationIndex a {@link java.lang.String} object.
     */
    public RouteBranchStop(String stopId, String locationName, String lineName, String routeId, String locationIndex) {
        this.id = stopId;
        this.locationName = locationName;
        this.lineName = lineName;
        this.routeId = routeId;
        this.locationIndex = locationIndex;
    }

    /**
     * <p>Constructor for RouteBranchStop.</p>
     *
     * @param stopId a {@link java.lang.String} object.
     * @param locationName a {@link java.lang.String} object.
     * @param lineName a {@link java.lang.String} object.
     * @param routeId a {@link java.lang.String} object.
     */
    public RouteBranchStop(String stopId, String locationName, String lineName, String routeId) {
        this.id = stopId;
        this.locationName = locationName;
        this.lineName = lineName;
        this.routeId = routeId;
    }

    /**
     * <p>Constructor for RouteBranchStop.</p>
     *
     * @param rbs a {@link com.camsys.shims.schedule.transformer.model.RouteBranchStop} object.
     */
    public RouteBranchStop(RouteBranchStop rbs) {
        this.id = rbs.getId();
        this.locationName = rbs.locationName;
        this.lineName = rbs.lineName;
        this.routeId = rbs.routeId;
        this.locationIndex = rbs.locationIndex;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>locationName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * <p>Getter for the field <code>lineName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLineName() {
        return lineName;
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
     * <p>Getter for the field <code>locationIndex</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLocationIndex() {
        return locationIndex;
    }
}

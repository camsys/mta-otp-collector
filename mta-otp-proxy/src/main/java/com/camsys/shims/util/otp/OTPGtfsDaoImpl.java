/**
 * Copyright (C) 2019 Cambridge Systematics, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.camsys.shims.util.otp;

import com.camsys.shims.util.gtfs.GtfsAndCalendar;
import com.vividsolutions.jts.geom.LineString;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.ShapePoint;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.Trip;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.opentripplanner.routing.edgetype.TripPattern;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.services.GraphService;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OTPGtfsDaoImpl implements GtfsAndCalendar {

    private GraphService _graphService;

    private String _feedId;

    public void setGraphService(GraphService graphService) {
        _graphService = graphService;
    }

    public void setFeedId(String feedId) {
        _feedId = feedId;
    }

    private Graph getGraph() {
        return _graphService.getRouter().graph;
    }

    // This is not indexed. Perhaps it should be.
    @Override
    public Collection<Route> getAllRoutes() {
        Collection<Route> allRoutes = getGraph().index.routeForId.values();
        Collection<Agency> feedAgencies = getGraph().getAgencies(_feedId);
        Set<Route> routes = new HashSet<>();
        for (Route route : allRoutes) {
            if (feedAgencies.contains(route.getAgency())) {
                routes.add(route);
            }
        }
        return routes;
    }

    @Override
    public Route getRouteForId(AgencyAndId agencyAndId) {
        return getGraph().index.routeForId.get(agencyAndId);
    }

    @Override
    public List<Trip> getTripsForRoute(Route route) {
        return getGraph().index.patternsForRoute.get(route).stream()
                .flatMap(pattern -> pattern.getTrips().stream())
                .collect(Collectors.toList());
    }

    @Override
    public List<Stop> getStopsForTrip(Trip trip) {
        TripPattern pattern = getGraph().index.getTripPatternForTripId(trip.getId());
        return pattern.getStops();
    }

    // not indexed
    @Override
    public Collection<Stop> getAllStops() {
        Collection<Stop> allStops = getGraph().index.stopForId.values();
        Set<Stop> stops = new HashSet<>();
        for (Stop stop : allStops) {
            if (stop.getId().getAgencyId().equals(_feedId)) {
                stops.add(stop);
            }
        }
        return stops;
    }

    @Override
    public Set<AgencyAndId> getServiceIdsForDate(ServiceDate serviceDate) {
        return getGraph().getCalendarService().getServiceIdsOnDate(serviceDate);
    }

    @Override
    public LineString getGeometryForTrip(Trip trip) {
        TripPattern pattern = getGraph().index.patternForTrip.get(trip);
        return pattern.geometry;
    }

    @Override
    public Trip getTripForId(AgencyAndId id) {
        return getGraph().index.tripForId.get(id);
    }
}

package com.camsys.shims.factory;

import org.joda.time.DateTime;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UpdateableGtfsRelationalDao extends GtfsRelationalDaoImpl {
    private static Logger _log = LoggerFactory.getLogger(UpdateableGtfsRelationalDao.class);

    private String _gtfsPath;

    private int _blockedRequestCounter = 0;

    private boolean _isLoading = false;

    public void setGtfsPath(String path) { _gtfsPath = path; }
    public String getGtfsPath() { return _gtfsPath; }

    GtfsRelationalDaoImpl _prime;
    GtfsRelationalDaoImpl _secondary;

    public void load(){
        _isLoading = true;

        File file = new File(_gtfsPath);

        _log.info("Is loading a DAO for path {} with file size {} at time {}", _gtfsPath, file.length(), DateTime.now().toLocalTime().toString("HH:mm:ss"));

        loadDao(_prime, file);

        _log.info("Is done loading a DAO for path {} at time {}", _gtfsPath, DateTime.now().toLocalTime().toString("HH:mm:ss"));

        _isLoading = false;

        loadDao(_secondary, file);

    }

    private void loadDao(GtfsRelationalDaoImpl daoToReload, File file)
    {
        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(daoToReload);
        try {
            reader.setInputLocation(file);
            reader.run();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }
    }


    public List<String> getTripAgencyIdsReferencingServiceId(AgencyAndId serviceId) {
        if(_isLoading)
        {
            return _secondary.getTripAgencyIdsReferencingServiceId(serviceId);
        }
        return _prime.getTripAgencyIdsReferencingServiceId(serviceId);
    }

    public List<Route> getRoutesForAgency(Agency agency) {
        if(_isLoading)
        {
            return _secondary.getRoutesForAgency(agency);
        }
        return _prime.getRoutesForAgency(agency);
    }

    public List<Stop> getStopsForStation(Stop station) {
        if(_isLoading)
        {
            return _secondary.getStopsForStation(station);
        }
        return _prime.getStopsForStation(station);
    }

    public List<AgencyAndId> getAllShapeIds() {
        if(_isLoading)
        {
            return _secondary.getAllShapeIds();
        }
        return _prime.getAllShapeIds();
    }

    public List<ShapePoint> getShapePointsForShapeId(AgencyAndId shapeId) {
        if(_isLoading)
        {
            return _secondary.getShapePointsForShapeId(shapeId);
        }
        return _prime.getShapePointsForShapeId(shapeId);
    }

    public List<StopTime> getStopTimesForTrip(Trip trip) {
        if(_isLoading)
        {
            return _secondary.getStopTimesForTrip(trip);
        }
        return _prime.getStopTimesForTrip(trip);
    }

    public List<StopTime> getStopTimesForStop(Stop stop) {
        if(_isLoading)
        {
            return _secondary.getStopTimesForStop(stop);
        }
        return _prime.getStopTimesForStop(stop);
    }

    public List<Trip> getTripsForRoute(Route route) {
        if(_isLoading)
        {
            return _secondary.getTripsForRoute(route);
        }
        return _prime.getTripsForRoute(route);
    }

    public List<Trip> getTripsForShapeId(AgencyAndId shapeId) {
        if(_isLoading)
        {
            return _secondary.getTripsForShapeId(shapeId);
        }
        return _prime.getTripsForShapeId(shapeId);
    }

    public List<Trip> getTripsForServiceId(AgencyAndId serviceId) {
        if(_isLoading)
        {
            return _secondary.getTripsForServiceId(serviceId);
        }
        return _prime.getTripsForServiceId(serviceId);
    }

    public List<Trip> getTripsForBlockId(AgencyAndId blockId) {
        if(_isLoading)
        {
            return _secondary.getTripsForBlockId(blockId);
        }
        return _prime.getTripsForBlockId(blockId);
    }

    public List<Frequency> getFrequenciesForTrip(Trip trip) {
        if(_isLoading)
        {
            return _secondary.getFrequenciesForTrip(trip);
        }
        return _prime.getFrequenciesForTrip(trip);
    }

    public List<AgencyAndId> getAllServiceIds() {
        if(_isLoading)
        {
            return _secondary.getAllServiceIds();
        }
        return _prime.getAllServiceIds();
    }

    public List<ServiceCalendarDate> getCalendarDatesForServiceId(AgencyAndId serviceId) {
        if(_isLoading)
        {
            return _secondary.getCalendarDatesForServiceId(serviceId);
        }
        return _prime.getCalendarDatesForServiceId(serviceId);
    }

    public ServiceCalendar getCalendarForServiceId(AgencyAndId serviceId) {
        if(_isLoading)
        {
            return _secondary.getCalendarForServiceId(serviceId);
        }
        return _prime.getCalendarForServiceId(serviceId);
    }

    public List<FareRule> getFareRulesForFareAttribute(FareAttribute fareAttribute) {
        if(_isLoading)
        {
            return _secondary.getFareRulesForFareAttribute(fareAttribute);
        }
        return _prime.getFareRulesForFareAttribute(fareAttribute);
    }
}

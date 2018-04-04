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

    public void setGtfsPath(String path) { _gtfsPath = path; }
    public String getGtfsPath() { return _gtfsPath; }

    GtfsRelationalDaoImpl _dao = new GtfsRelationalDaoImpl();
    public GtfsRelationalDaoImpl getRelaodedDao(){
        return _dao;
    }
    public void setReloadedDao(GtfsRelationalDaoImpl dao)
    {
        _dao = dao;
    }


    public void load(){
        File file = new File(_gtfsPath);

        _log.info("Is loading a DAO for path {} with file size {} at time {}", _gtfsPath, file.length(), DateTime.now().toLocalTime().toString("HH:mm:ss"));

        loadDao(file);

        _log.info("Is done loading a DAO for path {} at time {}", _gtfsPath, DateTime.now().toLocalTime().toString("HH:mm:ss"));
    }

    private void loadDao(File file)
    {
        GtfsRelationalDaoImpl dao = new GtfsRelationalDaoImpl();
        GtfsReader reader = new GtfsReader();

        reader.setEntityStore(dao);

        try {
            reader.setInputLocation(file);
            reader.run();
            reader.close();

            setReloadedDao(dao);
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }
    }

    public List<String> getTripAgencyIdsReferencingServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getTripAgencyIdsReferencingServiceId(serviceId);
    }

    public List<Route> getRoutesForAgency(Agency agency) {
        return getRelaodedDao().getRoutesForAgency(agency);
    }

    public List<Stop> getStopsForStation(Stop station) {
        return getRelaodedDao().getStopsForStation(station);
    }

    public List<AgencyAndId> getAllShapeIds() {
        return getRelaodedDao().getAllShapeIds();
    }

    public List<ShapePoint> getShapePointsForShapeId(AgencyAndId shapeId) {
        return getRelaodedDao().getShapePointsForShapeId(shapeId);
    }

    public List<StopTime> getStopTimesForTrip(Trip trip) {
        return getRelaodedDao().getStopTimesForTrip(trip);
    }

    public List<StopTime> getStopTimesForStop(Stop stop) {
        return getRelaodedDao().getStopTimesForStop(stop);
    }

    public List<Trip> getTripsForRoute(Route route) {
        return getRelaodedDao().getTripsForRoute(route);
    }

    public List<Trip> getTripsForShapeId(AgencyAndId shapeId) {
        return getRelaodedDao().getTripsForShapeId(shapeId);
    }

    public List<Trip> getTripsForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getTripsForServiceId(serviceId);
    }

    public List<Trip> getTripsForBlockId(AgencyAndId blockId) {
        return getRelaodedDao().getTripsForBlockId(blockId);
    }

    public List<Frequency> getFrequenciesForTrip(Trip trip) {
        return getRelaodedDao().getFrequenciesForTrip(trip);
    }

    public List<AgencyAndId> getAllServiceIds() {
        return getRelaodedDao().getAllServiceIds();
    }

    public List<ServiceCalendarDate> getCalendarDatesForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getCalendarDatesForServiceId(serviceId);
    }

    public ServiceCalendar getCalendarForServiceId(AgencyAndId serviceId) {
        return getRelaodedDao().getCalendarForServiceId(serviceId);
    }

    public List<FareRule> getFareRulesForFareAttribute(FareAttribute fareAttribute) {
        return getRelaodedDao().getFareRulesForFareAttribute(fareAttribute);
    }

}

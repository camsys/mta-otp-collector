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

    public void load(){
        _isLoading = true;

        File file = new File(_gtfsPath);

        _log.info("Is loading a DAO for path {} with file size {} at time {}", _gtfsPath, file.length(), DateTime.now().toLocalTime().toString("HH:mm:ss"));

        GtfsReader reader = new GtfsReader();
        reader.setEntityStore(this);
        try {
            reader.setInputLocation(file);
            reader.run();
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Failure while reading GTFS", e);
        }

        _log.info("Is done loading a DAO for path {} at time {}", _gtfsPath, DateTime.now().toLocalTime().toString("HH:mm:ss"));

        _isLoading = false;
    }

    /**
     * This method blocks until the bundle is ready--this method is called as part of the proxy to each of the underlying
     * methods of the TDS and NYC-TDS below to ensure all calls to those bundle-backed methods succeed (i.e. the bundle is ready
     * to be queried.)
     */
    private void blockUntilBundleIsReady() {
        try {
            while(_isLoading) {
                _blockedRequestCounter++;

                // only print this every 25 times so we don't fill up the logs!
                if(_blockedRequestCounter > 25) {
                    _log.warn("Dao is not ready we've blocked 25 requests for this dao since last log event.");
                    _blockedRequestCounter = 0;
                }

                synchronized(this) {
                    Thread.sleep(250);
                    Thread.yield();
                }
            }
        } catch(InterruptedException e) {
            return;
        }
    }

    public List<String> getTripAgencyIdsReferencingServiceId(AgencyAndId serviceId) {
        blockUntilBundleIsReady();
        return super.getTripAgencyIdsReferencingServiceId(serviceId);
    }

    public List<Route> getRoutesForAgency(Agency agency) {
        blockUntilBundleIsReady();
        return super.getRoutesForAgency(agency);
    }

    public List<Stop> getStopsForStation(Stop station) {
        blockUntilBundleIsReady();
        return super.getStopsForStation(station);
    }

    public List<AgencyAndId> getAllShapeIds() {
        blockUntilBundleIsReady();
        return super.getAllShapeIds();
    }

    public List<ShapePoint> getShapePointsForShapeId(AgencyAndId shapeId) {
        blockUntilBundleIsReady();
        return super.getShapePointsForShapeId(shapeId);
    }

    public List<StopTime> getStopTimesForTrip(Trip trip) {
        blockUntilBundleIsReady();
        return super.getStopTimesForTrip(trip);
    }

    public List<StopTime> getStopTimesForStop(Stop stop) {
        blockUntilBundleIsReady();
        return super.getStopTimesForStop(stop);
    }

    public List<Trip> getTripsForRoute(Route route) {
        blockUntilBundleIsReady();
        return super.getTripsForRoute(route);
    }

    public List<Trip> getTripsForShapeId(AgencyAndId shapeId) {
        blockUntilBundleIsReady();
        return super.getTripsForShapeId(shapeId);
    }

    public List<Trip> getTripsForServiceId(AgencyAndId serviceId) {
        blockUntilBundleIsReady();
        return super.getTripsForServiceId(serviceId);
    }

    public List<Trip> getTripsForBlockId(AgencyAndId blockId) {
        blockUntilBundleIsReady();
        return super.getTripsForBlockId(blockId);
    }

    public List<Frequency> getFrequenciesForTrip(Trip trip) {
        blockUntilBundleIsReady();
        return super.getFrequenciesForTrip(trip);
    }

    public List<AgencyAndId> getAllServiceIds() {
        blockUntilBundleIsReady();
        return super.getAllServiceIds();
    }

    public List<ServiceCalendarDate> getCalendarDatesForServiceId(AgencyAndId serviceId) {
        blockUntilBundleIsReady();
        return super.getCalendarDatesForServiceId(serviceId);
    }

    public ServiceCalendar getCalendarForServiceId(AgencyAndId serviceId) {
        blockUntilBundleIsReady();
        return super.getCalendarForServiceId(serviceId);
    }

    public List<FareRule> getFareRulesForFareAttribute(FareAttribute fareAttribute) {
        blockUntilBundleIsReady();
        return super.getFareRulesForFareAttribute(fareAttribute);
    }
}

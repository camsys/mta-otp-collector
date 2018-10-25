package com.camsys.shims.factory;

import org.joda.time.DateTime;
import org.onebusaway.gtfs.impl.GenericDaoImpl;
import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.impl.MultipleCalendarsForServiceIdException;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
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

    public void clearAllCaches() {
        getRelaodedDao().clearAllCaches();
    }

    public boolean isPackStopTimes() {
        return getRelaodedDao().isPackStopTimes();
    }

    public void setPackStopTimes(boolean packStopTimes) {
        getRelaodedDao().setPackStopTimes(packStopTimes);
    }

    public boolean isPackShapePoints() {
        return getRelaodedDao().isPackShapePoints();
    }

    public void setPackShapePoints(boolean packShapePoints) {
        getRelaodedDao().setPackShapePoints(packShapePoints);
    }

    public Agency getAgencyForId(String id) {
        return (Agency)getRelaodedDao().getEntityForId(Agency.class, id);
    }

    public Collection<Agency> getAllAgencies() {
        return getRelaodedDao().getAllEntitiesForType(Agency.class);
    }

    public Collection<Block> getAllBlocks() {
        return getRelaodedDao().getAllEntitiesForType(Block.class);
    }

    public Collection<ServiceCalendarDate> getAllCalendarDates() {
        return getRelaodedDao().getAllEntitiesForType(ServiceCalendarDate.class);
    }

    public Collection<ServiceCalendar> getAllCalendars() {
        return getRelaodedDao().getAllEntitiesForType(ServiceCalendar.class);
    }

    public Collection<FareAttribute> getAllFareAttributes() {
        return getRelaodedDao().getAllEntitiesForType(FareAttribute.class);
    }

    public Collection<FareRule> getAllFareRules() {
        return getRelaodedDao().getAllEntitiesForType(FareRule.class);
    }

    public Collection<FeedInfo> getAllFeedInfos() {
        return getRelaodedDao().getAllEntitiesForType(FeedInfo.class);
    }

    public Collection<Frequency> getAllFrequencies() {
        return getRelaodedDao().getAllEntitiesForType(Frequency.class);
    }

    public Collection<Route> getAllRoutes() {
        return getRelaodedDao().getAllEntitiesForType(Route.class);
    }

    public Collection<ShapePoint> getAllShapePoints() {
        return getRelaodedDao().getAllShapePoints();
    }

    public Collection<StopTime> getAllStopTimes() {
        return getRelaodedDao().getAllStopTimes();
    }

    public Collection<Stop> getAllStops() {
        return getRelaodedDao().getAllStops();
    }

    public Collection<Transfer> getAllTransfers() {
        return getRelaodedDao().getAllTransfers();
    }

    public Collection<Trip> getAllTrips() {
        return getRelaodedDao().getAllTrips();
    }

    public Block getBlockForId(int id) {
        return getRelaodedDao().getBlockForId(id);
    }

    public ServiceCalendarDate getCalendarDateForId(int id) {
        return getRelaodedDao().getCalendarDateForId(id);
    }

    public ServiceCalendar getCalendarForId(int id) {
        return getRelaodedDao().getCalendarForId(id);
    }

    public FareAttribute getFareAttributeForId(AgencyAndId id) {
        return getRelaodedDao().getFareAttributeForId(id);
    }

    public FareRule getFareRuleForId(int id) {
        return getRelaodedDao().getFareRuleForId(id);
    }

    public FeedInfo getFeedInfoForId(String id) {
        return (FeedInfo)this.getEntityForId(FeedInfo.class, id);
    }

    public Frequency getFrequencyForId(int id) {
        return (Frequency)this.getEntityForId(Frequency.class, id);
    }

    public Collection<Pathway> getAllPathways() {
        return this.getAllEntitiesForType(Pathway.class);
    }

    public Pathway getPathwayForId(AgencyAndId id) {
        return (Pathway)this.getEntityForId(Pathway.class, id);
    }

    public Route getRouteForId(AgencyAndId id) {
        return (Route)this.getEntityForId(Route.class, id);
    }

    public ShapePoint getShapePointForId(int id) {
        return getRelaodedDao().getShapePointForId(id);
    }

    public Stop getStopForId(AgencyAndId id) {
        return getRelaodedDao().getStopForId(id);
    }

    public StopTime getStopTimeForId(int id) {
        return getRelaodedDao().getStopTimeForId(id);
    }

    public Transfer getTransferForId(int id) {
        return getRelaodedDao().getTransferForId(id);
    }

    public Trip getTripForId(AgencyAndId id) {
        return getRelaodedDao().getTripForId(id);
    }

    public <K, V> Map<K, V> getEntitiesByIdForEntityType(Class<K> keyType, Class<V> entityType) {
        return getRelaodedDao().getEntitiesByIdForEntityType(keyType, entityType);
    }

    public <T> Collection<T> getAllEntitiesForType(Class<T> type) {
        return getRelaodedDao().getAllEntitiesForType(type);
    }

    public <T> T getEntityForId(Class<T> type, Serializable id) {
        return getRelaodedDao().getEntityForId(type, id);
    }

    public void saveEntity(Object entity) {
        getRelaodedDao().saveEntity(entity);
    }

    public <T> void clearAllEntitiesForType(Class<T> type) {
        getRelaodedDao().clearAllEntitiesForType(type);
    }

    public <K extends Serializable, T extends IdentityBean<K>> void removeEntity(T entity) {
        getRelaodedDao().removeEntity(entity);
    }

    public void close() {
        getRelaodedDao().close();
    }

    public void setGenerateIds(boolean generateIds) {
        getRelaodedDao().setGenerateIds(generateIds);
    }

    public Set<Class<?>> getEntityClasses() {
        return getRelaodedDao().getEntityClasses();
    }

    public void clear() {
        getRelaodedDao().clear();
    }

    public void updateEntity(Object entity) {
    }

    public void saveOrUpdateEntity(Object entity) {
        IdentityBean<Serializable> bean = (IdentityBean)entity;
        Object existing = this.getEntityForId(entity.getClass(), bean.getId());
        if (existing != entity) {
            this.saveEntity(entity);
        }
    }


    public void open() {
    }

    public void flush() {
    }
}
